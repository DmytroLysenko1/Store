package org.example.service.review;

import lombok.RequiredArgsConstructor;
import org.example.entity.ProductReview;
import org.example.repository.review.ProductReviewRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


@RequiredArgsConstructor
@Service
public class DefaultProductReviewsServiceImpl implements ProductReviewsService {

    private final ProductReviewRepository productReviewRepository;
    @Override
    public Mono<ProductReview> createProductReview(int productId, int rating, String review, String userId) {
        return this.productReviewRepository.save(
                new ProductReview(UUID.randomUUID(), productId, rating, review, userId));
    }

    @Override
    public Flux<ProductReview> findProductsReviewsByProduct(int productId) {
        return this.productReviewRepository.findAllByProductId(productId);
    }
}
