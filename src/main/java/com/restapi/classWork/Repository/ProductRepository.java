package com.restapi.classWork.Repository;

import com.restapi.classWork.Model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByPriceBetween(double min, double max, Pageable pageable);
    Page<Product> findByPriceGreaterThanEqual(double min, Pageable pageable);
    Page<Product> findByPriceLessThanEqual(double max, Pageable pageable);
}
