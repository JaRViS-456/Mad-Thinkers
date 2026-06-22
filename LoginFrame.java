package view;

import controllers.AuthController;
import dao.CSVHandler;
import models.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LoginFrame extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;
//    private ArrayList<User> users;
//    private ArrayList<Item> items;
private AuthController authController;

    public LoginFrame() {
//        users = CSVHandler.loadUsers();
//        items = CSVHandler.loadItems();
        authController = new AuthController();
        
//        int lastBill = CSVHandler.loadMaxBillCounter();
//        Bill.setCounter(lastBill + 1);
        
        initUI();
    }

    private void initUI() {
        setTitle("SuperShop Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
//        setLocation(100,100);
        setResizable(false);

        ImageIcon image=new ImageIcon("icon.png");
        setIconImage(image.getImage());
        

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0, 0, 0));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 200));
        JLabel titleLabel = new JLabel("SUPERSHOP MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
//        add(headerPanel,BorderLayout.NORTH); 

        // Login Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        
        // User type label
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel userTypeLabel = new JLabel();
        userTypeLabel.setText("Login as:");
        userTypeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(userTypeLabel,gbc);

        // Radio panel
        JRadioButton adminRadio = new JRadioButton("Admin", true);
        JRadioButton employeeRadio = new JRadioButton("Employee");
        ButtonGroup group = new ButtonGroup();
        group.add(adminRadio);
        group.add(employeeRadio);  
        
        JPanel radioPanel = new JPanel(new FlowLayout());
        radioPanel.add(adminRadio);
        radioPanel.add(employeeRadio);
        radioPanel.setBackground(new Color(240, 248, 255));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(radioPanel, gbc);

        // ID label
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(idLabel, gbc);
        
        // ID field
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        idField = new JTextField(15);
        idField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(idField, gbc);

        // Password label
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passLabel, gbc);
        
        // Password field
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(0, 128, 192));
        loginBtn.setForeground(new Color(255, 0, 0));
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setPreferredSize(new Dimension(100, 35));
        
        JButton exitBtn = new JButton("Exit");
        exitBtn.setBackground(new Color(0, 128, 192));
        exitBtn.setForeground(new Color(255, 0, 0));
        exitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        exitBtn.setPreferredSize(new Dimension(100, 35));
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(exitBtn);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(70, 130, 200));
        JLabel footerLabel = new JLabel("© 2026 Develop By Mad Thinkers");
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("Roboto", Font.BOLD, 12));
        footerPanel.add(footerLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);

        // Event Handlers
        loginBtn.addActionListener(e -> handleLogin(adminRadio.isSelected()));
        exitBtn.addActionListener(e -> System.exit(0));
        
        // Enter key to login
        getRootPane().setDefaultButton(loginBtn);
    }

    private void handleLogin(boolean isAdmin) {
        String id = idField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both ID and Password!", 
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

//        User user = findUserById(id);
        // Use AuthController for authentication
        User user = authController.authenticate(id, password, isAdmin);

        if (user != null) {
            if (isAdmin && user instanceof Admin) {
                // Pass controllers to dashboards
                new AdminDashboard(
                        (Admin) user,
                        authController.getAllUsers(),
                        authController.getAllItems()
                ).setVisible(true);
                dispose();
            } else if (!isAdmin && user instanceof Employee) {
                new EmployeeDashboard(
                        (Employee) user,
                        authController.getAllItems()
                ).setVisible(true);
                dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid ID or Password or Role Mismatch!",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }
}