package org.example.contoller;


import org.example.client.ProductsRestClient;
import org.example.client.exceptions.BadRequestException;
import org.example.contoller.payload.UpdateProductPayload;
import org.example.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    ProductsRestClient productsRestClient;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ProductController controller;

    @Test
    void product_ProductExists_ReturnsProduct() {
        //given
        Product product = new Product(1, "Product №1", "Product description №1");

        doReturn(Optional.of(product)).when(this.productsRestClient).findProduct(1);

        //when
        Product result = this.controller.product(1);

        //then
        assertEquals(product, result);

        verify(this.productsRestClient).findProduct(1);
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    void product_ProductsDoesNotExist_ThrowsNoSuchElementException() {
        //given

        //when
        Exception exception = assertThrows(NoSuchElementException.class,
                () -> this.controller.product(1));

        //then
        assertEquals("catalogue.errors.product.not_found", exception.getMessage());

        verify(this.productsRestClient).findProduct(1);
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    void getProduct_ReturnsProductPage() {
        //given

        //when
        String result = this.controller.getProduct();

        //then
        assertEquals("catalogue/products/product", result);

        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    void getProductEditPage_ReturnsProductEditPage() {
        //given

        //when
        String result = this.controller.getProductEditPage();

        //then
        assertEquals("catalogue/products/edit", result);

        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    void updateProduct_RequestIsValid_RedirectsToProductPage() {
        //given
        Product product = new Product(1,"Product №1","Product description №1");
        UpdateProductPayload payload = new UpdateProductPayload("New title", "New description");
        ConcurrentModel model = new ConcurrentModel();
        MockHttpServletResponse response = new MockHttpServletResponse();

        //when
        String result = this.controller.updateProduct(product, payload, model, response);

        //then
        assertEquals("redirect:/catalogue/products/1", result);

        verify(this.productsRestClient)
                .updateProduct(1, "New title", "New product description");

        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    void updateProduct_RequestIsValid_ReturnsProductEditPage() {
        //given
        Product product = new Product(1,"Product №1","Product description №1");
        UpdateProductPayload payload = new UpdateProductPayload("   ", null);
        ConcurrentModel model = new ConcurrentModel();
        MockHttpServletResponse response = new MockHttpServletResponse();

        doThrow(new BadRequestException(List.of("Exception 1", "Exception 2")))
                .when(this.productsRestClient).updateProduct(1, "   ", null);

        //when
        String result = this.controller.updateProduct(product, payload, model, response);

        //then
        assertEquals("catalogue/products/edit", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Exception 1", "Exception 2"), model.getAttribute("errors"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(this.productsRestClient).updateProduct(1, "   ", null);
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    void deleteProduct_RedirectsToProductsListPage() {
        //given
        Product product = new Product(1,"Product №1","Product description №1");

        //when
        String result = this.controller.deleteProduct(product);

        //then
        assertEquals("redirect:/catalogue/products/list", result);

        verify(this.productsRestClient).deleteProduct(1);
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    void handleNoSuchElementException_Returns404ErrorPage() {
        //given
        NoSuchElementException exception = new NoSuchElementException("error");
        ConcurrentModel model = new ConcurrentModel();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Locale locale = Locale.ENGLISH;

        doReturn("Exception").when(this.messageSource)
                .getMessage("error", new Object[0], "error", Locale.ENGLISH);


        //when

        String result = this.controller.handleNoSuchElementException(exception, model, response, locale);

        //then
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(this.messageSource)
                .getMessage("error", new Object[0], "error", Locale.ENGLISH);
        verifyNoMoreInteractions(this.productsRestClient);
        verifyNoMoreInteractions(this.messageSource);

    }
}
