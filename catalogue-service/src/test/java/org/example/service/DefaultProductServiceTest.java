package org.example.service;


import org.example.entity.Product;
import org.example.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    DefaultProductService service;

    @Test
    void findAllProducts_FilterIsNotSet_ReturnsProductsList() {
        //given
        List<Product> products = IntStream.range(1,4)
                .mapToObj(i -> new Product(i, "Product №%d".formatted(i),
                        "Product description №%d".formatted(i)))
                .toList();

        doReturn(products).when(this.productRepository).findAll();

        //when
        Iterable<Product> result = this.service.findAllProducts(null);

        //then
        assertEquals(products, result);

        verify(this.productRepository).findAll();
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void findAllProducts_FilterIsSet_ReturnsFilteredProductsList() {
        //given
        List<Product> products = IntStream.range(1,4)
                .mapToObj(i -> new Product(i, "Product №%d".formatted(i),
                        "Product description №%d".formatted(i)))
                .toList();

        doReturn(products).when(this.productRepository).findAllByTitleLikeIgnoreCase("%product%");

        //when
        Iterable<Product> result = this.service.findAllProducts("product");

        //then
        assertEquals(products, result);

        verify(this.productRepository).findAllByTitleLikeIgnoreCase("%product%");
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void findProduct_ProductExists_ReturnsNotEmptyOptional() {
        //given
        Product product = new Product(1, "Product №1", "Product description №1");

        doReturn(Optional.of(product)).when(this.productRepository).findById(1);

        //when
        Optional<Product> result = this.service.findProduct(1);

        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(product, result.orElseThrow());

        verify(this.productRepository).findById(1);
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void findProduct_ProductDoesNotExist_ReturnsEmptyOptional() {
        //given
        Product product = new Product(1, "Product №1", "Product description №1");

        //when
        Optional<Product> result = this.service.findProduct(1);

        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(this.productRepository).findById(1);
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void createProduct_ReturnsCreatedProduct() {
        String title = "New product";
        String details = "Description of new product";

        doReturn(new Product(1, "New product", "Description of new product"))
                .when(this.productRepository)
                .save(new Product(null, "New product", "Description of new product"));

        //when
        Product result = this.service.createProduct(title, details);

        //then
        assertEquals(new Product(1, "New product", "Description of new product"), result);

        verify(this.productRepository)
                .save(new Product(null, "New product", "Description of new product"));

        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void updateProduct_ProductExists_UpdateProduct() {
        //given
        Integer productId = 1;
        Product product = new Product(1, "New product", "Description of new product");
        String title = "New title";
        String details = "New description";

        doReturn(Optional.of(product))
                .when(this.productRepository).findById(1);

        //when
        this.service.updateProduct(productId, title, details);

        //then
        verify(this.productRepository).findById(productId);
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void updateProduct_ProductDoesNotExist_ThrowsNoSuchElementException() {
        //given
        Integer productId = 1;
        String title = "New title";
        String details = "New description";

        //when
        assertThrows(NoSuchElementException.class,
                () -> this.service.updateProduct(productId, title, details));

        //then
        verify(this.productRepository).findById(productId);
        verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void deleteProduct_DeletesProduct() {
        //given
        Integer productId = 1;

        //when
        this.service.deleteProduct(productId);

        //then
        verify(this.productRepository).deleteById(productId);
        verifyNoMoreInteractions(this.productRepository);
    }
}
