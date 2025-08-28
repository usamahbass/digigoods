package com.example.digigoods.controller;

import com.example.digigoods.model.Product;
import com.example.digigoods.service.ProductService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for product endpoints.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  /**
   * Get all products endpoint.
   *
   * @return list of all products
   */
  @GetMapping
  public ResponseEntity<List<Product>> getAllProducts() {
    List<Product> products = productService.getAllProducts();
    return ResponseEntity.ok(products);
  }

  /**
   * Search products by name endpoint.
   *
   * @param name the search term to look for in product names
   * @return list of products matching the search criteria
   */
  @GetMapping("/search")
  public ResponseEntity<List<Product>> searchProducts(@RequestParam("name") String name) {
    List<Product> products = productService.searchProductsByName(name);
    return ResponseEntity.ok(products);
  }
}
