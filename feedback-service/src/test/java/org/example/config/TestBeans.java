package org.example.config;


import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.testcontainers.containers.MongoDBContainer;

import static org.mockito.Mockito.mock;

@Configuration
public class TestBeans {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ServiceConnection
    // інтегрує контейнер в springboot, та надає дані для підключення до mongodb
    public MongoDBContainer mongoDBContainer(){
        return new MongoDBContainer("mongo:7");
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(){
        return mock();
    }

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        return mock();
    }

    // цей клас створено для того, щоб для кожного тесту було використано один і той самий контейнер,
    // ане створювати кожен раз різні, що економить ресурси й час
}
