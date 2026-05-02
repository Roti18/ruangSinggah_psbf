package com.example.kosapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.kosapp.services.ActivityLogService;
import com.example.kosapp.services.SecureService;
import com.example.kosapp.services.OAuthUserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final OAuthUserService oauthUserService;
        private final SecureService secureService;
        private final ActivityLogService activityLogService;

        public SecurityConfig(
                OAuthUserService oauthUserService,
                SecureService secureService,
                ActivityLogService activityLogService
        ) {
                this.oauthUserService = oauthUserService;
                this.secureService = secureService;
                this.activityLogService = activityLogService;
        }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/ruangsinggahLogo.png"
                        )
                        .permitAll()
                        .anyRequest()
                        .permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .authorizationEndpoint(auth -> auth.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redir -> redir.baseUri("/login/oauth2/code/*"))
                        .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService()))
                        .successHandler(authenticationSuccessHandler())
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                                activityLogService.log("LOGOUT");
                                response.sendRedirect("/login");
                        })
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
                return (request, response, authentication) -> {
                        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                        secureService.bindSessionUser(request.getSession(true), oauthUser);
                        activityLogService.log("LOGIN");
                        response.sendRedirect("/");
                };
    }

        @Bean
        public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
                DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
                return userRequest -> {
                        OAuth2User oauthUser = delegate.loadUser(userRequest);
                        oauthUserService.upsertFromOAuth(oauthUser);
                        return oauthUser;
                };
        }
}
