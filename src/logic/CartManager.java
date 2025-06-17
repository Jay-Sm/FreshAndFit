package logic;

import models.ClothingItem;
import java.util.*;

public class CartManager {
    private List<ClothingItem> cart;

    public CartManager() {
        cart = new ArrayList<>();
    }

    public void addToCart(ClothingItem item) {
        cart.add(item);
    }

    public void removeFromCart(ClothingItem item) {
        cart.remove(item);
    }

    public List<ClothingItem> getCartItems() {
        return cart;
    }

    public double getTotal() {
        return cart.stream().mapToDouble(ClothingItem::getPrice).sum();
    }

    public void clearCart() {
        cart.clear();
    }


}
