package org.example.config;


import org.example.client.WebClientProductsClientImpl;
import org.example.client.favourite.WebClientFavouriteProductsClient;
import org.example.client.review.WebClientProductReviewClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.mock;

@Configuration
public class TestBeans {

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository(){
        return mock();
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository(){
        return mock();
    }

    @Bean
    @Primary
    // Анотація, що вказує, що саме цей компонент є головним на впровадження під час тестів.
    // Використовується у разі, коли у контексті застосунку є декілька компонентів однакового типу,
    // але треба змінити ім'я біна
    public WebClientProductsClientImpl mockWebClientProductsClient(){
        return new WebClientProductsClientImpl(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public WebClientProductReviewClientImpl mockWebClientProductReviewClient()
    {
        return new WebClientProductReviewClientImpl(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public WebClientFavouriteProductsClient mockWebClientFavouriteProductsClient()
    {
        return new WebClientFavouriteProductsClient(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }
}
