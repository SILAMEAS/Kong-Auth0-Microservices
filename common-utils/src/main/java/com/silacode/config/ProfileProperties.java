package com.silacode.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "custom.auth0")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileProperties {
  private String audience;

  private String issuerUri;

  private String logoutRedirectUrl;

  private String frontendRedirectUrl;
}
