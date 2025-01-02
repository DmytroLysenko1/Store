package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.controller.payload.NewProductReviewPayload;
import org.example.entity.ProductReview;
import org.example.service.review.ProductReviewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("feedback-api/product-reviews")
public class ProductReviewsRestController {

    private final ProductReviewsService productReviewsService;

    @GetMapping("by-product-id/{productId:\\d+}")
    public Flux<ProductReview> findProductReviewsByProductId(@PathVariable("productId") int productId)
    {
        return this.productReviewsService.findProductsReviewsByProduct(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(
            Mono<JwtAuthenticationToken> jwtAuthenticationTokenMono,
            @Valid @RequestBody Mono<NewProductReviewPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder)
    {
        return jwtAuthenticationTokenMono.flatMap(token -> payloadMono
                .flatMap(payload -> this.productReviewsService.createProductReview(
                                        payload.productId(),
                                        payload.rating(),
                                        payload.review(),
                        token.getToken().getSubject())))
                .map(productReview -> ResponseEntity.created(
                                        uriComponentsBuilder
                                                .replacePath("/feedback-api/product-reviews/{id}")
                                                .build(productReview.getProductId()))
                                .body(productReview));
    }
}
