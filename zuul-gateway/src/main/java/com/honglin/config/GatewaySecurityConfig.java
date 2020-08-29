package com.honglin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class GatewaySecurityConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(" /token/**", "/token/oauth/token", "/user/register", "/user/login", "/blog/users/searchAllUsers", "/blog/blogs/**",
                        "/blog/comments/getListOfComments").permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        try {
            resources.resourceId("zuul-gateway").tokenStore(tokenStore());
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        Resource resource = new ClassPathResource("public.txt");
        String publicKey;
        try {
            publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        converter.setVerifier(new RsaVerifier(publicKey));
        return converter;
    }
}
