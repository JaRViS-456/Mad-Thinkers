package dao;

import models.*;
import java.io.*;
import java.util.*;

public class  CSVHandler {
    private static final String DATA_DIR = "data";
    public static final String EMPLOYEES_FILE = DATA_DIR + "/employees.csv";
    public static final String ITEMS_FILE = DATA_DIR + "/items.csv";
    public static final String INVOICES_FILE = DATA_DIR + "/invoices.csv";

    private static void ensureDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdir();
    }

    // User operations
    public static ArrayList<User> loadUsers() {
        ArrayList<User> users = new ArrayList<>();
        ensureDataDir();
        
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] data = line.split(",");
                if (data.length < 6) continue;
                
                if (data[3].equalsIgnoreCase("Admin")) {
                    users.add(new Admin(data[0], data[1], data[2]));
                } else {
                    users.add(Employee.fromCsvRow(data));
                }
            }
        } catch (FileNotFoundException e) {
            createDefaultAdmin();
            return loadUsers(); // Retry after creating default admin
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    private static void createDefaultAdmin() {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(EMPLOYEES_FILE))) {
            pw.println("# id,name,password,role,shift,phone");
            pw.println("ADMIN001,Super Admin,admin123,Admin,N/A,N/A");
        } catch (IOException e) {
            System.err.println("Error creating default admin: " + e.getMessage());
        }
    }

    public static void saveUsers(ArrayList<User> users) {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(EMPLOYEES_FILE))) {
            pw.println("# id,name,password,role,shift,phone");
            for (User u : users) {
                pw.println(u.toCsvRow());
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    // Item operations
    public static ArrayList<Item> loadItems() {
        ArrayList<Item> items = new ArrayList<>();
        ensureDataDir();
        
        try (BufferedReader br = new BufferedReader(new FileReader(ITEMS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] data = line.split(",");
                if (data.length < 6) continue;
                
                if (data[5].equalsIgnoreCase("Discount")) {
                    items.add(DiscountItem.fromCsvRow(data));
                } else {
                    items.add(StandardItem.fromCsvRow(data));
                }
            }
        } catch (FileNotFoundException e) {
            // No items file yet, return empty list
        } catch (IOException e) {
            System.err.println("Error loading items: " + e.getMessage());
        }
        return items;
    }

    public static void saveItems(ArrayList<Item> items) {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            pw.println("# id,name,basePrice,discountPercent,stock,type");
            for (Item item : items) {
                pw.println(item.toCsvRow());
            }
        } catch (IOException e) {
            System.err.println("Error saving items: " + e.getMessage());
        }
    }

    // Invoice operations
    public static void appendBill(Bill bill) {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(INVOICES_FILE, true))) {
            pw.println(bill.toCsvRow());
        } catch (IOException e) {
            System.err.println("Error saving bill: " + e.getMessage());
        }
    }

    public static int loadMaxBillCounter() {
        int max = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(INVOICES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] data = line.split(",");
                if (data.length >= 1 && data[0].startsWith("BILL-")) {
                    try {
                        int num = Integer.parseInt(data[0].substring(5));
                        max = Math.max(max, num);
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException ignored) {}
        return max;
    }

    public static List<String[]> loadTodaysBills() {
        List<String[]> bills = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        String today = String.format("%04d-%02d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        
        try (BufferedReader br = new BufferedReader(new FileReader(INVOICES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] data = line.split(",", 5);
                if (data.length >= 4 && data[2].startsWith(today)) {
                    bills.add(data);
                }
            }
        } catch (IOException ignored) {}
        return bills;
    }
}