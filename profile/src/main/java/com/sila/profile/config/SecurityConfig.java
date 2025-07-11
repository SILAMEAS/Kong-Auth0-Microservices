package com.sila.profile.config;

import com.silacode.config.ProfileProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final ProfileProperties profileProperties;

  private final ClientRegistrationRepository clientRegistrationRepository;

  private final JwtDecoder jwtDecoder;

  public SecurityConfig(
      ProfileProperties profileProperties,
      ClientRegistrationRepository clientRegistrationRepository,
      JwtDecoder jwtDecoder) {
    this.profileProperties = profileProperties;
    this.clientRegistrationRepository = clientRegistrationRepository;
    this.jwtDecoder = jwtDecoder;
  }

  @Bean
  public OAuth2AuthorizedClientRepository repository() {
    return new HttpSessionOAuth2AuthorizedClientRepository();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth -> auth.requestMatchers("/public/**").permitAll().anyRequest().authenticated())
        .oauth2Login(
            oauth2 ->
                oauth2.successHandler(
                    (request, response, authentication) -> {
                      var accessToken =
                          repository()
                              .loadAuthorizedClient("auth0", authentication, request)
                              .getAccessToken()
                              .getTokenValue();

                      response.sendRedirect(
                          profileProperties.getFrontendRedirectUrl()
                              + "?access_token="
                              + accessToken);
                    }))
        .logout(
            logout -> {
              logout.logoutRequestMatcher(
                  PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/logout"));
              LogoutHandler cookieClearing = new CookieClearingLogoutHandler("*");
              SecurityContextLogoutHandler contextLogout = new SecurityContextLogoutHandler();
              CompositeLogoutHandler composite =
                  new CompositeLogoutHandler(cookieClearing, contextLogout);

              OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                  new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
              oidcLogoutSuccessHandler.setDefaultTargetUrl(
                  profileProperties.getLogoutRedirectUrl());

              logout.addLogoutHandler(composite).logoutSuccessHandler(oidcLogoutSuccessHandler);
            })
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)));

    return http.build();
  }

  @Bean
  public OAuth2AuthorizationRequestResolver authRequestResolver() {
    var defaultResolver =
        new DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository, "/oauth2/authorization");

    defaultResolver.setAuthorizationRequestCustomizer(
        customizer ->
            customizer.additionalParameters(
                params -> params.put("audience", profileProperties.getAudience())));

    return defaultResolver;
  }

  @Bean
  public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>
      accessTokenResponseClient() {
    var client = new RestClientAuthorizationCodeTokenResponseClient();
    client.setParametersCustomizer(
        params -> params.add("audience", profileProperties.getAudience()));
    return client;
  }
}
