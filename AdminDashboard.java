package view;

import controllers.BillingController;
import controllers.InventoryController;
import controllers.UserController;
import dao.CSVHandler;
import models.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {
    private Admin admin;
//    private ArrayList<User> users;
//    private ArrayList<Item> items;
    private UserController userController;
    private InventoryController inventoryController;

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    public AdminDashboard(Admin admin, ArrayList<User> users, ArrayList<Item> items) {
        this.admin = admin;
        this.userController = new UserController(users);
        this.inventoryController = new InventoryController(items);
        initUI();
        loadEmployeeData();
    }

    private void initUI() {
        setTitle("Admin Dashboard - Welcome " + admin.getName());

        // Frame background
        getContentPane().setBackground(new Color(240, 248, 255));  // Alice Blue
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setLocationRelativeTo(null);
        setResizable(false);
        

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Employee Table
        String[] columns = {"ID", "Name", "Role", "Shift", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setFont(new Font("Arial", Font.PLAIN, 12));
        employeeTable.setRowHeight(25);
        employeeTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Employee List"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 200));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("ADMIN DASHBOARD - Employee Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel adminLabel = new JLabel("Logged in as: " + admin.getName());
        adminLabel.setForeground(Color.WHITE);
        headerPanel.add(adminLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton addBtn = createButton("Add Employee", new Color(46, 204, 113));
        JButton updateBtn = createButton("Update Employee", new Color(52, 152, 219));
        JButton deleteBtn = createButton("Delete Employee", new Color(231, 76, 60));
        JButton reportBtn = createButton("Sales Report", new Color(155, 89, 182));
        JButton stockBtn = createButton("Stock Status", new Color(241, 196, 15));
        JButton restockBtn = createButton("Restock Item", new Color(52, 73, 94));  // NEW RESTOCK BUTTON
        JButton logoutBtn = createButton("Logout", new Color(149, 165, 166));

        addBtn.addActionListener(e -> addEmployee());
        updateBtn.addActionListener(e -> updateEmployee());
        deleteBtn.addActionListener(e -> deleteEmployee());
        reportBtn.addActionListener(e -> showSalesReport());
        stockBtn.addActionListener(e -> showStockStatus());
        restockBtn.addActionListener(e -> restockItem());  // NEW ACTION LISTENER
        logoutBtn.addActionListener(e -> logout());

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(reportBtn);
        buttonPanel.add(stockBtn);
        buttonPanel.add(restockBtn);  // ADD RESTOCK BUTTON TO PANEL
        buttonPanel.add(logoutBtn);

        return buttonPanel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);  // Fixed: Changed from red to white for better visibility
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(130, 35));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }

    private void loadEmployeeData() {
        tableModel.setRowCount(0);
        ArrayList<Employee> employees = userController.getAllEmployees();
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                    emp.getId(), emp.getName(), emp.getRole(),
                    emp.getShift(), emp.getPhone()
            });
        }
    }

    private void addEmployee() {
        AddEmployeeDialog dialog = new AddEmployeeDialog(this, userController);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadEmployeeData();
            JOptionPane.showMessageDialog(this, "Employee added successfully!");
        }
    }

    private void updateEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to update!");
            return;
        }

        String empId = (String) tableModel.getValueAt(selectedRow, 0);
        Employee emp = userController.findEmployeeById(empId);
        if (emp != null) {
            UpdateEmployeeDialog dialog = new UpdateEmployeeDialog(this, emp, userController);
            dialog.setVisible(true);
            if (dialog.isUpdated()) {
                loadEmployeeData();
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
            }
        }
    }

    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete!");
            return;
        }

        String empId = (String) tableModel.getValueAt(selectedRow, 0);
        String empName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete employee '" + empName + "'? This action cannot be undone!",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userController.deleteEmployee(empId);
            if (success) {
                loadEmployeeData();
                JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
            }
        }
    }

    private void showSalesReport() {
        BillingController billingController = new BillingController(
                inventoryController.getAllItems(),
                admin.getId()
        );
        new SalesReportDialog(this, billingController).setVisible(true);
    }

    private void showStockStatus() {
        String stockStatus = inventoryController.getStockStatus();
        JTextArea textArea = new JTextArea(stockStatus);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 500));

        JOptionPane.showConfirmDialog(this, scrollPane, "Stock Status",
                JOptionPane.CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== RESTOCK METHOD ====================
    private void restockItem() {
        // Use inventory controller to get items
        ArrayList<Item> items = inventoryController.getAllItems();
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items available to restock!");
            return;
        }

        String[] itemDisplayStrings = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String status = "";
            if (item.getStock() == 0) {
                status = " [OUT OF STOCK]";
            } else if (item.getStock() <= 2) {
                status = " [LOW STOCK]";
            }
            itemDisplayStrings[i] = item.getItemId() + " - " + item.getItemName() +
                    " (Current Stock: " + item.getStock() + ")" + status;
        }

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select item to restock:",
                "Restock Item",
                JOptionPane.QUESTION_MESSAGE,
                null,
                itemDisplayStrings,
                itemDisplayStrings[0]);

        if (selected == null || selected.isEmpty()) return;

        String itemId = selected.split(" - ")[0];

        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity to add:");
        if (qtyStr == null || qtyStr.isEmpty()) return;

        try {
            int quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0!");
                return;
            }

            boolean success = inventoryController.restockItem(itemId, quantity);
            if (success) {
                JOptionPane.showMessageDialog(this, "✓ Stock Updated Successfully!\nAdded: +" + quantity);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to restock item!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    // ==================== END RESTOCK ====================

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
            "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }


}