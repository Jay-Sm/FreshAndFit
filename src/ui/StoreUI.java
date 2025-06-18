package ui;

import logic.CartManager;
import logic.InventoryManager;
import models.ClothingItem;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class StoreUI {
    private InventoryManager inventoryManager;
    private CartManager cartManager;

    public static String getAppRootPath() {
        try {
            return new File(StoreUI.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()).getParent();
        } catch (Exception e) {
            e.printStackTrace();
            return ".";
        }
    }

    public StoreUI() {
        inventoryManager = new InventoryManager();
        String root = getAppRootPath();
        String dataPath = root + File.separator + "FreshAndFit\\data" + File.separator + "items.txt";
        inventoryManager.loadInventory(dataPath);

        System.out.println(dataPath);
        System.out.println(root);


        cartManager = new CartManager();
        createAndShowGUI();
    }

    private void updateItemSelector(JComboBox<String> itemSelector, JTextArea itemsArea, String filterCategory) {
        itemSelector.removeAllItems();
        itemsArea.setText("Available Products:\n");

        for (ClothingItem item : inventoryManager.getInventory()) {
            if (filterCategory.equals("All") || item.getCategory().equalsIgnoreCase(filterCategory)) {
                itemSelector.addItem(item.toString());
                itemsArea.append(item.toString() + "\n");
            }
        }
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Fit & Fresh Clothing Store");

        String[] categories = {"All", "Shirts", "Pants", "Accessories", "Shoes"};
        JComboBox<String> categoryFilter = new JComboBox<>(categories);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JComboBox<String> itemSelector = new JComboBox<>();
        JTextArea itemsArea = new JTextArea();
        itemsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(itemsArea);

        for (ClothingItem item : inventoryManager.getInventory()) {
            itemSelector.addItem(item.toString());
        }

        JButton addButton = new JButton("Add to Cart");
        JButton viewCartButton = new JButton("View Cart");

        JButton adminButton = new JButton("Admin Panel");
        adminButton.addActionListener(e -> {
            String selectedCategory = (String) categoryFilter.getSelectedItem();
            showAdminPanel(frame, itemSelector, itemsArea, selectedCategory);
        });

        JButton previewButton = new JButton("Preview Item");

        addButton.addActionListener(e -> {
            int selectedIndex = itemSelector.getSelectedIndex();
            if (selectedIndex >= 0) {
                ClothingItem selectedItem = inventoryManager.getInventory().get(selectedIndex);

                if (selectedItem.getStock() <= 0) {
                    JOptionPane.showMessageDialog(frame, "Sorry, " + selectedItem.getName() + " is out of stock.", "Out of Stock", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                cartManager.addToCart(selectedItem);

                // Reduce stock by 1 when added to cart
                selectedItem.setStock(selectedItem.getStock() - 1);

                // Update UI to reflect new stock
                String selectedCategory = (String) categoryFilter.getSelectedItem();
                updateItemSelector(itemSelector, itemsArea, selectedCategory);

                JOptionPane.showMessageDialog(frame, selectedItem.getName() + " added to cart.");
            }
        });

        viewCartButton.addActionListener(e -> {
            java.util.List<ClothingItem> cartItems = cartManager.getCartItems();

            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Your cart is empty.", "Shopping Cart", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Build the cart display string + total cost
            StringBuilder cartDisplay = new StringBuilder();
            double totalCost = 0;
            for (ClothingItem item : cartItems) {
                cartDisplay.append(item.toString()).append("\n");
                totalCost += item.getPrice();
            }
            cartDisplay.append("\nTotal Cost: $").append(String.format("%.2f", totalCost));

            String[] options = {"Remove Item", "Checkout", "Cancel"};

            int choice = JOptionPane.showOptionDialog(
                    frame,
                    cartDisplay.toString(),
                    "Shopping Cart",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[2]
            );

            if (choice == 0) { // Remove Item

                String[] itemOptions = new String[cartItems.size()];
                for (int i = 0; i < cartItems.size(); i++) {
                    itemOptions[i] = cartItems.get(i).toString();
                }

                String selected = (String) JOptionPane.showInputDialog(
                        frame,
                        "Select an item to remove:",
                        "Remove Item",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        itemOptions,
                        itemOptions[0]
                );

                if (selected != null) {
                    for (ClothingItem item : cartItems) {
                        if (item.toString().equals(selected)) {
                            cartManager.removeFromCart(item);

                            // Increase stock by 1
                            item.setStock(item.getStock() + 1);

                            // Update UI to show new stock
                            String selectedCategory = (String) categoryFilter.getSelectedItem();
                            updateItemSelector(itemSelector, itemsArea, selectedCategory);

                            JOptionPane.showMessageDialog(frame, item.getName() + " removed from cart.");
                            break;
                        }
                    }
                }

            } else if (choice == 1) { // Checkout
                int confirm = JOptionPane.showConfirmDialog(
                        frame,
                        "Confirm purchase for $" + String.format("%.2f", totalCost) + "?",
                        "Confirm Checkout",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    cartManager.clearCart();
                    JOptionPane.showMessageDialog(frame, "Thank you for your purchase!");
                }
            }
            // If cancel or closed, do nothing


        });

        previewButton.addActionListener(e -> {
            int selectedIndex = itemSelector.getSelectedIndex();
            if (selectedIndex >= 0) {
                ClothingItem selectedItem = inventoryManager.getInventory().get(selectedIndex);

                // Create panel for preview
                JPanel previewPanel = new JPanel(new BorderLayout(10, 10));
                JPanel infoPanel = new JPanel(new GridLayout(4, 1));

                infoPanel.add(new JLabel("Name: " + selectedItem.getName()));
                infoPanel.add(new JLabel("Category: " + selectedItem.getCategory()));
                infoPanel.add(new JLabel("Price: $" + selectedItem.getPrice()));
                infoPanel.add(new JLabel("Stock: " + selectedItem.getStock()));

                previewPanel.add(infoPanel, BorderLayout.CENTER);

                // Add image if path exists
                String imagePath = selectedItem.getImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        ImageIcon imageIcon = new ImageIcon(imagePath);
                        Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        JLabel imageLabel = new JLabel(new ImageIcon(image));
                        previewPanel.add(imageLabel, BorderLayout.WEST);
                    } catch (Exception ex) {
                        previewPanel.add(new JLabel("Image could not be loaded."), BorderLayout.WEST);
                    }
                } else {
                    previewPanel.add(new JLabel("No image available."), BorderLayout.WEST);
                }


                JOptionPane.showMessageDialog(frame, previewPanel, "Item Preview", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an item first.");
            }
        });


        categoryFilter.addActionListener(e -> {
            String selectedCategory = (String) categoryFilter.getSelectedItem();
            updateItemSelector(itemSelector, itemsArea, selectedCategory);
        });

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));

        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(categoryFilter);

        JPanel actionPanel = new JPanel();
        actionPanel.add(itemSelector);
        actionPanel.add(addButton);
        actionPanel.add(viewCartButton);
        actionPanel.add(adminButton);
        actionPanel.add(previewButton);

        bottomPanel.add(filterPanel);
        bottomPanel.add(actionPanel);

        itemsArea.setText("Available Products:\n");
        for (ClothingItem item : inventoryManager.getInventory()) {
            itemsArea.append(item.toString() + "\n");
        }

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);

    }

    private void showAdminPanel(JFrame parentFrame, JComboBox<String> itemSelector, JTextArea itemsArea, String currentCategory) {
        JDialog adminDialog = new JDialog(parentFrame, "Admin Panel", true);
        adminDialog.setSize(400, 300);
        adminDialog.setLayout(new BorderLayout());

        // Item list
        DefaultListModel<ClothingItem> listModel = new DefaultListModel<>();
        for (ClothingItem item : inventoryManager.getInventory()) {
            listModel.addElement(item);
        }

        JList<ClothingItem> itemList = new JList<>(listModel);
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane itemScrollPane = new JScrollPane(itemList);

        // Buttons
        JButton addItemButton = new JButton("Add Item");
        JButton removeItemButton = new JButton("Remove Item");
        JButton editItemButton = new JButton("Edit Item");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addItemButton);
        buttonPanel.add(removeItemButton);
        buttonPanel.add(editItemButton);

        adminDialog.add(itemScrollPane, BorderLayout.CENTER);
        adminDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add item logic
        addItemButton.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField priceField = new JTextField();
            JTextField categoryField = new JTextField();
            JTextField stockField = new JTextField();
            JTextField imageField = new JTextField();
            Object[] inputs = {
                    "Name:", nameField,
                    "Price:", priceField,
                    "Category:", categoryField,
                    "Stock:", stockField,
                    "Image:", imageField
            };

            int result = JOptionPane.showConfirmDialog(adminDialog, inputs, "Add New Item", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String name = nameField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    String category = categoryField.getText().trim();
                    int stock = Integer.parseInt(stockField.getText().trim());
                    String image = imageField.getText().trim();

                    // Prevent negative values
                    if (price < 0 || stock < 0) {
                        JOptionPane.showMessageDialog(adminDialog, "Price and stock must be non-negative.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    ClothingItem newItem = new ClothingItem(name, category, price, stock, image);
                    inventoryManager.addItem(newItem);
                    inventoryManager.saveInventory("C:/Users/Jordan/IdeaProjects/FreshAndFit/out/production/FreshAndFit/data/items.txt");
                    listModel.addElement(newItem);
                    updateItemSelector(itemSelector, itemsArea, currentCategory);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(adminDialog, "Please enter valid numeric values for price and stock.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Remove item logic
        removeItemButton.addActionListener(e -> {
            ClothingItem selected = itemList.getSelectedValue();
            if (selected != null) {
                inventoryManager.removeItem(selected);
                inventoryManager.saveInventory("C:/Users/Jordan/IdeaProjects/FreshAndFit/out/production/FreshAndFit/data/items.txt");
                listModel.removeElement(selected);
                updateItemSelector(itemSelector, itemsArea, currentCategory);
            }
        });

        // Edit item logic
        editItemButton.addActionListener(e -> {
            ClothingItem selected = itemList.getSelectedValue();
            if (selected != null) {
                JTextField nameField = new JTextField(selected.getName());
                JTextField priceField = new JTextField(String.valueOf(selected.getPrice()));
                JTextField categoryField = new JTextField(selected.getCategory());
                JTextField stockField = new JTextField(String.valueOf(selected.getStock()));
                Object[] inputs = {
                        "Name:", nameField,
                        "Price:", priceField,
                        "Category:", categoryField,
                        "Stock:", stockField
                };

                int result = JOptionPane.showConfirmDialog(adminDialog, inputs, "Edit Item", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String name = nameField.getText().trim();
                        double price = Double.parseDouble(priceField.getText().trim());
                        String category = categoryField.getText().trim();
                        int stock = Integer.parseInt(stockField.getText().trim());

                        if (price < 0 || stock < 0) {
                            JOptionPane.showMessageDialog(adminDialog, "Price and stock must be non-negative.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        selected.setName(name);
                        selected.setPrice(price);
                        selected.setCategory(category);
                        selected.setStock(stock);

                        inventoryManager.saveInventory("C:/Users/Jordan/IdeaProjects/FreshAndFit/out/production/FreshAndFit/data/items.txt");

                        itemList.repaint();
                        updateItemSelector(itemSelector, itemsArea, currentCategory);

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(adminDialog, "Please enter valid numeric values for price and stock.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        adminDialog.setLocationRelativeTo(parentFrame);
        adminDialog.setVisible(true);
    }
}
