package com.Hyunsoo.PickYouth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PickYouthApplication {

  public static void main(String[] args) {
    SpringApplication.run(PickYouthApplication.class, args);
  }
}
