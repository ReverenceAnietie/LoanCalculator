package loancalculator;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Database.initializeDatabase();
        
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}