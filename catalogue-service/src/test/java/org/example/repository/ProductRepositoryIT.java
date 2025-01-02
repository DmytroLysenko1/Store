package org.example.repository;

import jakarta.transaction.Transactional;
import org.example.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Sql("/sql/products.sql")
@Transactional
// Transactional в даному випадку використовується для того,
// щоб під час виконання тесту дані, які вносяться в базу даних відкочувались,
// зберігаючи стан бази даних до тестів
@DisplayName("Інтеграційні тести для репозиторії(ProductRepository)")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// анотація, яка дає Spring зрозуміти, що під час тесту не треба створювати вбудовану базу даних,
// а треба використовувати, ут, що використовується в проєкті
class ProductRepositoryIT {

    @Autowired
    ProductRepository productRepository;

    @Test
    void findAllByTitleLikeIgnoreCase_ReturnsFilteredProductsList(){
        //given
        String filter = "%condom%";
        // знаки % для не суворого порівняння

        //when
        Iterable<Product> products = this.productRepository.findAllByTitleLikeIgnoreCase(filter);

        //then
        assertEquals(List.of(new Product(2, "Condom", "Thin")), products);
    }

}