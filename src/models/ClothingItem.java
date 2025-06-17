package models;

public class ClothingItem {
    private String name;
    private String category;
    private double price;
    private int stock;
    private String imagePath;

    public ClothingItem(String name, String category, double price, int stock, String imagePath) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String toString() {
        return name + " - $" + price + " - " + category + " (Stock: " + stock + ")";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}