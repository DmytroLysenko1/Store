package org.example.controller.integrationalTest;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.ProductReview;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;


@SpringBootTest
@AutoConfigureWebTestClient
@Slf4j
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
// для реактивних тестів, в не реактивних використовується MockMVC
class ProductReviewsRestControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setup(){
        this.reactiveMongoTemplate.insertAll(List.of(
                new ProductReview(UUID.fromString("eb933b4b-94c2-46b4-b190-b9c132a408b3"),
                        1, 1, "Product review №1", "user-1"),
                new ProductReview(UUID.fromString("ee40c415-aa5e-41fa-85f8-f5f0a90b6f8f"),
                        2, 2, "Product review №2", "user-2"),
                new ProductReview(UUID.fromString("d2a2d1b7-8d26-4635-a59b-6a0a758d13d7"),
                        3, 3, "Product review №3", "user-3")
        )).blockLast();
        // blockLast синхронізує виконання потоку і блокує його виконання, до виконання іншого коду
    }

    @AfterEach
    void tearDown(){
        this.reactiveMongoTemplate.remove(ProductReview.class).all().block();
        // видалення тестових даних
    }

    @Test
    void findProductReviewsByProductId_ReturnsProductsReviews(){
        //when
        this.webTestClient.mutateWith(mockJwt())
        //mutateWith вказує на те, що запит має виконуватись від певного користувача,
        // бо є налаштування безпеки в проєкті

                // ЛОГУВАННЯ ТЕСТУ
                .mutate().filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                    log.info("======REQUEST======");
                    log.info("{} {}", clientRequest.method(), clientRequest.url());
                    clientRequest.headers().forEach((header, value ) -> log.info("{} {}", header, value));
                    log.info("======END REQUEST======");
                    return Mono.just(clientRequest);
                }))
                .build()
                // ЛОГУВАННЯ ТЕСТУ
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                [
                {"id" : "eb933b4b-94c2-46b4-b190-b9c132a408b3",
                 "productId" : 1,
                  "rating" : 1,
                  "review" :  "Product review №1",
                  "userId" : "user-1"
                  },
                {"id" : "ee40c415-aa5e-41fa-85f8-f5f0a90b6f8f", "productId" : 2, "rating" : 2, "review" :  "Product review №2", "userId" : "user-2"},
                {"id" : "d2a2d1b7-8d26-4635-a59b-6a0a758d13d7", "productId" : 3, "rating" : 3, "review" :  "Product review №3", "userId" : "user-3"}
                ]""")
                .returnResult();
        //then
    }

    @Test
    void createProductReview_RequestIsValid_ReturnsCreatedProductReview(){
        //when
        this.webTestClient
                .mutateWith(mockJwt()
                        .jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                {
                "productId" : 1, "rating" :  5, "review" : "Good"
                }
                """)

                //then
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                         {
                           "productId" : 1,
                           "rating" :  5,
                           "review" : "Good",
                           "userId" : "user-tester"
                         }""").jsonPath("$.id").exists()
        //перевірка на те, що в тілі є вказані дані з методу json,
        // а також, те що в корінні тіла є властивість id та воно існує
                .consumeWith(document("feedback/product_reviews/create_product_review",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").type("int").description("Product identifier"),
                                fieldWithPath("rating").type("int").description("Grade"),
                                fieldWithPath("review").type("string").description("feedback")
                        ),
                        requestFields(
                                fieldWithPath("id").type("uuid").description("Feedback identifier"),
                                fieldWithPath("productId").type("int").description("Product identifier"),
                                fieldWithPath("rating").type("int").description("Grade"),
                                fieldWithPath("userId").type("int").description("User identifier")
                        ),
                        responseHeaders(
                            headerWithName(HttpHeaders.LOCATION)
                                    .description("Link to created feedback about product")
                        )));

    }

    @Test
    void createProductReview_RequestIsInvalid_ReturnsBadRequest(){
        //when
        this.webTestClient
                .mutateWith(mockJwt()
                        .jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                {
                "productId" : null, "rating" :  -1, "review" : "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce commodo mauris id mauris fermentum, sed sollicitudin velit finibus. Nulla facilisi. Sed ultricies eros eu eros ultricies, vel tristique libero gravida. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Quisque nec lacinia quam. Proin pulvinar viverra ultrices. Aenean volutpat lorem sit amet lectus ultricies, vel fermentum odio luctus. Ut nec dapibus risus. Donec efficitur odio sed metus aliquam, vitae condimentum enim convallis. Maecenas tempor ligula nec urna sodales, at consequat justo convallis. Vivamus at diam et odio lobortis tincidunt. Nunc vitae lectus ipsum. Sed laoreet magna et vestibulum dictum. Etiam convallis, mauris ut aliquet ultricies, justo leo commodo felis, ut molestie leo purus id justo. Sed ut malesuada nisi. Maecenas eleifend erat nec eros tincidunt, ac malesuada mi cursus."
                }
                """)

                //then
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody().json("""
                         {
                          "errors" : [
                            "Product was not specified",
                            "Rating is less than {value}",
                            "Size of review can not be greater that {max} symbols"
                          ]
                         }""");
    }

    @Test
    void createProductReview_UserIsNotAuthenticated_ReturnsNotAuthorized(){
        //when
        this.webTestClient
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                        "productId" : 1,
                        "rating" : 5,
                        "review" : "Good"
                    }
                """)
                .exchange()
        //then
                .expectStatus().isUnauthorized();
    }
}