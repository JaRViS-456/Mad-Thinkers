package controllers;

import models.*;
import dao.CSVHandler;
import java.util.ArrayList;

public class InventoryController {
    private ArrayList<Item> items;

    public InventoryController(ArrayList<Item> items) {
        this.items = items;
    }

    public boolean addItem(String id, String name, double price, int stock, String type, double discount) {
        // Validation
        if (id == null || id.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            return false;
        }
        if (price < 0 || stock < 0) {
            return false;
        }

        // Check for duplicate ID
        for (Item item : items) {
            if (item.getItemId().equalsIgnoreCase(id.trim())) {
                return false;
            }
        }

        Item item;
        if (type.equalsIgnoreCase("Discount")) {
            if (discount < 0 || discount > 100) {
                return false;
            }
            item = new DiscountItem(id.trim(), name.trim(), price, discount, stock);
        } else {
            item = new StandardItem(id.trim(), name.trim(), price, stock);
        }

        items.add(item);
        CSVHandler.saveItems(items);
        return true;
    }

    public boolean updateItem(String id, String name, Double price, Integer stock, Double discount) {
        Item item = findItemById(id);
        if (item == null) return false;

        if (name != null && !name.trim().isEmpty()) {
            item.setItemName(name.trim());
        }
        if (price != null && price >= 0) {
            item.setBasePrice(price);
        }
        if (stock != null && stock >= 0) {
            item.setStock(stock);
        }
        if (discount != null && item instanceof DiscountItem) {
            if (discount >= 0 && discount <= 100) {
                ((DiscountItem) item).setDiscountPercent(discount);
            }
        }

        CSVHandler.saveItems(items);
        return true;
    }

    public boolean deleteItem(String id) {
        Item item = findItemById(id);
        if (item == null) return false;

        items.remove(item);
        CSVHandler.saveItems(items);
        return true;
    }

    public boolean restockItem(String id, int quantity) {
        Item item = findItemById(id);
        if (item == null || quantity <= 0) return false;

        int oldStock = item.getStock();
        item.setStock(oldStock + quantity);
        CSVHandler.saveItems(items);
        return true;
    }

    public Item findItemById(String id) {
        for (Item item : items) {
            if (item.getItemId().equalsIgnoreCase(id)) {
                return item;
            }
        }
        return null;
    }

    public ArrayList<Item> searchItems(String keyword, boolean searchById) {
        ArrayList<Item> results = new ArrayList<>();
        keyword = keyword.toLowerCase().trim();

        for (Item item : items) {
            boolean match = searchById ?
                    item.getItemId().toLowerCase().contains(keyword) :
                    item.getItemName().toLowerCase().contains(keyword);
            if (match) {
                results.add(item);
            }
        }
        return results;
    }

    public ArrayList<Item> getAllItems() {
        return items;
    }

    public String getStockStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════════════════════════════════════════════\n");
        sb.append("                              INVENTORY STOCK STATUS                          \n");
        sb.append("══════════════════════════════════════════════════════════════════════════════\n");
        sb.append("   Item ID       Item Name           Price    Stock          Status           \n");
        sb.append("══════════════════════════════════════════════════════════════════════════════\n");

        for (Item item : items) {
            String status;
            if (item.getStock() == 0) {
                status = "❌ OUT OF STOCK!";
            } else if (item.getStock() <= 2) {
                status = "⚠️ LOW STOCK!";
            } else {
                status = "✓ In Stock";
            }

            String name = item.getItemName();
            if (name.length() > 20) name = name.substring(0, 17) + "...";

            sb.append(String.format(" %-10s  %-20s  %9.2f  %6d  %-22s \n",
                    item.getItemId(), name, item.calculatePrice(), item.getStock(), status));
        }
        sb.append("══════════════════════════════════════════════════════════════════════════════\n");
        return sb.toString();
    }
}