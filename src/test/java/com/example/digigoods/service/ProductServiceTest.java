package com.example.digigoods.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.digigoods.exception.InsufficientStockException;
import com.example.digigoods.exception.ProductNotFoundException;
import com.example.digigoods.model.Product;
import com.example.digigoods.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductService productService;

  private Product product1;
  private Product product2;

  @BeforeEach
  void setUp() {
    product1 = new Product(1L, "Product 1", new BigDecimal("10.00"), 5);
    product2 = new Product(2L, "Product 2", new BigDecimal("20.00"), 3);
  }

  @Test
  @DisplayName("Given valid product IDs, when getting products by IDs, then return products")
  void givenValidProductIds_whenGettingProductsByIds_thenReturnProducts() {
    // Arrange
    List<Long> productIds = List.of(1L, 2L);
    List<Product> expectedProducts = List.of(product1, product2);
    when(productRepository.findAllByIdIn(productIds)).thenReturn(expectedProducts);

    // Act
    List<Product> actualProducts = productService.getProductsByIds(productIds);

    // Assert
    assertEquals(expectedProducts, actualProducts);
    verify(productRepository).findAllByIdIn(productIds);
  }

  @Test
  @DisplayName("Given missing product IDs, when getting products by IDs, "
      + "then throw ProductNotFoundException")
  void givenMissingProductIds_whenGettingProductsByIds_thenThrowProductNotFoundException() {
    // Arrange
    List<Long> productIds = List.of(1L, 2L, 3L);
    List<Product> foundProducts = List.of(product1, product2);
    when(productRepository.findAllByIdIn(productIds)).thenReturn(foundProducts);

    // Act & Assert
    assertThrows(ProductNotFoundException.class,
        () -> productService.getProductsByIds(productIds));
  }

  @Test
  @DisplayName("Given sufficient stock, when validating and updating stock, "
      + "then update stock successfully")
  void givenSufficientStock_whenValidatingAndUpdatingStock_thenUpdateStockSuccessfully() {
    // Arrange
    List<Long> productIds = List.of(1L, 1L, 2L); // 2 of product1, 1 of product2
    when(productRepository.findAllByIdIn(List.of(1L, 2L))).thenReturn(List.of(product1, product2));

    // Act
    productService.validateAndUpdateStock(productIds);

    // Assert
    assertEquals(3, product1.getStock()); // 5 - 2 = 3
    assertEquals(2, product2.getStock()); // 3 - 1 = 2
    verify(productRepository, times(2)).save(any(Product.class));
  }

  @Test
  @DisplayName("Given insufficient stock, when validating and updating stock, "
      + "then throw InsufficientStockException")
  void givenInsufficientStock_whenValidatingAndUpdatingStock_thenThrowInsufficientStockException() {
    // Arrange
    List<Long> productIds = List.of(1L, 1L, 1L, 1L, 1L, 1L); // 6 of product1, but only 5 in stock
    when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product1));

    // Act & Assert
    assertThrows(InsufficientStockException.class,
        () -> productService.validateAndUpdateStock(productIds));
  }

  @Nested
  @DisplayName("Product Search Tests")
  class ProductSearchTests {

    @Test
    @DisplayName("Should return matching products when valid search term is provided")
    void givenValidSearchTerm_whenSearchingProductsByName_thenReturnMatchingProducts() {
      // Arrange
      String searchTerm = "Product";
      List<Product> expectedProducts = List.of(product1, product2);
      when(productRepository.findByNameContainingIgnoreCase(searchTerm.trim()))
          .thenReturn(expectedProducts);

      // Act
      List<Product> actualProducts = productService.searchProductsByName(searchTerm);

      // Assert
      assertThat(actualProducts).isNotNull();
      assertThat(actualProducts).hasSize(2);
      assertThat(actualProducts).containsExactlyInAnyOrder(product1, product2);
      verify(productRepository).findByNameContainingIgnoreCase(searchTerm.trim());
    }

    @Test
    @DisplayName("Should return empty list when no products match search term")
    void givenNonMatchingSearchTerm_whenSearchingProductsByName_thenReturnEmptyList() {
      // Arrange
      String searchTerm = "NonExistent";
      when(productRepository.findByNameContainingIgnoreCase(searchTerm.trim()))
          .thenReturn(List.of());

      // Act
      List<Product> actualProducts = productService.searchProductsByName(searchTerm);

      // Assert
      assertThat(actualProducts).isNotNull();
      assertThat(actualProducts).isEmpty();
      verify(productRepository).findByNameContainingIgnoreCase(searchTerm.trim());
    }

    @Test
    @DisplayName("Should trim whitespace from search term before processing")
    void givenSearchTermWithWhitespace_whenSearchingProductsByName_thenTrimWhitespace() {
      // Arrange
      String searchTermWithWhitespace = "  Product  ";
      String trimmedSearchTerm = "Product";
      List<Product> expectedProducts = List.of(product1);
      when(productRepository.findByNameContainingIgnoreCase(trimmedSearchTerm))
          .thenReturn(expectedProducts);

      // Act
      List<Product> actualProducts = productService.searchProductsByName(searchTermWithWhitespace);

      // Assert
      assertThat(actualProducts).isNotNull();
      assertThat(actualProducts).hasSize(1);
      assertThat(actualProducts).contains(product1);
      verify(productRepository).findByNameContainingIgnoreCase(trimmedSearchTerm);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when search term is null")
    void givenNullSearchTerm_whenSearchingProductsByName_thenThrowIllegalArgumentException() {
      // Arrange
      String nullSearchTerm = null;

      // Act & Assert
      assertThatThrownBy(() -> productService.searchProductsByName(nullSearchTerm))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Search term cannot be null or empty");
      verify(productRepository, never()).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when search term is empty")
    void givenEmptySearchTerm_whenSearchingProductsByName_thenThrowIllegalArgumentException() {
      // Arrange
      String emptySearchTerm = "";

      // Act & Assert
      assertThatThrownBy(() -> productService.searchProductsByName(emptySearchTerm))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Search term cannot be null or empty");
      verify(productRepository, never()).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when search term is only whitespace")
    void givenWhitespaceOnlySearchTerm_whenSearchingProductsByName_thenThrowException() {
      // Arrange
      String whitespaceSearchTerm = "   ";

      // Act & Assert
      assertThatThrownBy(() -> productService.searchProductsByName(whitespaceSearchTerm))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Search term cannot be null or empty");
      verify(productRepository, never()).findByNameContainingIgnoreCase(anyString());
    }
  }
}
