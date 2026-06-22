package view;

import controllers.InventoryController;
import models.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AddItemDialog extends JDialog {
    private JTextField idField, nameField, priceField, stockField, discountField;
    private JComboBox<String> typeCombo;
    private boolean confirmed = false;
    private InventoryController inventoryController;

    public AddItemDialog(JFrame parent, InventoryController inventoryController) {
        super(parent, "Add New Item", true);
        this.inventoryController = inventoryController;
        initUI();
    }

    private void initUI() {
        setSize(400, 420);
        setLocationRelativeTo(getParent());
        getContentPane().setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add form fields
        addField(formPanel, gbc, "Item ID:", idField = new JTextField(15), 0);
        addField(formPanel, gbc, "Item Name:", nameField = new JTextField(15), 1);
        addField(formPanel, gbc, "Base Price (BDT):", priceField = new JTextField(15), 2);
        addField(formPanel, gbc, "Stock Quantity:", stockField = new JTextField(15), 3);
        
        // Item Type
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 4;
        labelConstraints.insets = new Insets(8, 8, 8, 8);
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("Item Type:"), labelConstraints);
        
        GridBagConstraints comboConstraints = new GridBagConstraints();
        comboConstraints.gridx = 1;
        comboConstraints.gridy = 4;
        comboConstraints.insets = new Insets(8, 8, 8, 8);
        comboConstraints.fill = GridBagConstraints.HORIZONTAL;
        typeCombo = new JComboBox<>(new String[]{"Standard", "Discount"});
        formPanel.add(typeCombo, comboConstraints);
        
        // Discount field
        labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 5;
        labelConstraints.insets = new Insets(8, 8, 8, 8);
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("Discount %:"), labelConstraints);
        
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = 5;
        fieldConstraints.insets = new Insets(8, 8, 8, 8);
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        discountField = new JTextField("0", 15);
        discountField.setEnabled(false);
        formPanel.add(discountField, fieldConstraints);
        
        // Enable discount field only when Discount type is selected
        typeCombo.addActionListener(e -> 
            discountField.setEnabled(typeCombo.getSelectedIndex() == 1));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(new Color(0, 128, 192));
        cancelBtn.setBackground(new Color(149, 165, 166));
        cancelBtn.setForeground(new Color(0, 128, 192));
        
        saveBtn.addActionListener(e -> saveItem());
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        // Create separate constraints for label to avoid reuse issues
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.insets = new Insets(8, 8, 8, 8);
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel(label), labelConstraints);
        
        // Create separate constraints for field
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.insets = new Insets(8, 8, 8, 8);
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, fieldConstraints);
    }

    private void saveItem() {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID and Name are required!");
                return;
            }
            if (price < 0 || stock < 0) {
                JOptionPane.showMessageDialog(this, "Price and stock cannot be negative!");
                return;
            }

            String type = typeCombo.getSelectedIndex() == 1 ? "Discount" : "Standard";
            double discount = 0;
            if (typeCombo.getSelectedIndex() == 1) {
                discount = Double.parseDouble(discountField.getText().trim());
                if (discount < 0 || discount > 100) {
                    JOptionPane.showMessageDialog(this, "Discount must be between 0 and 100!");
                    return;
                }
            }

            boolean success = inventoryController.addItem(id, name, price, stock, type, discount);
            if (success) {
                confirmed = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add item. ID may already exist!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for price, stock, and discount!");
        }
    }

    public boolean isConfirmed() { 
        return confirmed; 
    }
}