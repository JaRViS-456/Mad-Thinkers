package view;

import controllers.UserController;
import models.*;
import javax.swing.*;
import java.awt.*;

public class UpdateEmployeeDialog extends JDialog {
    private Employee employee;
    private UserController userController;
    private JTextField nameField, shiftField, phoneField;
    private JPasswordField passwordField;
    private boolean updated = false;

    public UpdateEmployeeDialog(JFrame parent, Employee employee, UserController userController) {
        super(parent, "Update Employee", true);
        this.employee = employee;
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

        // Employee ID display (non-editable)
        JLabel idLabel = new JLabel("Employee ID:");
        JLabel idValue = new JLabel(employee.getId());
        idValue.setFont(new Font("Arial", Font.BOLD, 12));

        nameField = new JTextField(employee.getName(), 15);
        passwordField = new JPasswordField(15);
        shiftField = new JTextField(employee.getShift(), 15);
        phoneField = new JTextField(employee.getPhone(), 15);

        // Add ID field with separate constraints
        GridBagConstraints idLabelConstraints = new GridBagConstraints();
        idLabelConstraints.gridx = 0;
        idLabelConstraints.gridy = 0;
        idLabelConstraints.insets = new Insets(8, 8, 8, 8);
        idLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(idLabel, idLabelConstraints);

        GridBagConstraints idValueConstraints = new GridBagConstraints();
        idValueConstraints.gridx = 1;
        idValueConstraints.gridy = 0;
        idValueConstraints.insets = new Insets(8, 8, 8, 8);
        idValueConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(idValue, idValueConstraints);

        // Add form fields
        addField(formPanel, "Name:", nameField, 1);
        addField(formPanel, "New Password:", passwordField, 2);
        addField(formPanel, "Shift:", shiftField, 3);
        addField(formPanel, "Phone:", phoneField, 4);

        // Add note label
        JLabel noteLabel = new JLabel("(Leave password empty to keep current)");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        noteLabel.setForeground(Color.GRAY);

        GridBagConstraints noteConstraints = new GridBagConstraints();
        noteConstraints.gridx = 0;
        noteConstraints.gridy = 5;
        noteConstraints.gridwidth = 2;
        noteConstraints.insets = new Insets(8, 8, 8, 8);
        noteConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(noteLabel, noteConstraints);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton updateBtn = new JButton("Update");
        JButton cancelBtn = new JButton("Cancel");
        updateBtn.setBackground(new Color(52, 152, 219));
        updateBtn.setForeground(new Color(255, 0, 0));
        cancelBtn.setBackground(new Color(149, 165, 166));
        cancelBtn.setForeground(new Color(255, 0, 0));

        updateBtn.addActionListener(e -> updateEmployee());
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

    private void updateEmployee() {
        String name = nameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String shift = shiftField.getText().trim();
        String phone = phoneField.getText().trim();

        // Use controller to update employee
        boolean success = userController.updateEmployee(
                employee.getId(),
                name.isEmpty() ? null : name,
                password.isEmpty() ? null : password,
                shift.isEmpty() ? null : shift,
                phone.isEmpty() ? null : phone
        );

        if (success) {
            updated = true;
            JOptionPane.showMessageDialog(this, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update employee!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUpdated() {
        return updated;
    }
}