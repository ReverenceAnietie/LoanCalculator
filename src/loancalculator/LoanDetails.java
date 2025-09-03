package loancalculator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;

public class LoanDetails extends JFrame {
    private double principal;
    private double annualRate;
    private int termMonths;
    private JTable amortizationTable;
    private DefaultTableModel tableModel;
    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
    
    public LoanDetails(double principal, double annualRate, int termMonths) {
        this.principal = principal;
        this.annualRate = annualRate;
        this.termMonths = termMonths;
        
        initializeComponents();
        setupLayout();
        generateAmortizationSchedule();
        setupWindow();
    }
    
    private void initializeComponents() {
        String[] columnNames = {"Payment #", "Payment Amount", "Principal", 
                               "Interest", "Remaining Balance"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        amortizationTable = new JTable(tableModel);
        amortizationTable.setRowHeight(25);
        amortizationTable.getTableHeader().setBackground(new Color(70, 130, 180));
        amortizationTable.getTableHeader().setForeground(Color.WHITE);
        amortizationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        amortizationTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        amortizationTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        amortizationTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        amortizationTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        amortizationTable.getColumnModel().getColumn(4).setPreferredWidth(140);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 25, 112));
        JLabel titleLabel = new JLabel("Detailed Amortization Schedule", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        headerPanel.add(titleLabel);
        
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Loan Summary"));
        summaryPanel.setBackground(Color.WHITE);
        
        JLabel principalLabel = new JLabel("Principal: " + currencyFormat.format(principal), SwingConstants.CENTER);
        JLabel rateLabel = new JLabel("Rate: " + String.format("%.2f%%", annualRate * 100), SwingConstants.CENTER);
        JLabel termLabel = new JLabel("Term: " + termMonths + " months", SwingConstants.CENTER);
        
        summaryPanel.add(principalLabel);
        summaryPanel.add(rateLabel);
        summaryPanel.add(termLabel);
        
        JScrollPane scrollPane = new JScrollPane(amortizationTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Payment Schedule"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton printButton = new JButton("Print Schedule");
        JButton exportButton = new JButton("Export to CSV");
        JButton closeButton = new JButton("Close");
        
        printButton.setBackground(new Color(60, 179, 113));
        printButton.setForeground(Color.BLACK);
        printButton.setFocusPainted(false);
        
        exportButton.setBackground(new Color(255, 140, 0));
        exportButton.setForeground(Color.BLACK);
        exportButton.setFocusPainted(false);
        
        closeButton.setBackground(new Color(105, 105, 105));
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);
        
        buttonPanel.add(printButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);
        
        printButton.addActionListener(e -> printSchedule());
        exportButton.addActionListener(e -> exportSchedule());
        closeButton.addActionListener(e -> dispose());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void generateAmortizationSchedule() {
        double monthlyRate = annualRate / 12;
        double monthlyPayment;
        
        if (monthlyRate == 0) {
            monthlyPayment = principal / termMonths;
        } else {
            monthlyPayment = principal * (monthlyRate * Math.pow(1 + monthlyRate, termMonths)) / 
                           (Math.pow(1 + monthlyRate, termMonths) - 1);
        }
        
        double remainingBalance = principal;
        
        for (int i = 1; i <= termMonths; i++) {
            double interestPayment = remainingBalance * monthlyRate;
            double principalPayment = monthlyPayment - interestPayment;
            remainingBalance -= principalPayment;
            
            if (remainingBalance < 0.01) remainingBalance = 0;
            
            Object[] row = {
                i,
                currencyFormat.format(monthlyPayment),
                currencyFormat.format(principalPayment),
                currencyFormat.format(interestPayment),
                currencyFormat.format(remainingBalance)
            };
            
            tableModel.addRow(row);
        }
    }
    
    private void printSchedule() {
        try {
            boolean printed = amortizationTable.print();
            if (printed) {
                JOptionPane.showMessageDialog(this, "Schedule printed successfully!", 
                                            "Print Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to print: " + e.getMessage(), 
                                        "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportSchedule() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("amortization_schedule.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                writer.println("Payment #,Payment Amount,Principal,Interest,Remaining Balance");
                
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    StringBuilder row = new StringBuilder();
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        if (j > 0) row.append(",");
                        row.append("\"").append(tableModel.getValueAt(i, j)).append("\"");
                    }
                    writer.println(row.toString());
                }
                
                JOptionPane.showMessageDialog(this, "Schedule exported successfully!", 
                                            "Export Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to export: " + e.getMessage(), 
                                            "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void setupWindow() {
        setTitle("Loan Calculator - Detailed Schedule");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        pack();
        setLocationRelativeTo(null);
        
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
    }
}