package com.mipt.vsevolodkirichuk.dz8;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductValidatorTest {

    @Test
    void testValidProduct() {
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(999.99);
        product.setQuantity(50);
        product.setDescription("High-performance laptop");

        ValidationResult result = Validator.validate(product);

        assertTrue(result.isValid());
    }

    @Test
    void testProductWithNullName() {
        Product product = new Product();
        product.setName(null);
        product.setPrice(100.0);
        product.setQuantity(10);

        ValidationResult result = Validator.validate(product);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Product name is required"));
    }

    @Test
    void testProductWithInvalidPrice() {
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(-10.0);
        product.setQuantity(5);

        ValidationResult result = Validator.validate(product);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Price must be between 0 and 1,000,000"));
    }

    @Test
    void testProductWithInvalidQuantity() {
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(500.0);
        product.setQuantity(15000);

        ValidationResult result = Validator.validate(product);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Quantity must be between 0 and 10,000"));
    }

    @Test
    void testProductWithShortDescription() {
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(500.0);
        product.setQuantity(10);
        product.setDescription("Short");

        ValidationResult result = Validator.validate(product);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Description must be between 10 and 500 characters"));
    }

    @Test
    void testProductBoundaryValues() {
        Product product = new Product();
        product.setName("ABC");
        product.setPrice(0.0);
        product.setQuantity(0);
        product.setDescription("A".repeat(10));

        ValidationResult result = Validator.validate(product);

        assertTrue(result.isValid());
    }

    @Test
    void testProductMaxBoundaryValues() {
        Product product = new Product();
        product.setName("A".repeat(100));
        product.setPrice(1000000.0);
        product.setQuantity(10000);
        product.setDescription("A".repeat(500));

        ValidationResult result = Validator.validate(product);

        assertTrue(result.isValid());
    }
}
