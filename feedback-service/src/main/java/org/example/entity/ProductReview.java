package org.example.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("product_review")
/*
 В анотації Document написана назва колекції,
 яка відноситься до даного класу,
 за замовчуванням назва колекції іде не через нижнє підкреслювання,
 а через camelCase.
 Є чисто косметичним рішенням для зручнішого читання
 */

public class ProductReview {
    @Id
    private UUID id;

    private int productId;

    private int rating;

    private String review;

    private String userId;

}
