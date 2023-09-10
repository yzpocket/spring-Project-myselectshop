package com.sparta.myselectshop.naver.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "naver.client")
public class ApiKey {
    private String id;
    private String secret;
}