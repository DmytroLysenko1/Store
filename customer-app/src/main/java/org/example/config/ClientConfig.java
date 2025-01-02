package org.example.config;


import org.example.client.WebClientProductsClientImpl;
import org.example.client.favourite.WebClientFavouriteProductsClient;
import org.example.client.review.WebClientProductReviewClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    @Scope("prototype")
    public WebClient.Builder servicesWebClientBuilder(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository
    ) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
                        authorizedClientRepository);
        filter.setDefaultClientRegistrationId("keycloak");
        return WebClient.builder()
                .filter(filter);
    }

    @Bean
    public WebClientProductsClientImpl webClientProductsClient(
            @Value("${services.catalogue.uri:http://localhost:8082}") String catalogueBaseUrl,
            WebClient.Builder servicesWebClientBuilder
    ) {
        return new WebClientProductsClientImpl(servicesWebClientBuilder
                .baseUrl(catalogueBaseUrl)
                .build());
    }

    @Bean
    public WebClientProductReviewClientImpl webClientProductReviewClient(
            @Value("${services.feedback.uri:http://localhost:8085}") String feedBackBaseUrl,
            WebClient.Builder servicesWebClientBuilder)
    {
        return new WebClientProductReviewClientImpl(servicesWebClientBuilder
                .baseUrl(feedBackBaseUrl)
                .build());
    }

    @Bean
    public WebClientFavouriteProductsClient webClientFavouriteProductsClient(
            @Value("${services.feedback.uri:http://localhost:8085}") String feedBackBaseUrl,
            WebClient.Builder servicesWebClientBuilder)
    {
        return new WebClientFavouriteProductsClient(servicesWebClientBuilder
                .baseUrl(feedBackBaseUrl)
                .build());
    }
}