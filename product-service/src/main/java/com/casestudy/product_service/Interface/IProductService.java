package com.casestudy.product_service.Interface;

import com.casestudy.product_service.models.Product;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    List<Product> getAllProducts();
    Product addProduct(Product product);
    Object getProductById(String id);
    Product updateProduct(Product product);
    void deleteProductById(String id);
    List<Optional<Product>> findByCategory(String category);

}
