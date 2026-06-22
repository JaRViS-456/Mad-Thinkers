package view;

import controllers.BillingController;
import dao.CSVHandler;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SalesReportDialog extends JDialog {
    private BillingController billingController;
    public SalesReportDialog(JFrame parent, BillingController billingController) {
        super(parent, "Daily Sales Report", true);
        this.billingController = billingController;
        initUI();
    }

    private void initUI() {
        setSize(600, 450);
        setLocationRelativeTo(getParent());
        
        JTextArea reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setEditable(false);
        
        StringBuilder report = new StringBuilder();
        report.append("══════════════════════════════════════════════════════════\n");
        report.append("                   DAILY SALES REPORT                     \n");
        report.append("══════════════════════════════════════════════════════════\n\n");
        
        List<String[]> bills = CSVHandler.loadTodaysBills();
        
        if (bills.isEmpty()) {
            report.append("No sales recorded today.\n");
        } else {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            String today = String.format("%04d-%02d-%02d",
                cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.DAY_OF_MONTH));
            report.append("Date: ").append(today).append("\n\n");
            
            report.append(String.format("%-12s %-15s %-20s %10s\n", 
                "Bill ID", "Employee", "Time", "Amount"));
            report.append("------------------------------------------------------------\n");
            
            double total = 0;
            int count = 0;
            
            for (String[] bill : bills) {
                String time = bill[2].length() >= 19 ? bill[2].substring(11, 19) : bill[2];
                double amount = Double.parseDouble(bill[3]);
                total += amount;
                count++;
                report.append(String.format("%-12s %-15s %-20s %10.2f\n", 
                    bill[0], bill[1], time, amount));
            }
            
            report.append("------------------------------------------------------------\n");
            report.append(String.format("Total Transactions: %d\n", count));
            report.append(String.format("NET DAILY SALES: %.2f BDT\n", total));
        }
        
        reportArea.setText(report.toString());
        add(new JScrollPane(reportArea));
    }
}