package loancalculator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class LoanComparison extends JFrame {
    private JTextField[] principalFields;
    private JTextField[] rateFields;
    private JTextField[] termFields;
    private JTable comparisonTable;
    private DefaultTableModel tableModel;
    private JButton calculateButton;
    private JButton clearButton;
    private JButton closeButton;
    
    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
    private final int MAX_LOANS = 4;
    
    public LoanComparison() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindow();
    }
    
    private void initializeComponents() {
        principalFields = new JTextField[MAX_LOANS];
        rateFields = new JTextField[MAX_LOANS];
        termFields = new JTextField[MAX_LOANS];
        
        for (int i = 0; i < MAX_LOANS; i++) {
            principalFields[i] = new JTextField(12);
            rateFields[i] = new JTextField(8);
            termFields[i] = new JTextField(8);
        }
        
        String[] columnNames = {"Loan Option", "Principal", "Rate (%)", "Term (Months)", 
                               "Monthly Payment", "Total Payment", "Total Interest", "Savings vs Option 1"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        comparisonTable = new JTable(tableModel);
        comparisonTable.setRowHeight(30);
        comparisonTable.getTableHeader().setBackground(new Color(70, 130, 180));
        comparisonTable.getTableHeader().setForeground(Color.WHITE);
        comparisonTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        
        calculateButton = new JButton("Compare Loans");
        clearButton = new JButton("Clear All");
        closeButton = new JButton("Close");
        
        calculateButton.setBackground(new Color(70, 130, 180));
        calculateButton.setForeground(Color.BLACK);
        calculateButton.setFocusPainted(false);
        
        clearButton.setBackground(new Color(255, 140, 0));
        clearButton.setForeground(Color.BLACK);
        clearButton.setFocusPainted(false);
        
        closeButton.setBackground(new Color(105, 105, 105));
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 25, 112));
        JLabel titleLabel = new JLabel("Loan Comparison Tool", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        headerPanel.add(titleLabel);
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Loan Options to Compare"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Loan Option"), gbc);
        gbc.gridx = 1;
        inputPanel.add(new JLabel("Principal ($)"), gbc);
        gbc.gridx = 2;
        inputPanel.add(new JLabel("Rate (%)"), gbc);
        gbc.gridx = 3;
        inputPanel.add(new JLabel("Term (Months)"), gbc);
        
        for (int i = 0; i < MAX_LOANS; i++) {
            gbc.gridx = 0; gbc.gridy = i + 1;
            inputPanel.add(new JLabel("Option " + (i + 1) + ":"), gbc);
            
            gbc.gridx = 1;
            inputPanel.add(principalFields[i], gbc);
            
            gbc.gridx = 2;
            inputPanel.add(rateFields[i], gbc);
            
            gbc.gridx = 3;
            inputPanel.add(termFields[i], gbc);
        }

        JPanel inputButtonPanel = new JPanel(new FlowLayout());
        inputButtonPanel.setBackground(Color.WHITE);
        inputButtonPanel.add(calculateButton);
        inputButtonPanel.add(clearButton);
        
        gbc.gridx = 0; gbc.gridy = MAX_LOANS + 1; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(inputButtonPanel, gbc);
        
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Comparison Results"));
        
        JScrollPane scrollPane = new JScrollPane(comparisonTable);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(closeButton);
        
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(inputPanel);
        centerPanel.add(resultsPanel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        calculateButton.addActionListener(e -> compareLoans());
        clearButton.addActionListener(e -> clearAllFields());
        closeButton.addActionListener(e -> dispose());
    }
    
    private void compareLoans() {
        tableModel.setRowCount(0);
        
        double[] totalPayments = new double[MAX_LOANS];
        boolean hasValidLoan = false;
        
        for (int i = 0; i < MAX_LOANS; i++) {
            String principalText = principalFields[i].getText().trim();
            String rateText = rateFields[i].getText().trim();
            String termText = termFields[i].getText().trim();
            
            if (principalText.isEmpty() || rateText.isEmpty() || termText.isEmpty()) {
                continue;
            }
            
            try {
                double principal = Double.parseDouble(principalText);
                double annualRate = Double.parseDouble(rateText) / 100;
                int termMonths = Integer.parseInt(termText);
                
                if (principal <= 0 || annualRate < 0 || termMonths <= 0) {
                    continue;
                }
                
                LoanCalculatorUtils.LoanResult result = 
                    LoanCalculatorUtils.calculateLoan(principal, annualRate, termMonths);
                
                totalPayments[i] = result.totalPayment;
                hasValidLoan = true;
                
                Object[] row = {
                    "Option " + (i + 1),
                    currencyFormat.format(principal),
                    String.format("%.2f", annualRate * 100),
                    termMonths,
                    currencyFormat.format(result.monthlyPayment),
                    currencyFormat.format(result.totalPayment),
                    currencyFormat.format(result.totalInterest),
                    ""
                };
                
                tableModel.addRow(row);
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid input in Option " + (i + 1) + ". Please check your values.", 
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        if (!hasValidLoan) {
            JOptionPane.showMessageDialog(this, "Please enter at least one complete loan option!", 
                                        "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double baseTotal = totalPayments[0];
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            double currentTotal = totalPayments[i];
            double savings = baseTotal - currentTotal;
            String savingsText = savings == 0 ? "Base Option" : 
                               (savings > 0 ? "+" + currencyFormat.format(Math.abs(savings)) + " more" : 
                                             currencyFormat.format(Math.abs(savings)) + " saved");
            tableModel.setValueAt(savingsText, i, 7);
        }
        
        highlightBestOption();
    }
    
    private void highlightBestOption() {
        int bestRow = -1;
        double lowestTotal = Double.MAX_VALUE;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String totalPaymentStr = (String) tableModel.getValueAt(i, 5);
            try {
                double total = Double.parseDouble(totalPaymentStr.replace("$", "").replace(",", ""));
                if (total < lowestTotal) {
                    lowestTotal = total;
                    bestRow = i;
                }
            } catch (NumberFormatException e) {
            }
        }
        
        if (bestRow != -1) {
            comparisonTable.setRowSelectionAllowed(true);
            comparisonTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            comparisonTable.setRowSelectionInterval(bestRow, bestRow);
            
            JOptionPane.showMessageDialog(this, 
                "Option " + (bestRow + 1) + " offers the lowest total payment!\n" +
                "Best option is highlighted in the table.", 
                "Best Option Found", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void clearAllFields() {
        for (int i = 0; i < MAX_LOANS; i++) {
            principalFields[i].setText("");
            rateFields[i].setText("");
            termFields[i].setText("");
        }
        tableModel.setRowCount(0);
    }
    
    private void setupWindow() {
        setTitle("Loan Calculator - Compare Options");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        pack();
        setLocationRelativeTo(null);
        
        setSize(1000, 600);
        setMinimumSize(new Dimension(900, 500));
    }
}