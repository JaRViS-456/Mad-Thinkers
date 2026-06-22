package view;

import controllers.UserController;
import models.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AddEmployeeDialog extends JDialog {
    private JTextField idField, nameField, shiftField, phoneField;
    private JPasswordField passwordField;
    private boolean confirmed = false;
//    private ArrayList<User> users;
    private UserController userController;

    public AddEmployeeDialog(JFrame parent, UserController userController) {
        super(parent, "Add New Employee", true);
        this.userController = userController;
        initUI();
    }

    private void initUI() {
        setSize(400, 350);
        setLocationRelativeTo(getParent());
        getContentPane().setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        addField(formPanel, gbc, "Employee ID:", idField = new JTextField(15), 0);
        addField(formPanel, gbc, "Full Name:", nameField = new JTextField(15), 1);
        addField(formPanel, gbc, "Password:", passwordField = new JPasswordField(15), 2);
        addField(formPanel, gbc, "Shift (Day/Night):", shiftField = new JTextField(15), 3);
        addField(formPanel, gbc, "Phone Number:", phoneField = new JTextField(15), 4);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        saveBtn.setBackground(new Color(0, 128, 192));
        saveBtn.setForeground(new Color(0, 128, 192));
        cancelBtn.setBackground(new Color(149, 165, 166));
        cancelBtn.setForeground(new Color(0, 128, 192));
        
        saveBtn.addActionListener(e -> saveEmployee());
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void saveEmployee() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String shift = shiftField.getText().trim();
        String phone = phoneField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID, Name and Password are required!");
            return;
        }

        // Use controller to add employee
        boolean success = userController.addEmployee(id, name, password, shift, phone);

        if (success) {
            confirmed = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add employee. ID may already exist!");
        }
    }

    public boolean isConfirmed() { return confirmed; }
}