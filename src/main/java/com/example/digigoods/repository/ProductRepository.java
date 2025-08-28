package com.example.digigoods.repository;

import com.example.digigoods.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  /**
   * Find all products by their IDs.
   *
   * @param ids the list of product IDs
   * @return list of products
   */
  List<Product> findAllByIdIn(List<Long> ids);

  /**
   * Find products by name containing the search term (case-insensitive).
   *
   * @param name the search term to look for in product names
   * @return list of products matching the search criteria
   */
  @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
  List<Product> findByNameContainingIgnoreCase(@Param("name") String name);
}
