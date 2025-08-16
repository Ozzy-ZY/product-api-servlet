package org.ozzy.product.servlet.requestmodel;

public class AddProductRequest {
    private String idempotencyKey;
    private String name;
    private String description;
    private double price;
    public AddProductRequest() {
    }
    public AddProductRequest(String idempotencyKey, String name, String description, double price) {
        idempotencyKey = idempotencyKey;
        this.name = name;
        this.description = description;
        this.price = price;
    }
    public String getIdempotencyKey() {
        return idempotencyKey;
    }
    public void setIdempotencyKey(String idempotencyKey) {
        idempotencyKey = idempotencyKey;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public static boolean isValid(AddProductRequest addProductRequest) {
        return addProductRequest != null && addProductRequest.idempotencyKey != null && addProductRequest.getName() != null && addProductRequest.getDescription() != null && addProductRequest.getPrice() >= 0;
    }
}
