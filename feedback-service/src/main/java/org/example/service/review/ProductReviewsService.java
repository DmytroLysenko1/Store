package org.example.service.review;


import org.example.entity.ProductReview;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ProductReviewsService {

    Mono<ProductReview> createProductReview(int productId, int rating, String review, String userId);

    Flux<ProductReview> findProductsReviewsByProduct(int productId);
}
