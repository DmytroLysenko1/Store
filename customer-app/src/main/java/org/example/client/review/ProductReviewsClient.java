package org.example.client.review;

import org.example.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewsClient {

    Flux<ProductReview> findProductReviewsByProductId(int productId);

    Mono<ProductReview> createProductReview(Integer productId, Integer rating, String review);
}
