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
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length == 3) {
                    String name = parts[0];
                    String category = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    inventory.add(new ClothingItem(name, category, price));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading inventory: " + e.getMessage());
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


}
