package org.example.client.review;

import lombok.RequiredArgsConstructor;
import org.example.client.exception.ClientBadRequestException;
import org.example.client.review.payload.NewProductReviewPayload;
import org.example.entity.ProductReview;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RequiredArgsConstructor
public class WebClientProductReviewClientImpl implements ProductReviewsClient {

    private final WebClient webClient;
    @Override
    public Flux<ProductReview> findProductReviewsByProductId(int productId) {
        return this.webClient
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/{productId}", productId)
                .retrieve()
                .bodyToFlux(ProductReview.class);
    }

    @Override
    public Mono<ProductReview> createProductReview(Integer productId, Integer rating, String review) {
        return this.webClient
                .post()
                .uri("/feedback-api/product-reviews")
                .bodyValue(new NewProductReviewPayload(productId, rating, review))
                .retrieve()
                .bodyToMono(ProductReview.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        exception -> new ClientBadRequestException(
                                "Exception happened creating product review",
                                exception,
                                ((List<String>) exception.getResponseBodyAs(ProblemDetail.class)
                                        .getProperties()
                                        .get("erors"))));
    }
}
