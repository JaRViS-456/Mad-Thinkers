package view;

import controllers.InventoryController;
import models.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class SearchItemDialog extends JDialog {
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private InventoryController inventoryController;

    public SearchItemDialog(JFrame parent, InventoryController inventoryController) {  // ✅ Accept controller
        super(parent, "Search Item", true);
        this.inventoryController = inventoryController;
        initUI();
    }

    private void initUI() {
        setSize(700, 500);
        setLocationRelativeTo(getParent());
        getContentPane().setLayout(new BorderLayout());

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Criteria"));
        
        searchTypeCombo = new JComboBox<>(new String[]{"By ID", "By Name"});
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton closeBtn = new JButton("Close");
        
        searchBtn.setBackground(new Color(52, 152, 219));
        searchBtn.setForeground(new Color(0, 128, 192));
        closeBtn.setBackground(new Color(149, 165, 166));
        closeBtn.setForeground(new Color(0, 128, 192));
        
        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(searchTypeCombo);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(closeBtn);
        
        // Result Table
        String[] columns = {"ID", "Item Name", "Base Price", "Disc.%", "Final Price", "Stock", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Arial", Font.PLAIN, 12));
        resultTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Search Results"));
        
        // Event handlers
        searchBtn.addActionListener(e -> performSearch());
        closeBtn.addActionListener(e -> dispose());
        
        getContentPane().add(searchPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void performSearch() {
        tableModel.setRowCount(0);
        String keyword = searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search keyword!");
            return;
        }

        boolean searchById = searchTypeCombo.getSelectedIndex() == 0;
        ArrayList<Item> results = inventoryController.searchItems(keyword, searchById);  // ✅ Use controller

        for (Item item : results) {
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

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items found matching '" + keyword + "'");
        }
    }
}