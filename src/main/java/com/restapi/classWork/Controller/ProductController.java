package com.restapi.classWork.Controller;

import com.restapi.classWork.Model.Product;
import com.restapi.classWork.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")

public class ProductController {

    @Autowired
    private ProductRepository repository;

    // GET all with filtering, pagination, sorting
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> result;

        if (minPrice != null && maxPrice != null) {
            result = repository.findByPriceBetween(minPrice, maxPrice, pageable);
        } else if (minPrice != null) {
            result = repository.findByPriceGreaterThanEqual(minPrice, pageable);
        } else if (maxPrice != null) {
            result = repository.findByPriceLessThanEqual(maxPrice, pageable);
        } else {
            result = repository.findAll(pageable);
        }

        return ResponseEntity.ok(result);
    }

    // GET by ID with error handling
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(product -> ResponseEntity.ok(product))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Error: Product with ID " + id + " does not exist."));
    }



    // CREATE
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return new ResponseEntity<>(repository.save(product), HttpStatus.CREATED);
    }

    // UPDATE with error handling
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product product) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(existing -> {
                    existing.setName(product.getName());
                    existing.setPrice(product.getPrice());
                    Product updated = repository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Error: Product with ID " + id + " does not exist."));
    }



    // DELETE with confirmation
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(product -> {
                    repository.deleteById(id);
                    return ResponseEntity.ok("Product with ID " + id + " has been deleted.");
                })
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Error: Product with ID " + id + " does not exist."));
    }


}
