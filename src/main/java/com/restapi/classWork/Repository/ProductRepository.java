package com.restapi.classWork.Repository;

import com.restapi.classWork.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByPrice(double price);
}
