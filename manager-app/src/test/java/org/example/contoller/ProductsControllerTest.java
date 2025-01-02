package org.example.contoller;

import org.example.client.ProductsRestClient;
import org.example.client.exceptions.BadRequestException;
import org.example.contoller.payload.NewProductPayload;
import org.example.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульні тести класу ProductsController")
class ProductsControllerTest {
    @Mock
    ProductsRestClient productsRestClient;
    @InjectMocks
    ProductsController controller;

    @Test
    @DisplayName("createProduct створить новий товар та перенаправить на сторінку товару")
    void createProduct_RequestIsValid_ReturnsRedirectionToProductPage(){

        // given
        NewProductPayload payload = new NewProductPayload("Новий товар", "Опис нового товару");
        ConcurrentModel model = new ConcurrentModel();

        doReturn(new Product(1,"Новий товар","Опис нового товару"))
                .when(this.productsRestClient)
                .createProduct("Новий товар","Опис нового товару");

        // when
        String result = this.controller.createProduct(payload, model);

        // then
        assertEquals("redirect:/catalogue/products/1", result);
        verify(this.productsRestClient).createProduct("Новий товар","Опис нового товару");
        verifyNoMoreInteractions(this.productsRestClient);
        /*
        verifyNoMoreInteractions метод, який перевіряє,
        чи не було більше звертань до даного mock об'єкту
        */
    }

    @Test
    @DisplayName("createProduct поверне сторінку з помилками, якщо запит не валіден")
    void createProduct_RequestIsInvalid_ReturnsProductFormWithErrors(){
        //given
        NewProductPayload payload = new NewProductPayload("   ", null);
        ConcurrentModel model = new ConcurrentModel();

        doThrow(new BadRequestException(List.of("Помилка 1", "Помилка 2")))
                .when(this.productsRestClient)
                .createProduct("   ",null);

        //when
        String result = this.controller.createProduct(payload, model);

        //then
        assertEquals("catalogue/products/new_product", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Помилка 1", "Помилка 2"), model.getAttribute("errors"));

        verify(this.productsRestClient).createProduct("   ", null);
        verifyNoMoreInteractions(this.productsRestClient);

    }

}