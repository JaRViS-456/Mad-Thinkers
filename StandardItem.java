package models;

public class StandardItem extends Item {
    public StandardItem(String itemId, String itemName, double basePrice, int stock) {
        super(itemId, itemName, basePrice, stock);
    }

    @Override
    public double calculatePrice() {
        return basePrice;
    }

    @Override
    public double getDiscountPercent() {
        return 0.0;
    }

    @Override
    public String getType() {
        return "Standard";
    }

    @Override
    public String toCsvRow() {
        return itemId + "," + itemName + "," + basePrice + ",0.0," + stock + ",Standard";
    }

    public static StandardItem fromCsvRow(String[] data) {
        return new StandardItem(data[0], data[1], Double.parseDouble(data[2]), Integer.parseInt(data[4]));
    }
}