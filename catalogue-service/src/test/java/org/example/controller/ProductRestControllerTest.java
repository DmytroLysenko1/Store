package org.example.controller;

import org.example.config.controller.ProductRestController;
import org.example.config.controller.payload.UpdateProductPayload;
import org.example.entity.Product;
import org.example.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductRestControllerTest {

    @Mock
    ProductService productService;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ProductRestController productRestController;

    @Test
    void getProduct_ProductExists_ReturnsProduct() {
        //given
        Product product = new Product(1, "Product title", "Product detail");
        doReturn(Optional.of(product)).when(this.productService).findProduct(1);

        //when
        Product result = this.productRestController.getProduct(1);

        //then
        assertEquals(product, result);
    }

    @Test
    void getProduct_ProductDoesNotExist_ThrowsNoSuchElementException() {
        //given

        //when
        Exception exception = assertThrows(NoSuchFieldException.class,
                () -> this.productRestController.getProduct(1));

        //then
        assertEquals("catalogue.errors.product.not_found", exception.getMessage());
    }

    @Test
    void findProduct_ReturnsProduct(){
        //given
        Product product = new Product(1, "Product title", "Product details");

        //when
        Product result = this.productRestController.findProduct(product);

        //then
        assertEquals(product, result);
    }

    @Test
    void updateProduct_RequestIsValid_ReturnsNoContent() throws BindException {
        //given
        UpdateProductPayload payload =
                new UpdateProductPayload("New title", "New description");

        BindingResult bindingResult = new MapBindingResult(Map.of(), "payload");

        //when
        ResponseEntity<?> result = this.productRestController.updateProduct(1, payload, bindingResult);

        //then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.productService).updateProduct(1, "New title", "New description");

    }

    @Test
    void updateProduct_RequestIsInvalidAndBindingResultIsBindException_ReturnsBadRequest() {
        //given
        UpdateProductPayload payload = new UpdateProductPayload("   ", null);
        BindingResult bindingResult = new BindException(new MapBindingResult(Map.of(), "payload"));
        bindingResult.addError(new FieldError("payload", "title", "error"));

        //when
        BindException exception = assertThrows(BindException.class,
                () -> this.productRestController.updateProduct(1, payload, bindingResult));

        //then
        assertEquals(List.of(new FieldError("payload", "title", "error")),
                exception.getAllErrors());

        verifyNoInteractions(this.productService);
    }

    @Test
    void deleteProduct_ReturnsNoContent() {
        //given

        //when
        ResponseEntity<Void> result = this.productRestController.deleteProduct(1);

        //then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.productService).deleteProduct(1);
    }

    @Test
    void handleNoSuchElementException_ReturnsNotFound() {
        //given
        NoSuchElementException exception = new NoSuchElementException("error_code");
        Locale locale = Locale.ENGLISH;

        doReturn("error details")
                .when(this.messageSource)
                .getMessage("error_code", new Object[0],
                        "error_code", Locale.ENGLISH);

        //when

        ResponseEntity<ProblemDetail> result =
                this.productRestController.handleNoSuchElementException(exception, locale);

        //then
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getBody().getStatus());
        assertEquals("error details", result.getBody().getDetail());

        verifyNoInteractions(this.productService);
    }
}
