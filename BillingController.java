package controllers;

import models.*;
import dao.CSVHandler;
import java.util.ArrayList;
import java.util.List;

public class BillingController {
    private Bill currentBill;
    private ArrayList<Item> items;
    private String employeeId;

    public BillingController(ArrayList<Item> items, String employeeId) {
        this.items = items;
        this.employeeId = employeeId;
        this.currentBill = new Bill(employeeId);
    }

    public boolean addToCart(String itemId, int quantity) {
        if (itemId == null || itemId.trim().isEmpty() || quantity <= 0) {
            return false;
        }

        Item item = findItemById(itemId.trim());
        if (item == null) {
            return false;
        }

        if (quantity > item.getStock()) {
            return false;
        }

        currentBill.addToCart(item, quantity);
        return true;
    }

    public boolean removeFromCart(int index) {
        if (index < 0 || index >= currentBill.getCartItems().size()) {
            return false;
        }
        currentBill.removeFromCart(index);
        return true;
    }

    public boolean updateQuantity(int index, int newQuantity) {
        if (index < 0 || index >= currentBill.getCartItems().size() || newQuantity <= 0) {
            return false;
        }

        Item item = currentBill.getCartItems().get(index);
        if (newQuantity > item.getStock()) {
            return false;
        }

        currentBill.updateQuantity(index, newQuantity);
        return true;
    }

    public void clearCart() {
        while (currentBill.getCartItems().size() > 0) {
            currentBill.removeFromCart(0);
        }
    }

    public boolean generateBill() {
        if (currentBill.getCartItems().isEmpty()) {
            return false;
        }

        // Reduce stock for all items
        ArrayList<Item> cartItems = currentBill.getCartItems();
        ArrayList<Integer> cartQtys = currentBill.getCartQtys();

        for (int i = 0; i < cartItems.size(); i++) {
            cartItems.get(i).reduceStock(cartQtys.get(i));
        }

        // Save bill
        CSVHandler.appendBill(currentBill);
        CSVHandler.saveItems(items);
        return true;
    }

    public Bill getCurrentBill() {
        return currentBill;
    }

    public List<String[]> getDailySalesReport() {
        return CSVHandler.loadTodaysBills();
    }

    private Item findItemById(String id) {
        for (Item item : items) {
            if (item.getItemId().equalsIgnoreCase(id)) {
                return item;
            }
        }
        return null;
    }

    public String generateInvoiceString() {
        StringBuilder invoice = new StringBuilder();
        invoice.append("══════════════════════════════════════════════════════════\n");
        invoice.append("                 SUPERSHOP CUSTOMER INVOICE               \n");
        invoice.append("══════════════════════════════════════════════════════════\n");
        invoice.append(String.format(" Bill ID    : %-40s \n", currentBill.getBillId()));
        invoice.append(String.format(" Date/Time  : %-40s \n", currentBill.getDateTime()));
        invoice.append("══════════════════════════════════════════════════════════\n");
        invoice.append(" Item                    Qty     Unit Price     Total     \n");
        invoice.append("══════════════════════════════════════════════════════════\n");

        ArrayList<Item> cartItems = currentBill.getCartItems();
        ArrayList<Integer> cartQtys = currentBill.getCartQtys();

        for (int i = 0; i < cartItems.size(); i++) {
            Item item = cartItems.get(i);
            int qty = cartQtys.get(i);
            double unitPrice = item.calculatePrice();
            double total = unitPrice * qty;

            String itemName = item.getItemName();
            if (itemName.length() > 20) itemName = itemName.substring(0, 20);

            invoice.append(String.format(" %-20s %5d %11.2f %10.2f \n",
                    itemName, qty, unitPrice, total));
        }

        invoice.append("══════════════════════════════════════════════════════════\n");
        invoice.append(String.format(" GRAND TOTAL (BDT): %30.2f \n", currentBill.getGrandTotal()));
        invoice.append("══════════════════════════════════════════════════════════\n");
        invoice.append("         Thank you for shopping at SuperShop!\n");

        return invoice.toString();
    }
}