package com.honglin.controller;

import com.honglin.common.CommonResponse;
import com.honglin.exceptions.KafkaFailureException;
import com.honglin.httpclient.blogClient;
import com.honglin.service.KafkaSender;
import com.honglin.vo.*;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@RestController
@Slf4j
public class UserController {

    private final RestTemplate restTemplate;

    @Autowired
    private blogClient blogClient;

    @Autowired
    private KafkaSender emailSender;

    @Autowired
    public UserController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * user login feature
     *
     * @param client_id
     * @param client_secret
     * @param credentials
     * @param bindingResult
     * @return
     */

    @PostMapping("/login")
    @HystrixCommand(fallbackMethod = "loginFallBack", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "20"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "6000"),
            @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
    })
    public CommonResponse<TokenInfo> login(@RequestParam(required = true, name = "client_id") String client_id,
                                           @RequestParam(required = true, name = "client_secret") String client_secret,
                                           @RequestBody @Valid Credentials credentials, BindingResult bindingResult) {
        Optional<Credentials> hasCredentials;
        try {
            hasCredentials = Optional.of(credentials);
        } catch (NullPointerException ex) {
            log.debug("empty credentials!");
            return new CommonResponse<>(HttpStatus.SC_INTERNAL_SERVER_ERROR, "empty credentials!");
        }

        if (bindingResult.hasErrors()) {
            if (bindingResult.getErrorCount() > 0) {
                List<FieldError> errorFields = bindingResult.getFieldErrors();
                for (FieldError fieldError : errorFields) {
                    return new CommonResponse<>(HttpStatus.SC_BAD_REQUEST, fieldError.getDefaultMessage());
                }
            }
        }

        String url = "http://auth-service/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(client_id, client_secret);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", hasCredentials.get().getUsername());
        params.add("password", hasCredentials.get().getPassword());
        params.add("grant_type", "password");
        params.add("scope", "read write");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        log.info(hasCredentials.get().getUsername() + " trying to login");
        try {
            ResponseEntity<TokenInfo> response = restTemplate.exchange(url, HttpMethod.POST, entity, TokenInfo.class);
            TokenInfo body = response.getBody();

            log.info(hasCredentials.get().getUsername() + " login success");

            return new CommonResponse<>(200, hasCredentials.get().getUsername() + " login success", body);
        } catch (HttpClientErrorException e) {
            return new CommonResponse<>(HttpStatus.SC_UNAUTHORIZED, "invalid credential");
        }
    }

    //fallback for login
    public CommonResponse<TokenInfo> loginFallBack(@RequestParam(required = true, name = "client_id") String client_id,
                                                   @RequestParam(required = true, name = "client_secret") String client_secret,
                                                   @RequestBody @Valid Credentials credentials, BindingResult bindingResult) {
        return new CommonResponse(HttpStatus.SC_NOT_FOUND, "login service not available");
    }


    /**
     * user register feature
     *
     * @param user
     * @param bindingResult
     * @return
     * @throws InterruptedException
     */

    @PostMapping("/register")
    @HystrixCommand(fallbackMethod = "createUserFallBack", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "20"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "6000"),
            @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
    })
    public CommonResponse createUser(@RequestBody @Valid UserDto user, BindingResult bindingResult) throws InterruptedException {

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);


        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch emailLatch = new CountDownLatch(2);
        Optional<UserDto> userDto;
        try {
            userDto = Optional.of(user);
        } catch (NullPointerException ex) {
            log.info("user is null, failed to create new user");
            return new CommonResponse(HttpStatus.SC_BAD_REQUEST, "user should not be NULL!");
        }
        if (bindingResult.hasErrors()) {
            if (bindingResult.getErrorCount() > 0) {
                List<FieldError> errorFields = bindingResult.getFieldErrors();
                for (FieldError fieldError : errorFields) {
                    return new CommonResponse<>(HttpStatus.SC_BAD_REQUEST, fieldError.getDefaultMessage());
                }
            }
        }

        String url = "http://auth-service/createNewUser";
        authDto autDto = new authDto();
        BeanUtils.copyProperties(userDto.get(), autDto);
        HttpEntity<authDto> request = new HttpEntity<>(autDto);


        Callable<Integer> callableFeedback = () -> {
            int result = restTemplate.postForObject(url, request, Integer.class);
            emailLatch.countDown();
            return result;
        };

        FutureTask<Integer> task1 = new FutureTask(callableFeedback);
        Thread t1 = new Thread(task1);
        t1.start();
        t1.join();
        Optional<Integer> feedback = null;
        try {
            if (task1.isDone()) {
                feedback = Optional.of(task1.get());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (feedback.get() != HttpStatus.SC_OK) {
            return new CommonResponse(HttpStatus.SC_BAD_REQUEST, userDto.get().getUsername() + " already exist");
        } else {
            log.info("User: " + userDto.get().getUsername() + " added to db in auth server!");
            blogUserDto user1 = new blogUserDto();
            BeanUtils.copyProperties(userDto.get(), user1);
            //call blog server
            Callable<Integer> callableResponse = () -> {
                int result = blogClient.sync(user1);
                latch.countDown();
                emailLatch.countDown();
                return result;
            };

            FutureTask<Integer> task2 = new FutureTask<>(callableResponse);
            Thread t2 = new Thread(task2);
            t2.start();
//            t2.join();
            latch.await();
            Optional<Integer> response = null;
            try {
                Thread.sleep(1000);
                if (task2.isDone()) {
                    response = Optional.of(task2.get(200, TimeUnit.MILLISECONDS));
                }
            } catch (ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }

            System.out.println(response.get());
            if (response.get() != 200) {
                log.error("Error happens at blog server, rollback");
                String deleteUrl = "http://auth-service/deleteUser";
                try {

                    Integer deleteReponse = restTemplate.postForObject(deleteUrl, request, Integer.class);
                    if (deleteReponse == 200) {
                        log.info(userDto.get().getUsername() + " remove from auth server db");
                    } else {
                        log.error("Unable to remove " + userDto.get().getUsername() + " from auth server db");
                    }
                    return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, userDto.get().getUsername() + " failed to register");
                } catch (HttpClientErrorException ex) {
                    return new CommonResponse<>(HttpStatus.SC_METHOD_FAILURE, "transaction at auth server failed");
                }
            } else { //success
                emailLatch.await();
                try {
                    EmailDto emailDto = new EmailDto();
                    emailDto.setUsername(userDto.get().getUsername());
                    emailDto.setEmail(userDto.get().getEmail());
                    emailDto.setFirstname(userDto.get().getFirstname());
                    emailDto.setLastname(userDto.get().getLastname());
                    emailSender.send(emailDto);
                } catch (KafkaFailureException ex) {
                    return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, "fail to send email request to kafka");
                }
            }
            log.info("User: " + userDto.get().getUsername() + " register success!");
        }
        return new CommonResponse(HttpStatus.SC_OK, userDto.get().getUsername() + " register success!");
    }

    //fallback for register
    public CommonResponse createUserFallBack(@RequestBody @Valid UserDto user, BindingResult bindingResult) {
        return new CommonResponse(HttpStatus.SC_NOT_FOUND, "register service not available");
    }

    /**
     * change password for users
     *
     * @param changePasswordVO
     * @param username
     * @return
     */
    @PostMapping("/changePassword")
    @HystrixCommand(fallbackMethod = "changePasswordFallBack", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "20"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "6000"),
            @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
    })
    public CommonResponse changePassword(@RequestBody ChangePasswordVO changePasswordVO, @AuthenticationPrincipal String username) {
        String url = "http://auth-service/changePassword?username=" + username;
        try {
            log.info(username + " is trying to change password");
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
            messageConverters.add(new FormHttpMessageConverter());
            messageConverters.add(new StringHttpMessageConverter());
            messageConverters.add(new MappingJackson2HttpMessageConverter());
            restTemplate.setMessageConverters(messageConverters);

            Integer response = restTemplate.postForObject(url, changePasswordVO, Integer.class);
//            System.out.println(response);
            log.info(username + " already communicate with auth server");
            if (response != 200) {
                return new CommonResponse(HttpStatus.SC_NOT_ACCEPTABLE, "Password not match");
            }
        } catch (HttpClientErrorException ex) {
            return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, "failed to call auth server");
        }

        return new CommonResponse(HttpStatus.SC_OK, username + " change password success!");
    }

    //fallback for change password
    public CommonResponse changePasswordFallBack(@RequestBody ChangePasswordVO changePasswordVO, @AuthenticationPrincipal String username) {
        return new CommonResponse(HttpStatus.SC_NOT_FOUND, "change password service not available");
    }
}
