package org.ozzy.product.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DataStore {
    public Collection<Product> getProducts();
    public Product getProduct(int id);
    public void addProduct(Product product);
    public void removeProduct(int id);
    public void updateProduct(Product product);
}
