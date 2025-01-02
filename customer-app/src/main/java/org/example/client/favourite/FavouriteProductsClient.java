package org.example.client.favourite;

import io.micrometer.observation.ObservationFilter;
import org.example.entity.FavouriteProduct;
import org.example.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductsClient {

    Mono<FavouriteProduct> findFavouriteProductByProductId(int id);

    Mono<FavouriteProduct> addProductToFavourites(int productId);

     Mono<Void> removeProductFromFavourites(int productId);

    Flux<FavouriteProduct> findFavouriteProducts();



}
