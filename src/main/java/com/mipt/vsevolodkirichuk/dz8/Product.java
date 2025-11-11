package com.mipt.vsevolodkirichuk.dz8;
public class Product {
    @NotNull(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Price is required")
    @Range(min = 0, max = 1000000, message = "Price must be between 0 and 1,000,000")
    private Double price;

    @Range(min = 0, max = 10000, message = "Quantity must be between 0 and 10,000")
    private Integer quantity;

    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    public Product() {
    }

    public Product(String name, Double price, Integer quantity, String description) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
