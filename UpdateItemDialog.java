package view;

import controllers.InventoryController;
import models.*;
import javax.swing.*;
import java.awt.*;

public class UpdateItemDialog extends JDialog {
    private Item item;
    private InventoryController inventoryController;  // ✅ Add controller
    private JTextField nameField, priceField, stockField, discountField;
    private boolean updated = false;

    public UpdateItemDialog(JFrame parent, Item item, InventoryController inventoryController) {  // ✅ Accept controller
        super(parent, "Update Item", true);
        this.item = item;
        this.inventoryController = inventoryController;
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
            String priceStr = priceField.getText().trim();
            String stockStr = stockField.getText().trim();

            Double price = priceStr.isEmpty() ? null : Double.parseDouble(priceStr);
            Integer stock = stockStr.isEmpty() ? null : Integer.parseInt(stockStr);
            Double discount = null;

            if (price != null && price < 0) {
                JOptionPane.showMessageDialog(this, "Price cannot be negative!");
                return;
            }
            if (stock != null && stock < 0) {
                JOptionPane.showMessageDialog(this, "Stock cannot be negative!");
                return;
            }

            if (item instanceof DiscountItem && discountField != null) {
                String discountStr = discountField.getText().trim();
                if (!discountStr.isEmpty()) {
                    discount = Double.parseDouble(discountStr);
                    if (discount < 0 || discount > 100) {
                        JOptionPane.showMessageDialog(this, "Discount must be between 0 and 100!");
                        return;
                    }
                }
            }

            boolean success = inventoryController.updateItem(  // ✅ Use controller
                    item.getItemId(),
                    name.isEmpty() ? null : name,
                    price,
                    stock,
                    discount
            );

            if (success) {
                updated = true;
                JOptionPane.showMessageDialog(this, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update item!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUpdated() { 
        return updated; 
    }
}