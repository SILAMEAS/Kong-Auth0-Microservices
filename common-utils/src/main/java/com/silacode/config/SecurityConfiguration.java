package com.silacode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

public class SecurityConfiguration {

  private final ProfileProperties profileProperties;

  public SecurityConfiguration(ProfileProperties profileProperties) {
    this.profileProperties = profileProperties;
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder decoder = JwtDecoders.fromIssuerLocation(profileProperties.getIssuerUri());

    OAuth2TokenValidator<Jwt> audienceValidator =
        new AudienceValidator(profileProperties.getAudience());
    OAuth2TokenValidator<Jwt> withIssuer =
        JwtValidators.createDefaultWithIssuer(profileProperties.getIssuerUri());
    OAuth2TokenValidator<Jwt> withAudience =
        new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

    decoder.setJwtValidator(withAudience);
    return decoder;
  }
}
