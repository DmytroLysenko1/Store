package org.example.repository;


import org.example.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Integer> {
    Iterable<Product> findAllByTitleLikeIgnoreCase(String filter);
}
