package view;

import controllers.BillingController;
import controllers.InventoryController;  // ✅ Added missing import
import models.*;
import dao.CSVHandler;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class BillingDialog extends JDialog {
    private final Employee employee;  // ✅ Made final
    private BillingController billingController;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel totalLabel;
    private JTextField itemIdField, quantityField;
    private boolean billGenerated = false;
    private ArrayList<Item> items;  // ✅ Added missing items field

    public BillingDialog(JFrame parent, Employee employee, InventoryController inventoryController) {
        super(parent, "Generate Bill - " + employee.getName(), true);
        this.employee = employee;
        this.items = inventoryController.getAllItems();  // ✅ Store items reference
        this.billingController = new BillingController(
                inventoryController.getAllItems(),
                employee.getId()
        );
        initUI();
    }

    private void initUI() {
        setSize(900, 600);
        setLocationRelativeTo(getParent());
        getContentPane().setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add Item Panel
        JPanel addPanel = createAddItemPanel();
        mainPanel.add(addPanel, BorderLayout.NORTH);

        // Cart Table
        String[] columns = {"#", "Item ID", "Item Name", "Unit Price", "Qty", "Total"};
        cartModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(30);
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel (Total + Buttons)
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    private JPanel createAddItemPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Add Item to Cart"));

        panel.add(new JLabel("Item ID:"));
        itemIdField = new JTextField(10);
        panel.add(itemIdField);

        panel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(5);
        panel.add(quantityField);

        JButton addBtn = new JButton("Add to Cart");
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);  // ✅ Fixed color
        addBtn.addActionListener(e -> addToCart());

        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.setBackground(new Color(231, 76, 60));
        removeBtn.setForeground(Color.WHITE);  // ✅ Fixed color
        removeBtn.addActionListener(e -> removeFromCart());

        panel.add(addBtn);
        panel.add(removeBtn);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        totalLabel = new JLabel("Grand Total: 0.00 BDT");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(new Color(231, 76, 60));
        panel.add(totalLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton clearBtn = new JButton("Clear Cart");
        clearBtn.setBackground(new Color(241, 196, 15));
        clearBtn.setForeground(Color.WHITE);  // ✅ Fixed color
        clearBtn.addActionListener(e -> clearCart());

        JButton generateBtn = new JButton("Generate Invoice");
        generateBtn.setBackground(new Color(155, 89, 182));
        generateBtn.setForeground(Color.WHITE);  // ✅ Fixed color
        generateBtn.addActionListener(e -> generateInvoice());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(149, 165, 166));
        cancelBtn.setForeground(Color.WHITE);  // ✅ Fixed color
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(clearBtn);
        buttonPanel.add(generateBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private void addToCart() {
        String itemId = itemIdField.getText().trim();
        String qtyStr = quantityField.getText().trim();

        if (itemId.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Item ID and Quantity!");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity!");
            return;
        }

        boolean success = billingController.addToCart(itemId, qty);
        if (success) {
            updateCartTable();
            updateTotal();
            itemIdField.setText("");
            quantityField.setText("");
            itemIdField.requestFocus();
            JOptionPane.showMessageDialog(this, "Added item to cart!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add item! Check ID or stock availability.");
        }
    }

    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove!");
            return;
        }

        boolean success = billingController.removeFromCart(selectedRow);
        if (success) {
            updateCartTable();
            updateTotal();
        }
    }

    private void clearCart() {
        int confirm = JOptionPane.showConfirmDialog(this, "Clear entire cart?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            billingController.clearCart();
            updateCartTable();
            updateTotal();
        }
    }

    private void updateCartTable() {
        cartModel.setRowCount(0);
        // ✅ Use billingController.getCurrentBill() instead of currentBill
        Bill currentBill = billingController.getCurrentBill();
        ArrayList<Item> cartItems = currentBill.getCartItems();
        ArrayList<Integer> cartQtys = currentBill.getCartQtys();

        for (int i = 0; i < cartItems.size(); i++) {
            Item item = cartItems.get(i);
            int qty = cartQtys.get(i);
            double total = item.calculatePrice() * qty;

            cartModel.addRow(new Object[]{
                    i + 1,
                    item.getItemId(),
                    item.getItemName(),
                    String.format("%.2f", item.calculatePrice()),
                    qty,
                    String.format("%.2f", total)
            });
        }
    }

    private void updateTotal() {
        // ✅ Use billingController.getCurrentBill() instead of currentBill
        totalLabel.setText(String.format("Grand Total: %.2f BDT",
                billingController.getCurrentBill().getGrandTotal()));
    }

    private void generateInvoice() {
        boolean success = billingController.generateBill();
        if (success) {
            showInvoice();
            billGenerated = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Cart is empty! Cannot generate invoice.");
        }
    }

    private void showInvoice() {
        String invoice = billingController.generateInvoiceString();
        JTextArea textArea = new JTextArea(invoice);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Invoice",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSalesReport() {
        StringBuilder report = new StringBuilder();
        report.append("══════════════════════════════════════════════════════════\n");
        report.append("                   DAILY SALES REPORT                     \n");
        report.append("══════════════════════════════════════════════════════════\n\n");

        java.util.List<String[]> bills = CSVHandler.loadTodaysBills();

        if (bills.isEmpty()) {
            report.append("No sales recorded today.\n");
        } else {
            double total = 0;
            report.append(String.format("%-12s %-15s %-20s %10s\n",
                    "Bill ID", "Employee", "Time", "Amount"));
            report.append("------------------------------------------------------------\n");

            for (String[] bill : bills) {
                String time = bill[2].length() >= 19 ? bill[2].substring(11, 19) : bill[2];
                double amount = Double.parseDouble(bill[3]);
                total += amount;
                report.append(String.format("%-12s %-15s %-20s %10.2f\n",
                        bill[0], bill[1], time, amount));
            }
            report.append("------------------------------------------------------------\n");
            report.append(String.format("TOTAL SALES: %.2f BDT\n", total));
        }

        JTextArea textArea = new JTextArea(report.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                "Sales Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showStockStatus() {
        StringBuilder status = new StringBuilder();
        status.append("══════════════════════════════════════════════════════════\n");
        status.append("                    STOCK STATUS                          \n");
        status.append("══════════════════════════════════════════════════════════\n\n");

        // ✅ Use 'items' field instead of undefined variable
        for (Item item : items) {
            String stockStatus;
            if (item.getStock() == 0) stockStatus = "OUT OF STOCK!";
            else if (item.getStock() <= 2) stockStatus = "LOW STOCK!";
            else stockStatus = "OK";

            status.append(String.format("%-8s %-20s Stock: %3d - %s\n",
                    item.getItemId(), item.getItemName(), item.getStock(), stockStatus));
        }

        JTextArea textArea = new JTextArea(status.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                "Stock Status", JOptionPane.INFORMATION_MESSAGE);
    }

    private Item findItemById(String id) {
        // ✅ Use 'items' field instead of undefined variable
        for (Item item : items) {
            if (item.getItemId().equalsIgnoreCase(id)) return item;
        }
        return null;
    }

    public boolean isBillGenerated() { return billGenerated; }
}