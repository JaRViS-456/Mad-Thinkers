package models;

public class DiscountItem extends Item {
    private double discountPercent;

    public DiscountItem(String itemId, String itemName, double basePrice, double discountPercent, int stock) {
        super(itemId, itemName, basePrice, stock);
        this.discountPercent = discountPercent;
    }

    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }

    @Override
    public double calculatePrice() {
        return basePrice * (1.0 - discountPercent / 100.0);
    }

    @Override
    public String getType() {
        return "Discount";
    }

    @Override
    public String toCsvRow() {
        return itemId + "," + itemName + "," + basePrice + "," + discountPercent + "," + stock + ",Discount";
    }

    public static DiscountItem fromCsvRow(String[] data) {
        return new DiscountItem(data[0], data[1], Double.parseDouble(data[2]), 
                               Double.parseDouble(data[3]), Integer.parseInt(data[4]));
    }
}