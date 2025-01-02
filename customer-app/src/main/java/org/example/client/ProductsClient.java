package org.example.client;

import org.example.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductsClient {
    Flux<Product> findAllProducts(String filter);

    /*
     Flux - патерн проєктування "потік даних",
     який являє собою послідовність елементів,
     котра може випромінювати дані асинхронно
    */

    Mono<Product> findProduct(int id);

}
