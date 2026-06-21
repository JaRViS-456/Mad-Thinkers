package ui;

import dao.CSVHandler;
import models.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class EmployeeDashboard extends JFrame {
    private Employee employee;
    private ArrayList<Item> items;
    private JTable itemTable;
    private DefaultTableModel tableModel;

    public EmployeeDashboard(Employee employee, ArrayList<Item> items) {
        this.employee = employee;
        this.items = items;
        initUI();
        loadItemData();
    }

    private void initUI() {
        setTitle("Employee Dashboard - Welcome " + employee.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Item Table
        String[] columns = {"ID", "Item Name", "Base Price", "Disc.%", "Final Price", "Stock", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemTable = new JTable(tableModel);
        itemTable.setFont(new Font("Arial", Font.PLAIN, 12));
        itemTable.setRowHeight(25);
        itemTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(itemTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Inventory Items"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(46, 134, 222));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("EMPLOYEE DASHBOARD - Inventory & Billing");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel empLabel = new JLabel(employee.getName() + " | Shift: " + employee.getShift());
        empLabel.setForeground(Color.WHITE);
        headerPanel.add(empLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton addBtn = createButton("Add Item", new Color(46, 204, 113));
        JButton searchBtn = createButton("Search Item", new Color(52, 152, 219));
        JButton updateBtn = createButton("Update Item", new Color(241, 196, 15));
        JButton deleteBtn = createButton("Delete Item", new Color(231, 76, 60));
        JButton billingBtn = createButton("Generate Bill", new Color(155, 89, 182));
        JButton refreshBtn = createButton("Refresh", new Color(52, 73, 94));
        JButton logoutBtn = createButton("Logout", new Color(149, 165, 166));

        addBtn.addActionListener(e -> addItem());
        searchBtn.addActionListener(e -> searchItem());
        updateBtn.addActionListener(e -> updateItem());
        deleteBtn.addActionListener(e -> deleteItem());
        billingBtn.addActionListener(e -> generateBill());
        refreshBtn.addActionListener(e -> loadItemData());
        logoutBtn.addActionListener(e -> logout());

        buttonPanel.add(addBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(billingBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(logoutBtn);

        return buttonPanel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(new Color(255, 0, 0));
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(120, 35));
        btn.setFocusPainted(false);
        return btn;
    }

    private void loadItemData() {
        tableModel.setRowCount(0);
        for (Item item : items) {
            tableModel.addRow(new Object[]{
                item.getItemId(),
                item.getItemName(),
                String.format("%.2f", item.getBasePrice()),
                String.format("%.1f", item.getDiscountPercent()),
                String.format("%.2f", item.calculatePrice()),
                item.getStock(),
                item.getType()
            });
        }
    }

    private void addItem() {
        AddItemDialog dialog = new AddItemDialog(this, items);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            CSVHandler.saveItems(items);
            loadItemData();
            JOptionPane.showMessageDialog(this, "Item added successfully!");
        }
    }

    private void searchItem() {
        SearchItemDialog dialog = new SearchItemDialog(this, items);
        dialog.setVisible(true);
    }

    private void updateItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to update!");
            return;
        }
        
        String itemId = (String) tableModel.getValueAt(selectedRow, 0);
        Item item = findItemById(itemId);
        if (item != null) {
            UpdateItemDialog dialog = new UpdateItemDialog(this, item);
            dialog.setVisible(true);
            if (dialog.isUpdated()) {
                CSVHandler.saveItems(items);
                loadItemData();
                JOptionPane.showMessageDialog(this, "Item updated successfully!");
            }
        }
    }

    private void deleteItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete!");
            return;
        }
        
        String itemId = (String) tableModel.getValueAt(selectedRow, 0);
        String itemName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Delete item '" + itemName + "'? This action cannot be undone!",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Item item = findItemById(itemId);
            items.remove(item);
            CSVHandler.saveItems(items);
            loadItemData();
            JOptionPane.showMessageDialog(this, "Item deleted successfully!");
        }
    }

    private void generateBill() {
        BillingDialog dialog = new BillingDialog(this, employee, items);
        dialog.setVisible(true);
        if (dialog.isBillGenerated()) {
            CSVHandler.saveItems(items);
            loadItemData();
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
            "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private Item findItemById(String id) {
        for (Item item : items) {
            if (item.getItemId().equalsIgnoreCase(id)) return item;
        }
        return null;
    }
}