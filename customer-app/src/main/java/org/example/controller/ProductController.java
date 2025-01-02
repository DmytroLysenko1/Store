package org.example.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client.ProductsClient;
import org.example.client.exception.ClientBadRequestException;
import org.example.client.favourite.FavouriteProductsClient;
import org.example.client.review.ProductReviewsClient;
import org.example.controller.payload.NewProductReviewPayload;
import org.example.entity.Product;
import org.example.entity.ProductReview;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.csrf.CsrfToken;


import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products/{productId:\\d+}")
@Slf4j
public class ProductController {

    private final ProductReviewsClient productReviewsClient;

    private final ProductsClient productsClient;

    private final FavouriteProductsClient favouriteProductsClient;

    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> loadProduct(@PathVariable("productId") int id){
        return this.productsClient.findProduct(id)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new NoSuchElementException("customer.products.error.not_found"))));
    }

    @GetMapping
    public Mono<String> getProductPage(@ModelAttribute("product") Mono<Product> productMono, Model model) {
        model.addAttribute("inFavourite", false);
        return productMono.flatMap(
                product -> this.productReviewsClient.findProductReviewsByProductId(product.id())
                        .collectList()
                        .doOnNext(productReviews -> model.addAttribute("reviews", productReviews))
                        .then(this.favouriteProductsClient.findFavouriteProductByProductId(product.id())
                                .doOnNext(favouriteProduct -> model.addAttribute("inFavourite", true)))
                        .thenReturn("customer/products/product")
        );
    }

    @PostMapping("add-to-favourites")
    public Mono<String> addProductToFavourites(@ModelAttribute("product") Mono<Product> productMono){
        return productMono
                .map(Product::id)
                .flatMap(productId -> this.favouriteProductsClient.addProductToFavourites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId))
                        .onErrorResume(exception -> {
                            log.error(exception.getMessage(), exception);
                            return Mono.just("redirect:/customer/products/%d".formatted(productId));
                        }));
    }

    @PostMapping("remove-from-favourites")
    public Mono<String> removeProductFromFavourites(@ModelAttribute("product") Mono<Product> productMono){
        return productMono
                .map(Product::id)
                .flatMap(productId ->
                        this.favouriteProductsClient.removeProductFromFavourites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId)));
    }

    @PostMapping("create-review")
    public Mono<String> createReview(
            @ModelAttribute("product") Mono<Product> productMono,
            NewProductReviewPayload payload,
            Model model,
            ServerHttpResponse response)
    {
        return productMono.flatMap(product ->
                this.productReviewsClient.createProductReview(product.id(), payload.rating(), payload.review())
                        .thenReturn("redirect:/customer/products/%d".formatted(product.id()))
                        .onErrorResume(ClientBadRequestException.class, exception -> {
                            model.addAttribute("inFavourite", false);
                            model.addAttribute("payload", payload);
                            model.addAttribute("errors", exception.getErrors());
                            response.setStatusCode(HttpStatus.BAD_REQUEST);
                            return this.favouriteProductsClient.findFavouriteProductByProductId(product.id())
                                    .doOnNext(favouriteProduct -> model.addAttribute("inFavourite", true))
                                    .thenReturn("customer/products/product");
                        }));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception,
                                               Model model,
                                               ServerHttpResponse response)
    {
        model.addAttribute("error", exception.getMessage());
        response.setStatusCode(HttpStatus.NOT_FOUND);

        return "errors/404";
    }

    @ModelAttribute
    public Mono<CsrfToken> loadCsrfToken(ServerWebExchange serverWebExchange){
       return Objects.requireNonNull(
               serverWebExchange.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName()))
               .doOnSuccess(token ->
                       serverWebExchange
                               .getAttributes()
                               .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
    }
}
