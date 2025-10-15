package com.bowling.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient bowlingBallWebClient() {
        return WebClient.builder()
                .baseUrl("http://bowlingball-service:8080")
                .build();
    }

    @Bean
    public WebClient shoeWebClient() {
        return WebClient.builder()
                .baseUrl("http://shoe-service:8080")
                .build();
    }

    @Bean
    public WebClient laneWebClient() {
        return WebClient.builder()
                .baseUrl("http://lane-service:8080")
                .build();
    }

    @Bean
    public WebClient transactionWebClient() {
        return WebClient.builder()
                .baseUrl("http://transaction-service:8080")
                .build();
    }

}



