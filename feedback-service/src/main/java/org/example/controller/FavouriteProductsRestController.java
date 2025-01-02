package org.example.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.controller.payload.NewFavouriteProductPayload;
import org.example.controller.payload.NewProductReviewPayload;
import org.example.entity.FavouriteProduct;
import org.example.service.favourite.FavouriteProductsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("feedback-api/favourite-products")
public class FavouriteProductsRestController {

    private final FavouriteProductsService favouriteProductsService;

    @GetMapping
    public Flux<FavouriteProduct> findFavouriteProducts(
            Mono<JwtAuthenticationToken> jwtAuthenticationTokenMono)
    {
        return jwtAuthenticationTokenMono.flatMapMany(token ->
                this.favouriteProductsService.findFavouriteProducts(token.getToken().getSubject()));
    }

    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavouriteProduct> findFavouriteProductByProductId(
            Mono<JwtAuthenticationToken> jwtAuthenticationTokenMono,
            @PathVariable("productId") int productId)
    {
        return jwtAuthenticationTokenMono.flatMap(token ->
                this.favouriteProductsService
                        .findFavouriteProductByProduct(productId, token.getToken().getSubject()));
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProduct>> addProductToFavourites(
            Mono<JwtAuthenticationToken> jwtAuthenticationTokenMono,
            @Valid @RequestBody Mono<NewFavouriteProductPayload> payloadMono,

            UriComponentsBuilder uriComponentsBuilder)
    {
        return Mono.zip(jwtAuthenticationTokenMono, payloadMono)
                .flatMap(tuple  ->
                        this.favouriteProductsService
                                .addToFavourites(
                                        tuple.getT2().productId(),
                                        tuple.getT1().getToken().getSubject()))
                .map(favouriteProduct -> ResponseEntity
                        .created(uriComponentsBuilder
                                .replacePath("/feedback-api/favourite-products/{id}")
                                .build(favouriteProduct.getId()))
                        .body(favouriteProduct));
    }

    @DeleteMapping("by-product-id/{productId:\\\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavourites(
            Mono<JwtAuthenticationToken> jwtAuthenticationTokenMono,
            @PathVariable("productId") int productId
            )
    {
        return jwtAuthenticationTokenMono.flatMap(token ->
                this.favouriteProductsService.removeProductFromFavourites(
                        productId, token.getToken().getSubject()))
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
