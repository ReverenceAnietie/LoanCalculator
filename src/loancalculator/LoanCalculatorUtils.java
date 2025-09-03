package loancalculator;


import java.text.DecimalFormat;

public class LoanCalculatorUtils {
    private static final DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
    private static final DecimalFormat percentFormat = new DecimalFormat("0.00%");
    
    public static class LoanResult {
        public double monthlyPayment;
        public double totalPayment;
        public double totalInterest;
        public double effectiveRate;
        
        public LoanResult(double monthlyPayment, double totalPayment, 
                         double totalInterest, double effectiveRate) {
            this.monthlyPayment = monthlyPayment;
            this.totalPayment = totalPayment;
            this.totalInterest = totalInterest;
            this.effectiveRate = effectiveRate;
        }
    }
    
    public static LoanResult calculateLoan(double principal, double annualRate, int termMonths) {
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
        double effectiveRate = totalInterest / principal;
        
        return new LoanResult(monthlyPayment, totalPayment, totalInterest, effectiveRate);
    }
    
    public static String formatCurrency(double amount) {
        return currencyFormat.format(amount);
    }
    
    public static String formatPercent(double rate) {
        return percentFormat.format(rate);
    }
    
    public static boolean isValidNumber(String text) {
        try {
            double value = Double.parseDouble(text);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isValidInteger(String text) {
        try {
            int value = Integer.parseInt(text);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static double calculateAffordability(double monthlyIncome, double monthlyExpenses) {
        double availableIncome = monthlyIncome - monthlyExpenses;
        return Math.min(availableIncome * 0.8, monthlyIncome * 0.28);
    }
    
    public static String getLoanAdvice(double debtToIncomeRatio) {
        if (debtToIncomeRatio <= 0.28) {
            return "Excellent - Your debt-to-income ratio is very healthy.";
        } else if (debtToIncomeRatio <= 0.36) {
            return "Good - Your debt-to-income ratio is acceptable.";
        } else if (debtToIncomeRatio <= 0.43) {
            return "Caution - Consider reducing debt before taking new loans.";
        } else {
            return "High Risk - Your debt-to-income ratio is too high.";
        }
    }
    
    public static String formatAmortizationSummary(double principal, double annualRate, 
                                                  int termMonths, double monthlyPayment, 
                                                  double totalPayment, double totalInterest) {
        StringBuilder summary = new StringBuilder();
        summary.append("LOAN CALCULATION SUMMARY\n");
        summary.append("=" .repeat(50)).append("\n\n");
        
        summary.append("Loan Details:\n");
        summary.append("  Principal Amount:     ").append(formatCurrency(principal)).append("\n");
        summary.append("  Annual Interest Rate: ").append(String.format("%.2f%%", annualRate * 100)).append("\n");
        summary.append("  Loan Term:           ").append(termMonths).append(" months\n\n");
        
        summary.append("Payment Information:\n");
        summary.append("  Monthly Payment:     ").append(formatCurrency(monthlyPayment)).append("\n");
        summary.append("  Total Payment:       ").append(formatCurrency(totalPayment)).append("\n");
        summary.append("  Total Interest:      ").append(formatCurrency(totalInterest)).append("\n\n");
        
        summary.append("Analysis:\n");
        summary.append("  Interest as % of Total: ").append(formatPercent(totalInterest / totalPayment)).append("\n");
        summary.append("  Effective Interest Rate: ").append(formatPercent(totalInterest / principal)).append("\n");
        
        return summary.toString();
    }
}