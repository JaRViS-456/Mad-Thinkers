package ui;

import models.*;
import javax.swing.*;
import java.awt.*;

public class UpdateItemDialog extends JDialog {
    private Item item;
    private JTextField nameField, priceField, stockField, discountField;
    private boolean updated = false;

    public UpdateItemDialog(JFrame parent, Item item) {
        super(parent, "Update Item", true);
        this.item = item;
        initUI();
    }

    private void initUI() {
        setSize(400, 380);
        setLocationRelativeTo(getParent());
        getContentPane().setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID display (non-editable) - with separate constraints
        GridBagConstraints idLabelConstraints = new GridBagConstraints();
        idLabelConstraints.gridx = 0;
        idLabelConstraints.gridy = 0;
        idLabelConstraints.insets = new Insets(8, 8, 8, 8);
        idLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("Item ID:"), idLabelConstraints);
        
        JLabel idLabel = new JLabel(item.getItemId());
        idLabel.setFont(new Font("Arial", Font.BOLD, 12));
        GridBagConstraints idValueConstraints = new GridBagConstraints();
        idValueConstraints.gridx = 1;
        idValueConstraints.gridy = 0;
        idValueConstraints.insets = new Insets(8, 8, 8, 8);
        idValueConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(idLabel, idValueConstraints);

        // Initialize fields with current values
        nameField = new JTextField(item.getItemName(), 15);
        priceField = new JTextField(String.valueOf(item.getBasePrice()), 15);
        stockField = new JTextField(String.valueOf(item.getStock()), 15);
        
        // Add form fields
        addField(formPanel, "Item Name:", nameField, 1);
        addField(formPanel, "Base Price:", priceField, 2);
        addField(formPanel, "Stock:", stockField, 3);

        // Add discount field if item is a DiscountItem
        if (item instanceof DiscountItem) {
            discountField = new JTextField(String.valueOf(((DiscountItem) item).getDiscountPercent()), 15);
            addField(formPanel, "Discount %:", discountField, 4);
        }

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton updateBtn = new JButton("Update");
        JButton cancelBtn = new JButton("Cancel");
        updateBtn.setBackground(new Color(52, 152, 219));
        updateBtn.setForeground(new Color(255, 0, 0));
        cancelBtn.setBackground(new Color(149, 165, 166));
        cancelBtn.setForeground(new Color(255, 0, 0));
        
        updateBtn.addActionListener(e -> updateItem());
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addField(JPanel panel, String label, JComponent field, int row) {
        // Create separate constraints for label
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

    private void updateItem() {
        try {
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());

            // Validation
            if (name.isEmpty() && price < 0 && stock < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Please update at least one field with valid values!", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (price < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Price cannot be negative!", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (stock < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Stock cannot be negative!", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Update fields if they have valid values
            if (!name.isEmpty()) {
                item.setItemName(name);
            }
            if (price >= 0) {
                item.setBasePrice(price);
            }
            if (stock >= 0) {
                item.setStock(stock);
            }

            // Update discount if applicable
            if (item instanceof DiscountItem && discountField != null) {
                double discount = Double.parseDouble(discountField.getText().trim());
                if (discount < 0 || discount > 100) {
                    JOptionPane.showMessageDialog(this, 
                        "Discount must be between 0 and 100!", 
                        "Validation Error", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                ((DiscountItem) item).setDiscountPercent(discount);
            }

            updated = true;
            JOptionPane.showMessageDialog(this, 
                "Item updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numbers for price, stock, and discount!", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUpdated() { 
        return updated; 
    }
}