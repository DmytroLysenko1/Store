package org.example.controller;

import org.example.client.ProductsClient;
import org.example.client.exception.ClientBadRequestException;
import org.example.client.favourite.FavouriteProductsClient;
import org.example.client.review.ProductReviewsClient;
import org.example.controller.payload.NewProductReviewPayload;
import org.example.entity.FavouriteProduct;
import org.example.entity.Product;
import org.example.entity.ProductReview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    @Mock
    ProductsClient productsClient;
    @Mock
    FavouriteProductsClient favouriteProductsClient;
    @Mock
    ProductReviewsClient productReviewsClient;
    @InjectMocks
    ProductController productController;

    @Test
    void loadProduct_ProductExists_ReturnsNotEmptyMono(){
        //given
        Product product = new Product(1, "Product №1", "Description of product №1");
        doReturn(Mono.just(product)).when(this.productsClient).findProduct(1);

        //when
        StepVerifier.create(this.productController.loadProduct(1))
                //StepVerifier дозволяє створити stream, а також перевірити дані stream

                //then
                .expectNext(new Product(1, "Product №1", "Description of product №1"))
                .expectComplete()
                //завершує expectNext
                .verify();
                //перевіряє на валідність

        verify(this.productsClient).findProduct(1);
        verifyNoMoreInteractions(this.productsClient);
        verifyNoMoreInteractions(this.favouriteProductsClient, this.productReviewsClient);
    }

    @Test
    void loadProduct_ProductDoesNotExist_ReturnsMonoWithNoSuchElementException(){
        //given
        doReturn(Mono.empty()).when(this.productsClient).findProduct(1);

        //when
        StepVerifier.create(this.productController.loadProduct(1))
                .expectErrorMatches(exception ->
                        exception instanceof NoSuchElementException noSuchElementException &&
                        noSuchElementException.getMessage()
                            .equals("customer.products.error.not_found"))
                .verify();

        verify(this.productsClient).findProduct(1);
        verifyNoMoreInteractions(this.productsClient);
        verifyNoInteractions(this.favouriteProductsClient, this.productReviewsClient);
    }

    @Test
    void getProductPage_ReturnsProductPage() {
        //given
        ConcurrentModel model = new ConcurrentModel();
        List<ProductReview> productReviews = List.of(
                new ProductReview(UUID.fromString(
                        "6a8512d8-cbaa-11ee-b986-376cc5867cf5"),
                        1, 5, "Amazing"),
                new ProductReview(UUID.fromString(
                        "849c3fac-cbaa-11ee-af68-737c6d37214a"),
                        1, 4, "Could be better"));

        doReturn(Flux.fromIterable(productReviews))
                .when(this.productReviewsClient)
                .findProductReviewsByProductId(1);

        FavouriteProduct favouriteProduct = new FavouriteProduct(
                UUID.fromString("af5f9496-cbaa-11ee-a407-27b46917819e"), 1);

        doReturn(Mono.just(favouriteProduct))
                .when(this.favouriteProductsClient)
                .findFavouriteProductByProductId(1);

        //when
        StepVerifier.create(this.productController.getProductPage(
                Mono.just(new Product(1, "Product №1", "Product description №1")), model))

                //then
                .expectNext("customer/products/product")
                .verifyComplete();

        assertEquals(productReviews, model.getAttribute("reviews"));
        assertEquals(true, model.getAttribute("inFavourite"));

        verify(this.productReviewsClient).findProductReviewsByProductId(1);
        verify(this.favouriteProductsClient).findFavouriteProductByProductId(1);
        verifyNoMoreInteractions(this.productsClient, this.favouriteProductsClient);
        verifyNoMoreInteractions(this.productsClient);
    }

    @Test
    void addProductToFavourites_RequestIsValid_RedirectsToProductPage() {
        //given
        doReturn(Mono.just(new FavouriteProduct(
                UUID.fromString("25ec67b4-cbac-11ee-adc8-4bd80e8171c4"), 1)))
                .when(this.favouriteProductsClient).addProductToFavourites(1);

        //when
        StepVerifier.create(this.productController.addProductToFavourites(
                Mono.just(new Product(1, "Product №1", "Product description №1"))))

                //then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(this.favouriteProductsClient).addProductToFavourites(1);
        verifyNoMoreInteractions(this.favouriteProductsClient);
        verifyNoMoreInteractions(this.productReviewsClient, this.productsClient);
    }

    @Test
    void addProductToFavourites_RequestIsInvalid_RedirectsToProductPage() {
        //given
        doReturn(Mono.error(new ClientBadRequestException("Exception happened", null, List.of("Exception"))))
                .when(this.favouriteProductsClient).addProductToFavourites(1);

        //when
        StepVerifier.create(this.productController.addProductToFavourites(
                Mono.just(new Product(1, "Product №1", "Product description №1"))))

                //then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(this.favouriteProductsClient).addProductToFavourites(1);
        verifyNoInteractions(this.favouriteProductsClient);
        verifyNoInteractions(this.productsClient, this.productReviewsClient);
    }

    @Test
    void removeProductFromFavourites_RedirectsToProductPage() {
        //given
        doReturn(Mono.empty()).when(this.favouriteProductsClient).removeProductFromFavourites(1);

        //when
        StepVerifier.create(this.productController.removeProductFromFavourites(
                Mono.just(new Product(1, "Product №1", "Product description №1"))))

                //then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(this.favouriteProductsClient).removeProductFromFavourites(1);
        verifyNoInteractions(this.favouriteProductsClient);
        verifyNoMoreInteractions(this.productController, this.productReviewsClient);
    }

    @Test
    void createReview_RequestIsValid_RedirectsToProductPage() {
        //given
        ConcurrentModel model = new ConcurrentModel();
        MockServerHttpResponse response = new MockServerHttpResponse();

        doReturn(Mono.just(new ProductReview(
                UUID.fromString("86efa22c-cbae-11ee-ab01-679baf165fb7"),
                1, 3, "Good")))
                .when(this.productReviewsClient).createProductReview(1, 3, "Good");

        //when
        StepVerifier.create(this.productController.createReview(
                        Mono.just(
                                new Product(1, "Product №1", "Product description №1")),
                        new NewProductReviewPayload( "Good", 3),
                        model,
                        response))

                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        assertNull(response.getStatusCode());

        verify(this.productReviewsClient).createProductReview(1, 3, "Good");
        verifyNoInteractions(this.productReviewsClient);
        verifyNoMoreInteractions(this.productsClient, this.favouriteProductsClient);
    }

    @Test
    void createReview_RequestIsInvalid_ReturnsProductPageWithPayloadAndErrors() {
        //given
        ConcurrentModel model = new ConcurrentModel();
        MockServerHttpResponse response = new MockServerHttpResponse();

        FavouriteProduct favouriteProduct = new FavouriteProduct(UUID.fromString("af5f9496-cbaa-11ee-a407-27b46917819e"), 1);

        doReturn(Mono.just(favouriteProduct)).when(this.favouriteProductsClient).findFavouriteProductByProductId(1);

        doReturn(Mono.error(new ClientBadRequestException("Exception happened", null,
                List.of("Exception 1", "Exception 2"))))
                .when(this.productReviewsClient).createProductReview(1, null, "Too long feedback");

        //when
        StepVerifier.create(this.productController.createReview(
                Mono.just(new Product(1, "Product №1", "Product description №1")),
                new NewProductReviewPayload("Too long feedback", null), model, response))

                //then
                .expectNext("customer/products/product")
                .verifyComplete();

        assertEquals(true, model.getAttribute("inFavourite"));
        assertEquals(new NewProductReviewPayload("Too long feedback", null), model.getAttribute("payload"));
        assertEquals(List.of("Exception 1", "Exception 2"), model.getAttribute("errors"));

        verify(this.productReviewsClient).createProductReview(1, null, "Too long feedback");
        verify(this.favouriteProductsClient).findFavouriteProductByProductId(1);

        verifyNoInteractions(this.productReviewsClient, this.favouriteProductsClient);
        verifyNoInteractions(this.productsClient);
    }

    @Test
    @DisplayName("Виключення NoSuchElementException має транслюватись у сторінку errors/404")
    void handleNoSuchElementException_ReturnsErrors404(){
        //given
        NoSuchElementException exception = new NoSuchElementException("Product was not found");
        ConcurrentModel model = new ConcurrentModel();
        MockServerHttpResponse response = new MockServerHttpResponse();

        //when
        String result = this.productController.handleNoSuchElementException(exception, model, response);

        //then
        assertEquals("errors/404", result);
        assertEquals("Product was not found", model.getAttribute("error"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }



}