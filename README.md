### Folder Structure
```
MyStoreApp/  
├── StoreApp.jar  
├── data/  
│ ├── items.txt  
│ └── images/  
│ ├── tshirt.png  
│ └── jeans.png
```
---

## 🛍️ App Features

-   Browse and filter clothing items by category
    
-   Add/remove items from a shopping cart
    
-   View total cost at checkout
    
-   Preview item details (name, price, stock, image)
    
-   Admin panel for adding/removing/modifying items and stock
    

---

## 📝 Editing Inventory

The file `data/items.txt` contains all store inventory in this format:

```
ItemName,Category,Price,Stock,data/images/image-name.png
```

Example:

```
T-Shirt,Shirts,19.99,10,data/images/tshirt.png
```

You can manually edit this file or use the built-in **Admin Panel** in the app to make changes (these changes are temporary unless saved manually).

---

## 🧩 Troubleshooting
-   **Items not showing up?**  
    Make sure `data/items.txt` exists and image paths inside it are correct and relative to the JAR.
    
-   **Images not loading?**  
    Check that each path in `items.txt` is valid, and that images are present in `data/images/`.


