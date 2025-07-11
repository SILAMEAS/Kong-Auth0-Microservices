package com.sila.restaurant;

import com.silacode.config.EnableAuth0Security;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAuth0Security
public class RestaurantApplication {

  public static void main(String[] args) {
    SpringApplication.run(RestaurantApplication.class, args);
  }
}
