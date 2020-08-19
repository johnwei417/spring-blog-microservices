package com.honglin.config;

import com.honglin.service.impl.UserDetailImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;


@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailImpl userServiceDetail;


    @Bean
    public TokenStore tokenStore() {
        //return new JdbcTokenStore(dataSource);
        return new JwtTokenStore(jwtTokenEnhancer());
    }

    @Bean
    public JwtAccessTokenConverter jwtTokenEnhancer() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource("myblog.jks"), "wei000311".toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("myblog"));
        return converter;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore())
                .tokenEnhancer(jwtTokenEnhancer())
                .userDetailsService(userServiceDetail)
                .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("authService")
                .secret(passwordEncoder.encode("jojowei"))
                .accessTokenValiditySeconds(3600)
                .scopes("read", "write")
                .resourceIds("auth-service")
                .and()
                .withClient("blogService")
                .secret(passwordEncoder.encode("jojowei"))
                .authorizedGrantTypes("password")
                .scopes("read", "write")
                .accessTokenValiditySeconds(3600)
                .resourceIds("blog-service")
                .and()
                .withClient("gateway")
                .secret(passwordEncoder.encode("jojowei"))
                .authorizedGrantTypes("password")
                .scopes("read", "write")
                .accessTokenValiditySeconds(3600)
                .resourceIds("zuul-gateway")
                .and()
                .withClient("userService")
                .secret(passwordEncoder.encode("jojowei"))
                .authorizedGrantTypes("password")
                .scopes("read", "write")
                .accessTokenValiditySeconds(3600)
                .resourceIds("user-service")
                .and()
                .withClient("iphone")
                .secret(passwordEncoder.encode("jojowei"))
                .authorizedGrantTypes("password")
                .scopes("read", "write")
                .accessTokenValiditySeconds(3600)
                .resourceIds()
                .and()
                .withClient("browser")
                .secret(passwordEncoder.encode("jojowei"))
                .authorizedGrantTypes("password")
                .scopes("read", "write")
                .accessTokenValiditySeconds(3600)
                .resourceIds();
    }

}
