package org.example.client.review.payload;

public record NewProductReviewPayload (Integer productId, Integer rating, String review) {
}
