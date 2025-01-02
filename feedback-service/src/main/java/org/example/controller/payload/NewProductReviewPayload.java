package org.example.controller.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewProductReviewPayload (
        @NotNull(message = "{feedback.products.reviews.create.errors.product_id_is_null}")
        Integer productId,
        @NotNull(message = "{feedback.products.reviews.create.errors.rating_is_null}")
        @Max(value = 5, message = "{feedback.products.reviews.create.errors.rating_is_above_max}")
        @Min(value = 1, message = "{feedback.products.reviews.create.errors.rating_is_below_min}")
        Integer rating,
        @Size(max = 1000)
        String review){
}
