package loancalculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class LoanCalculator extends JFrame {
    private int userId;
    private JTextField principalField;
    private JTextField interestRateField;
    private JTextField termField;
    private JComboBox<String> termUnitCombo;
    private JTextArea resultArea;
    private JButton calculateButton;
    private JButton clearButton;
    private JButton saveButton;
    private JButton detailsButton;
    private JButton compareButton;
    private JButton historyButton;
    private JButton aboutButton;
    private JButton logoutButton;
    
    private double lastPrincipal = 0;
    private double lastAnnualRate = 0;
    private int lastTermMonths = 0;
    
    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
    private DecimalFormat percentFormat = new DecimalFormat("0.00%");
    
    public LoanCalculator(int userId) {
        this.userId = userId;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindow();
    }
    
    private void initializeComponents() {
        principalField = new JTextField(15);
        interestRateField = new JTextField(15);
        termField = new JTextField(15);
        termUnitCombo = new JComboBox<>(new String[]{"Years", "Months"});
        
        resultArea = new JTextArea(15, 45);
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(248, 248, 255));
        resultArea.setBorder(BorderFactory.createLoweredBevelBorder());
        resultArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        
        calculateButton = new JButton("Calculate Loan");
        clearButton = new JButton("Clear");
        saveButton = new JButton("Save Calculation");
        detailsButton = new JButton("View Details");
        compareButton = new JButton("Compare Loans");
        historyButton = new JButton("View History");
        aboutButton = new JButton("About");
        logoutButton = new JButton("Logout");
        
        calculateButton.setBackground(new Color(70, 130, 180));
        calculateButton.setForeground(Color.BLACK);
        calculateButton.setFocusPainted(false);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        clearButton.setBackground(new Color(255, 140, 0));
        clearButton.setForeground(Color.BLACK);
        clearButton.setFocusPainted(false);
        
        saveButton.setBackground(new Color(60, 179, 113));
        saveButton.setForeground(Color.BLACK);
        saveButton.setFocusPainted(false);
        saveButton.setEnabled(false);
        
        detailsButton.setBackground(new Color(138, 43, 226));
        detailsButton.setForeground(Color.BLACK);
        detailsButton.setFocusPainted(false);
        detailsButton.setEnabled(false);
        
        compareButton.setBackground(new Color(255, 165, 0));
        compareButton.setForeground(Color.BLACK);
        compareButton.setFocusPainted(false);
        
        historyButton.setBackground(new Color(32, 178, 170));
        historyButton.setForeground(Color.BLACK);
        historyButton.setFocusPainted(false);
        
        aboutButton.setBackground(new Color(105, 105, 105));
        aboutButton.setForeground(Color.BLACK);
        aboutButton.setFocusPainted(false);
        
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 112));
        
        JLabel titleLabel = new JLabel("Professional Loan Calculator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel userLabel = new JLabel("Welcome, " + Database.getUsername(userId), SwingConstants.RIGHT);
        userLabel.setForeground(Color.LIGHT_GRAY);
        userLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        JPanel inputPanel = createInputPanel();
        
        JPanel resultPanel = createResultPanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, resultPanel);
        splitPane.setResizeWeight(0.4);
        splitPane.setDividerLocation(400);
        splitPane.setBorder(null);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = createBottomPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createRaisedBevelBorder(), "Loan Information",
            0, 0, new Font("Arial", Font.BOLD, 14), new Color(70, 130, 180)));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel principalLabel = new JLabel("Loan Amount ($):");
        principalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(principalLabel, gbc);
        
        gbc.gridx = 1;
        principalField.setPreferredSize(new Dimension(150, 25));
        formPanel.add(principalField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel rateLabel = new JLabel("Annual Interest Rate (%):");
        rateLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(rateLabel, gbc);
        
        gbc.gridx = 1;
        interestRateField.setPreferredSize(new Dimension(150, 25));
        formPanel.add(interestRateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel termLabel = new JLabel("Loan Term:");
        termLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(termLabel, gbc);
        
        gbc.gridx = 1;
        JPanel termPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        termPanel.setBackground(Color.WHITE);
        termField.setPreferredSize(new Dimension(100, 25));
        termPanel.add(termField);
        termPanel.add(Box.createHorizontalStrut(10));
        termPanel.add(termUnitCombo);
        formPanel.add(termPanel, gbc);
        
        inputPanel.add(formPanel);
        inputPanel.add(Box.createVerticalStrut(20));
        
        JPanel actionPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        actionPanel.add(calculateButton);
        actionPanel.add(clearButton);
        actionPanel.add(saveButton);
        actionPanel.add(detailsButton);
        actionPanel.add(compareButton);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 0)));
        
        inputPanel.add(actionPanel);
        
        return inputPanel;
    }
    
    private JPanel createResultPanel() {
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createRaisedBevelBorder(), "Calculation Results",
            0, 0, new Font("Arial", Font.BOLD, 14), new Color(70, 130, 180)));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        resultArea.setText("""
            Welcome to the Professional Loan Calculator!
            
            Instructions:
            1. Enter the loan amount
            2. Enter the annual interest rate as a percentage
            3. Enter the loan term and select Years or Months
            4. Click 'Calculate Loan' to see results
            
            Features:
            • Detailed payment breakdown
            • Total interest calculation
            • Monthly payment schedule
            • Save calculations to history
            • View detailed amortization schedules
            • Compare multiple loan options
            
            The calculator uses the standard loan formula:
            Monthly Payment = P × [r(1+r)^n] / [(1+r)^n - 1]
            
            Where P = Principal, r = Monthly rate, n = Number of payments
            """);
        
        return resultPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        bottomPanel.add(historyButton);
        bottomPanel.add(Box.createHorizontalStrut(10));
        bottomPanel.add(aboutButton);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(logoutButton);
        
        return bottomPanel;
    }
    
    private void setupEventHandlers() {
        calculateButton.addActionListener(e -> calculateLoan());
        clearButton.addActionListener(e -> clearFields());
        saveButton.addActionListener(e -> saveCalculation());
        detailsButton.addActionListener(e -> openDetails());
        compareButton.addActionListener(e -> openComparison());
        historyButton.addActionListener(e -> openHistory());
        aboutButton.addActionListener(e -> openAbout());
        logoutButton.addActionListener(e -> logout());
        
        principalField.addActionListener(e -> calculateLoan());
        interestRateField.addActionListener(e -> calculateLoan());
        termField.addActionListener(e -> calculateLoan());
    }
    
    private void calculateLoan() {
        try {
            String principalText = principalField.getText().trim();
            String rateText = interestRateField.getText().trim();
            String termText = termField.getText().trim();
            
            if (principalText.isEmpty() || rateText.isEmpty() || termText.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill in all fields!", 
                    "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double principal = Double.parseDouble(principalText);
            double annualRate = Double.parseDouble(rateText) / 100;
            int term = Integer.parseInt(termText);
            
            int termMonths = termUnitCombo.getSelectedItem().equals("Years") ? term * 12 : term;
            
            if (principal <= 0 || annualRate < 0 || termMonths <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter valid positive numbers!\nLoan amount and term must be greater than 0.", 
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (annualRate > 1) {
                int choice = JOptionPane.showConfirmDialog(this,
                    "Interest rate seems high (" + String.format("%.2f%%", annualRate * 100) + 
                    ").\nDid you mean " + String.format("%.2f%%", annualRate) + "?",
                    "Confirm Interest Rate", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    annualRate = annualRate / 100;
                    interestRateField.setText(String.format("%.2f", annualRate * 100));
                }
            }
            
            lastPrincipal = principal;
            lastAnnualRate = annualRate;
            lastTermMonths = termMonths;
            
            double monthlyRate = annualRate / 12;
            double monthlyPayment;
            
            if (monthlyRate == 0) {
                monthlyPayment = principal / termMonths;
            } else {
                monthlyPayment = principal * (monthlyRate * Math.pow(1 + monthlyRate, termMonths)) / 
                               (Math.pow(1 + monthlyRate, termMonths) - 1);
            }
            
            double totalPayment = monthlyPayment * termMonths;
            double totalInterest = totalPayment - principal;
            
            displayResults(principal, annualRate, termMonths, monthlyPayment, totalPayment, totalInterest);
            
            saveButton.setEnabled(true);
            detailsButton.setEnabled(true);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numbers only!\n\nExample:\nLoan Amount: 250000\nInterest Rate: 4.5\nTerm: 30", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "An error occurred during calculation:\n" + e.getMessage(), 
                "Calculation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayResults(double principal, double annualRate, int termMonths, 
                               double monthlyPayment, double totalPayment, double totalInterest) {
        StringBuilder result = new StringBuilder();
        
        result.append("=".repeat(60)).append("\n");
        result.append("               LOAN CALCULATION RESULTS\n");
        result.append("=".repeat(60)).append("\n\n");
        
        result.append("LOAN DETAILS:\n");
        result.append("-".repeat(30)).append("\n");
        result.append(String.format("Principal Amount:      %s\n", currencyFormat.format(principal)));
        result.append(String.format("Annual Interest Rate:  %.4f%%\n", annualRate * 100));
        result.append(String.format("Loan Term:            %d months (%.1f years)\n", 
                     termMonths, termMonths / 12.0));
        result.append("\n");
        
        result.append("PAYMENT INFORMATION:\n");
        result.append("-".repeat(30)).append("\n");
        result.append(String.format("Monthly Payment:      %s\n", currencyFormat.format(monthlyPayment)));
        result.append(String.format("Total Amount Paid:    %s\n", currencyFormat.format(totalPayment)));
        result.append(String.format("Total Interest Paid:  %s\n", currencyFormat.format(totalInterest)));
        result.append("\n");
        
        result.append("LOAN ANALYSIS:\n");
        result.append("-".repeat(30)).append("\n");
        result.append(String.format("Interest as %% of Total: %s (%.2f%%)\n", 
                     currencyFormat.format(totalInterest), 
                     (totalInterest / totalPayment) * 100));
        result.append(String.format("Monthly Payment as %% of Principal: %.2f%%\n", 
                     (monthlyPayment / principal) * 100));
        
        result.append("\n");
        result.append("AFFORDABILITY GUIDE:\n");
        result.append("-".repeat(30)).append("\n");
        result.append(String.format("Recommended Monthly Income: %s\n", 
                     currencyFormat.format(monthlyPayment / 0.28)));
        result.append("(Based on 28%% debt-to-income ratio)\n");
        result.append("\n");
        
        result.append("FIRST 3 PAYMENTS BREAKDOWN:\n");
        result.append("-".repeat(40)).append("\n");
        result.append("Payment | Interest  | Principal | Balance\n");
        result.append("-".repeat(40)).append("\n");
        
        double remainingBalance = principal;
        double monthlyRate = annualRate / 12;
        
        for (int i = 1; i <= Math.min(3, termMonths); i++) {
            double interestPayment = remainingBalance * monthlyRate;
            double principalPayment = monthlyPayment - interestPayment;
            remainingBalance -= principalPayment;
            
            result.append(String.format("   %2d   | %s | %s | %s\n",
                         i,
                         String.format("%8s", currencyFormat.format(interestPayment)),
                         String.format("%8s", currencyFormat.format(principalPayment)),
                         String.format("%9s", currencyFormat.format(remainingBalance))));
        }
        
        if (termMonths > 3) {
            result.append("   ...  |    ...    |    ...    |    ...\n");
            result.append(String.format("   %2d   | %s | %s | %s\n",
                         termMonths,
                         String.format("%8s", "varies"),
                         String.format("%8s", "varies"),
                         String.format("%9s", "$0.00")));
        }
        
        result.append("\n");
        result.append("TIP: Use 'View Details' for complete amortization schedule\n");
        result.append("TIP: Use 'Compare Loans' to evaluate different options\n");
        
        resultArea.setText(result.toString());
        resultArea.setCaretPosition(0);
    }
    
    private void clearFields() {
        principalField.setText("");
        interestRateField.setText("");
        termField.setText("");
        termUnitCombo.setSelectedIndex(0);
        resultArea.setText("Fields cleared. Enter loan details to calculate payments.");
        saveButton.setEnabled(false);
        detailsButton.setEnabled(false);
        
        lastPrincipal = 0;
        lastAnnualRate = 0;
        lastTermMonths = 0;
    }
    
    private void saveCalculation() {
        if (lastPrincipal <= 0) {
            JOptionPane.showMessageDialog(this, 
                "No calculation to save! Please calculate a loan first.", 
                "Nothing to Save", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            double monthlyRate = lastAnnualRate / 12;
            double monthlyPayment;
            
            if (monthlyRate == 0) {
                monthlyPayment = lastPrincipal / lastTermMonths;
            } else {
                monthlyPayment = lastPrincipal * (monthlyRate * Math.pow(1 + monthlyRate, lastTermMonths)) / 
                               (Math.pow(1 + monthlyRate, lastTermMonths) - 1);
            }
            
            double totalPayment = monthlyPayment * lastTermMonths;
            double totalInterest = totalPayment - lastPrincipal;
            
            boolean saved = Database.saveCalculation(userId, lastPrincipal, lastAnnualRate, 
                                                   lastTermMonths, monthlyPayment, 
                                                   totalPayment, totalInterest);
            
            if (saved) {
                JOptionPane.showMessageDialog(this, 
                    "Calculation saved successfully!\nYou can view it in your history.", 
                    "Save Successful", JOptionPane.INFORMATION_MESSAGE);
                saveButton.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to save calculation!\nPlease check your database connection.", 
                    "Save Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving calculation:\n" + e.getMessage(), 
                "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openDetails() {
        if (lastPrincipal <= 0) {
            JOptionPane.showMessageDialog(this, 
                "No calculation available! Please calculate a loan first.", 
                "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        new LoanDetails(lastPrincipal, lastAnnualRate, lastTermMonths).setVisible(true);
    }
    
    private void openComparison() {
        new LoanComparison().setVisible(true);
    }
    
    private void openHistory() {
        new LoanHistory(userId).setVisible(true);
    }
    
    private void openAbout() {
        String username = Database.getUsername(userId);
        if (username != null) {
            new About(username).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Unable to retrieve username. Please try again later.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, 
                                                 "Are you sure you want to logout?\nAny unsaved work will be lost.", 
                                                 "Confirm Logout", 
                                                 JOptionPane.YES_NO_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            new Login().setVisible(true);
            this.dispose();
        }
    }
    
    private void setupWindow() {
        setTitle("Professional Loan Calculator - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        try {
        } catch (Exception e) {  
        }
        
        pack();
        setLocationRelativeTo(null);
        
        Dimension size = getSize();
        setMinimumSize(new Dimension(Math.max(900, size.width), Math.max(600, size.height)));
        setPreferredSize(new Dimension(1000, 700));
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}