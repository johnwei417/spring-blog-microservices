package com.honglin.controller;

import com.honglin.common.CommonResponse;
import com.honglin.exceptions.DuplicateUserException;
import com.honglin.exceptions.KafkaFailureException;
import com.honglin.httpclient.blogClient;
import com.honglin.service.KafkaSender;
import com.honglin.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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
import java.util.List;

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

    @PostMapping("/login")
    public CommonResponse<TokenInfo> login(@RequestParam(required = true, name = "client_id") String client_id,
                                           @RequestParam(required = true, name = "client_secret") String client_secret,
                                           @RequestBody @Valid Credentials credentials, BindingResult bindingResult) {

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
        params.add("username", credentials.getUsername());
        params.add("password", credentials.getPassword());
        params.add("grant_type", "password");
        params.add("scope", "read write");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        log.info(credentials.getUsername() + " trying to login");
        try {
            ResponseEntity<TokenInfo> response = restTemplate.exchange(url, HttpMethod.POST, entity, TokenInfo.class);
            TokenInfo body = response.getBody();

            log.info(credentials.getUsername() + " login success");

            return new CommonResponse<>(200, credentials.getUsername() + " login success", body);
        } catch (HttpClientErrorException e) {
            return new CommonResponse<>(HttpStatus.SC_UNAUTHORIZED, "invalid credential");
        }
    }

    @PostMapping("/register")
    public CommonResponse createUser(@RequestBody @Valid UserDto user, BindingResult bindingResult) {
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
        BeanUtils.copyProperties(user, autDto);
        HttpEntity<authDto> request = new HttpEntity<>(autDto);
        try {
            restTemplate.postForObject(url, request, CommonResponse.class);
            log.info("User: " + user.getUsername() + " added to db in auth server!");
        } catch (HttpClientErrorException ex) {
            log.debug(user.getUsername() + " already exist");
            throw new DuplicateUserException(user.getUsername() + " already exist");
        }

        blogUserDto user1 = new blogUserDto();
        BeanUtils.copyProperties(user, user1);
        Integer response = blogClient.sync(user1);

        if (response != 200) {
            log.error("Error happens at blog server, rollback");
            String deleteUrl = "http://auth-service/deleteUser";
            try {
                restTemplate.postForObject(deleteUrl, request, CommonResponse.class);
                log.info(user.getUsername() + " remove from auth server db");
                return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, user.getUsername() + " failed to register");
            } catch (HttpClientErrorException ex) {
                return new CommonResponse<>(HttpStatus.SC_METHOD_FAILURE, "transaction at auth server failed");
            }
        } else {
            try {
                EmailDto emailDto = new EmailDto();
                emailDto.setUsername(user.getUsername());
                emailDto.setEmail(user.getEmail());
                emailDto.setFirstname(user.getFirstname());
                emailDto.setLastname(user.getLastname());
                emailSender.send(emailDto);
            } catch (KafkaFailureException ex) {
                return new CommonResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, "fail to send email request to kafka");
            }
        }
        log.info("User: " + user.getUsername() + " register success!");

        return new CommonResponse(HttpStatus.SC_OK, user.getUsername() + " register success!");
    }
}
