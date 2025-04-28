package com.casestudy.product_service.service;

import com.casestudy.product_service.models.Product;
import com.casestudy.product_service.repository.ProductRepository;

import java.util.*;

public abstract class InMemoryProductRepository implements ProductRepository {

    private Map<Long, Product> db = new HashMap<>();

    @Override
    public List<Optional<Product>> findByCategory(String category) {
        List<Optional<Product>> result = new ArrayList<>();
        for (Product p : db.values()) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                result.add(Optional.of(p));
            }
        }
        return result;
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(db.values());
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(db.get(id));
    }

    @Override
    public Product save(Product product) {
        db.put(product.getId(), product);
        return product;
    }

    @Override
    public boolean existsById(Long id) {
        return db.containsKey(id);
    }

    @Override
    public void deleteById(Long id) {
        db.remove(id);
    }
}
