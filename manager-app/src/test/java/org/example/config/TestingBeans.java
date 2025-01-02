package org.example.config;

import org.example.client.RestClientProductRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.client.RestClient;

import javax.sql.DataSource;

import static org.mockito.Mockito.mock;

@Configuration
public class TestingBeans {
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(){
        return mock(ClientRegistrationRepository.class);
    }
    @Bean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository(){
        return mock(OAuth2AuthorizedClientRepository.class);
    }

    @Bean
    public JwtDecoder jwtDecoder(){
        return mock();
    }

    @Bean
    @Primary
    // анотація, що вказує Springy,
    // що саме цей компонент має бути головним на використання для впровадження у ProductsRestClient
    public RestClientProductRestClient testRestClientProductRestClient(
            @Value("${services.catalogue.uri:http://localhost:54321}") String catalogueBaseUrl
    ){
        return new RestClientProductRestClient(RestClient.builder()
                .baseUrl(catalogueBaseUrl)
                .requestFactory(new JdkClientHttpRequestFactory())
                .build());
    }
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5433/manager");
        dataSource.setUsername("manager");
        dataSource.setPassword("manager");
        return dataSource;
    }
}
//такий клас є хорошою практикою мокування конфігурацій
