package org.example.entity;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("favourite_product")
public class FavouriteProduct {
    @Id
    private UUID id;

    private int productId;

    private String userId;

}
