package logic;

import models.ClothingItem;
import java.io.*;
import java.util.*;

public class InventoryManager {
    private List<ClothingItem> inventory;

    public InventoryManager() {
        inventory = new ArrayList<>();
    }

    public void loadInventory(String filename) {
        inventory.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String name = parts[0].trim();
                    String category = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    int stock = Integer.parseInt(parts[3].trim());
                    String imagePath = parts[4].trim();

                    ClothingItem item = new ClothingItem(name, category, price, stock, imagePath);
                    inventory.add(item);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
        }
    }


    public List<ClothingItem> getInventory() {
        return inventory;
    }

    public void addItem(ClothingItem item) {
        inventory.add(item);
    }

    public void removeItem(ClothingItem item) {
        inventory.remove(item);
    }

    public void saveInventory(String filepath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            for (ClothingItem item : inventory) {
                writer.printf("%s,%s,%.2f,%d,%s%n",
                        item.getName(),
                        item.getCategory(),
                        item.getPrice(),
                        item.getStock(),
                        item.getImagePath()
                );
            }
        } catch (IOException e) {
            System.err.println("Failed to save inventory: " + e.getMessage());
        }
    }

}
