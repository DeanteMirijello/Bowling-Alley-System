package com.bowling.transaction.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class ServiceClientsConfig {
    private ServiceDetails bowlingballService;
    private ServiceDetails laneService;
    private ServiceDetails shoeService;

    @Data
    public static class ServiceDetails {
        private String host;
        private String port;
    }
}

