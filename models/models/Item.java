package models;

public abstract class Item {
    protected String itemId;
    protected String itemName;
    protected double basePrice;
    protected int stock;

    public Item(String itemId, String itemName, double basePrice, int stock) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.basePrice = basePrice;
        this.stock = stock;
    }

    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public double getBasePrice() { return basePrice; }
    public int getStock() { return stock; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public void setStock(int stock) { this.stock = stock; }

    public abstract double calculatePrice();
    public abstract double getDiscountPercent();
    public abstract String getType();
    public abstract String toCsvRow();

    public void reduceStock(int qty) {
        this.stock -= qty;
    }
}