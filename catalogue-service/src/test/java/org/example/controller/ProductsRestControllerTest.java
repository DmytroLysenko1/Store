package org.example.controller;


import org.example.config.controller.ProductsRestController;
import org.example.config.controller.payload.NewProductPayload;
import org.example.entity.Product;
import org.example.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductsRestControllerTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductsRestController productRestController;


    @Test
    void findProduct_ReturnsProductsList() {
        //given

        String filter = "product";

        doReturn(List.of(new Product(1, "First product", "Description of first product"),
                new Product(2, "Second product", "Description of second product")))
                .when(this.productService).findAllProducts("product");

        //when
        Iterable<Product> result = this.productRestController.findProducts(filter);

        //then
        assertEquals(List.of(new Product(1, "First product", "Description of first product"),
                new Product(2, "Second product", "Description of second product")), result);

    }

    @Test
    void createProduct_RequestIsValid_ReturnsNoContent() throws BindException {
        //given
        NewProductPayload payload = new NewProductPayload("New title", "New description");
        BindingResult bindingResult = new MapBindingResult(Map.of(), "payload");

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        doReturn(new Product(1, "New product title", "New product description"))
                .when(this.productService)
                .createProduct("New product title", "New product description");

        //when
        ResponseEntity<?> result = this.productRestController.createProduct(payload, bindingResult, uriComponentsBuilder);

        //then
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(URI.create("http://localhost/catalogue-api/products/1"), result.getHeaders().getLocation());
        assertEquals(new Product(1,"New product title", "New product description"), result.getBody());

        verify(this.productService).createProduct("New product title", "New product description");
        verifyNoInteractions(this.productService);
    }

    @Test
    void createProduct_RequestIsInvalid_ReturnsBadRequest() {
        //given
        NewProductPayload payload = new NewProductPayload("   ", null);
        BindingResult bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "title", "error"));

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        //when
        BindException exception = assertThrows(BindException.class,
                () -> this.productRestController.createProduct(payload, bindingResult, uriComponentsBuilder));

        //then
        assertEquals(List.of(new FieldError("payload", "title", "error")),
                exception.getAllErrors());

        verifyNoInteractions(this.productService);
    }

    @Test
    void createProduct_RequestIsInvalidAAndBindResultIsBindException_ReturnsBadRequest() {
        //given
        NewProductPayload payload = new NewProductPayload("   ", null);
        BindingResult bindingResult = new BindException(new MapBindingResult(Map.of(), "payload"));
        bindingResult.addError(new FieldError("payload", "title", "error"));

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        //when
        BindException exception = assertThrows(BindException.class,
                () -> this.productRestController.createProduct(payload, bindingResult, uriComponentsBuilder));

        //then
        assertEquals(List.of(new FieldError("payload", "title", "error")),
                exception.getAllErrors());

        verifyNoInteractions(this.productService);
    }
}
