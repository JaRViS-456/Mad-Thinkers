package models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Bill {
    private static int counter = 1;
    private String billId;
    private String employeeId;
    private String dateTime;
    private double grandTotal;
    private ArrayList<Item> cartItems;
    private ArrayList<Integer> cartQtys;

    public Bill(String employeeId) {
        this.billId = String.format("BILL-%05d", counter++);
        this.employeeId = employeeId;
        this.dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.grandTotal = 0.0;
        this.cartItems = new ArrayList<>();
        this.cartQtys = new ArrayList<>();
    }

    public static void setCounter(int n) { counter = n; }
    public static int getCounter() { return counter; }
    public String getBillId() { return billId; }
    public String getDateTime() { return dateTime; }
    public double getGrandTotal() { return grandTotal; }
    public ArrayList<Item> getCartItems() { return cartItems; }
    public ArrayList<Integer> getCartQtys() { return cartQtys; }

    public void addToCart(Item item, int qty) {
        cartItems.add(item);
        cartQtys.add(qty);
        grandTotal += item.calculatePrice() * qty;
    }

    public void removeFromCart(int index) {
        if (index >= 0 && index < cartItems.size()) {
            grandTotal -= cartItems.get(index).calculatePrice() * cartQtys.get(index);
            cartItems.remove(index);
            cartQtys.remove(index);
        }
    }

    public void updateQuantity(int index, int newQty) {
        if (index >= 0 && index < cartItems.size()) {
            grandTotal -= cartItems.get(index).calculatePrice() * cartQtys.get(index);
            cartQtys.set(index, newQty);
            grandTotal += cartItems.get(index).calculatePrice() * newQty;
        }
    }

    public String toCsvRow() {
        return billId + "," + employeeId + "," + dateTime + "," + 
               String.format("%.2f", grandTotal) + "," + cartItems.size();
    }
}