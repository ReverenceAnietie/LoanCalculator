package loancalculator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class LoanHistory extends JFrame {
    private int userId;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public LoanHistory(int userId) {
        this.userId = userId;
        initializeComponents();
        setupLayout();
        loadHistoryData();
        setupWindow();
    }
    
    private void initializeComponents() {
        String[] columnNames = {"Date", "Principal", "Rate (%)", "Term (Months)", 
                               "Monthly Payment", "Total Payment", "Total Interest"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setRowHeight(25);
        historyTable.getTableHeader().setBackground(new Color(70, 130, 180));
        historyTable.getTableHeader().setForeground(Color.WHITE);
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(110);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 25, 112));
        JLabel titleLabel = new JLabel("Loan Calculation History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        headerPanel.add(titleLabel);
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Previous Calculations"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton refreshButton = new JButton("Refresh");
        JButton deleteButton = new JButton("Delete Selected");
        JButton closeButton = new JButton("Close");
        
        refreshButton.setBackground(new Color(60, 179, 113));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.BLACK);
        deleteButton.setFocusPainted(false);
        
        closeButton.setBackground(new Color(105, 105, 105));
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        
        refreshButton.addActionListener(e -> loadHistoryData());
        deleteButton.addActionListener(e -> deleteSelectedCalculation());
        closeButton.addActionListener(e -> dispose());
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadHistoryData() {
        tableModel.setRowCount(0);
        
        String sql = """
            SELECT principal, annual_rate, term_months, monthly_payment, 
                   total_payment, total_interest, calculation_date, id
            FROM loan_calculations 
            WHERE user_id = ? 
            ORDER BY calculation_date DESC
        """;
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    dateFormat.format(rs.getTimestamp("calculation_date")),
                    currencyFormat.format(rs.getDouble("principal")),
                    String.format("%.2f", rs.getDouble("annual_rate") * 100),
                    rs.getInt("term_months"),
                    currencyFormat.format(rs.getDouble("monthly_payment")),
                    currencyFormat.format(rs.getDouble("total_payment")),
                    currencyFormat.format(rs.getDouble("total_interest"))
                };
                tableModel.addRow(row);
            }
            
            if (tableModel.getRowCount() == 0) {
                JLabel noDataLabel = new JLabel("No calculation history found.", SwingConstants.CENTER);
                noDataLabel.setForeground(Color.GRAY);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load history: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedCalculation() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a calculation to delete!", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this, 
                                                 "Are you sure you want to delete this calculation?", 
                                                 "Confirm Delete", 
                                                 JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Calculation deleted successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void setupWindow() {
        setTitle("Loan Calculator - History");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        pack();
        setLocationRelativeTo(null);
        
        setSize(800, 500);
        setMinimumSize(new Dimension(700, 400));
    }
}