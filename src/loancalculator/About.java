package loancalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class About extends JDialog {
    private JLabel profileImageLabel;
    private JButton uploadImageButton;
    private JButton removeImageButton;
    private final int IMAGE_SIZE = 120;
    private final String username;
    private final Database database;
    
    public About(String username) {
        this.username = username;
        this.database = new Database();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindow();
    }
    
    private void initializeComponents() {
        profileImageLabel = new JLabel();
        profileImageLabel.setPreferredSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        profileImageLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        profileImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profileImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        uploadImageButton = new JButton("Upload Photo");
        removeImageButton = new JButton("Remove Photo");
        
        setDefaultProfileImage();
 
        uploadImageButton.setBackground(new Color(30, 144, 255)); 
        uploadImageButton.setForeground(Color.BLACK);
        uploadImageButton.setFocusPainted(false);
        uploadImageButton.setFont(new Font("Arial", Font.BOLD, 11));
        
        removeImageButton.setBackground(new Color(30, 144, 255)); 
        removeImageButton.setForeground(Color.BLACK);
        removeImageButton.setFocusPainted(false);
        removeImageButton.setFont(new Font("Arial", Font.BOLD, 11));
    }
    
    private void setDefaultProfileImage() {
        try {
            BufferedImage image = database.getProfilePicture(username);
            if (image != null) {
                image = resizeImage(image, IMAGE_SIZE, IMAGE_SIZE);
                image = createCircularImage(image);
                profileImageLabel.setIcon(new ImageIcon(image));
                profileImageLabel.setText(null);
                removeImageButton.setEnabled(true);
            } else {
                profileImageLabel.setText("No Image");
                profileImageLabel.setIcon(null);
                removeImageButton.setEnabled(false);
            }
        } catch (Exception e) {
            System.err.println("Failed to set default profile image: " + e.getMessage());
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 25, 112));
        JLabel titleLabel = new JLabel("About Loan Calculator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        headerPanel.add(titleLabel);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JPanel profilePanel = createProfilePanel();
        
        JPanel infoPanel = createInfoPanel();
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(profilePanel, BorderLayout.WEST);
        topPanel.add(infoPanel, BorderLayout.CENTER);
        
        JPanel descriptionPanel = createDescriptionPanel();
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(descriptionPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(new EmptyBorder(0, 0, 0, 30));
        
        profileImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePanel.add(profileImageLabel);
        profilePanel.add(Box.createVerticalStrut(10));
        
        JPanel imageButtonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        imageButtonPanel.setBackground(Color.WHITE);
        imageButtonPanel.setMaximumSize(new Dimension(120, 60));
        imageButtonPanel.add(uploadImageButton);
        imageButtonPanel.add(removeImageButton);
        
        imageButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePanel.add(imageButtonPanel);
        profilePanel.add(Box.createVerticalStrut(10));
        
        JLabel profileLabel = new JLabel("User Profile", SwingConstants.CENTER);
        profileLabel.setFont(new Font("Arial", Font.BOLD, 12));
        profileLabel.setForeground(new Color(30, 144, 255));
        profileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePanel.add(profileLabel);
        
        return profilePanel;
    }
    
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel appNameLabel = new JLabel("Your_Name");
        appNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        appNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel RegLabel = new JLabel("Your_Reg_Number");
        RegLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        RegLabel.setForeground(Color.GRAY);
        RegLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        /*JLabel dateLabel = new JLabel("Released: " + java.time.LocalDate.now().toString());
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);*/
        
        infoPanel.add(appNameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(RegLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        //infoPanel.add(dateLabel);
        
        return infoPanel;
    }
    
    private JPanel createDescriptionPanel() {
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBackground(Color.WHITE);
        
        JTextArea descriptionArea = new JTextArea(
            "Professional Loan Calculator is designed to help you make informed financial decisions.\n\n" +
            "Key Features:\n" +
            "• Calculate monthly payments and total interest\n" +
            "• Compare multiple loan options\n" +
            "• View detailed amortization schedules\n" +
            "• Save and review calculation history\n" +
            "• Secure user authentication\n\n" +
            "This application provides accurate calculations for personal and professional use."
        );
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Application Description"));
        
        descriptionPanel.add(scrollPane, BorderLayout.CENTER);
        return descriptionPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        JButton systemInfoButton = new JButton("System Info");
        JButton creditsButton = new JButton("Credits");
        JButton closeButton = new JButton("Close");
        
        systemInfoButton.setBackground(new Color(30, 144, 255));
        systemInfoButton.setForeground(Color.BLACK);
        systemInfoButton.setFocusPainted(false);
        
        creditsButton.setBackground(new Color(30, 144, 255));
        creditsButton.setForeground(Color.BLACK);
        creditsButton.setFocusPainted(false);
        
        closeButton.setBackground(new Color(30, 144, 255));
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);
        
        buttonPanel.add(systemInfoButton);
        buttonPanel.add(creditsButton);
        buttonPanel.add(closeButton);
        
        systemInfoButton.addActionListener(e -> showSystemInfo());
        creditsButton.addActionListener(e -> showCredits());
        closeButton.addActionListener(e -> dispose());
        
        return buttonPanel;
    }
    
    private void setupEventHandlers() {
        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadProfileImage();
            }
        });
        
        removeImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeProfileImage();
            }
        });
    }
    
    private void uploadProfileImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                BufferedImage image = ImageIO.read(selectedFile);
                
                image = resizeImage(image, IMAGE_SIZE, IMAGE_SIZE);
                image = createCircularImage(image);
                
                profileImageLabel.setIcon(new ImageIcon(image));
                profileImageLabel.setText(null);
                
                database.saveProfilePicture(username, image);
                removeImageButton.setEnabled(true);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Failed to load image: " + e.getMessage(), 
                    "Image Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        
        return resizedImage;
    }
    
    private BufferedImage createCircularImage(BufferedImage image) {
        int size = Math.min(image.getWidth(), image.getHeight());
        BufferedImage circularImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = circularImage.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));
        
        g2d.drawImage(image, 0, 0, size, size, null);
        
        g2d.setClip(null);
        g2d.setColor(new Color(30, 144, 255));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(1, 1, size-3, size-3);
        
        g2d.dispose();
        return circularImage;
    }
    
    private void removeProfileImage() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove your profile photo?",
            "Remove Photo", JOptionPane.YES_NO_OPTION);
            
        if (choice == JOptionPane.YES_OPTION) {
            database.removeProfilePicture(username);
            setDefaultProfileImage();
            JOptionPane.showMessageDialog(this,
                "Profile photo removed successfully!",
                "Photo Removed", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showSystemInfo() {
        StringBuilder info = new StringBuilder();
        info.append("SYSTEM INFORMATION\n");
        info.append("==================\n\n");
        info.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        info.append("Java Vendor: ").append(System.getProperty("java.vendor")).append("\n");
        info.append("Operating System: ").append(System.getProperty("os.name")).append("\n");
        info.append("OS Version: ").append(System.getProperty("os.version")).append("\n");
        info.append("OS Architecture: ").append(System.getProperty("os.arch")).append("\n");
        info.append("User Name: ").append(System.getProperty("user.name")).append("\n");
        info.append("User Home: ").append(System.getProperty("user.home")).append("\n");
        info.append("Available Processors: ").append(Runtime.getRuntime().availableProcessors()).append("\n");
        info.append("Max Memory: ").append(Runtime.getRuntime().maxMemory() / 1024 / 1024).append(" MB\n");
        info.append("Free Memory: ").append(Runtime.getRuntime().freeMemory() / 1024 / 1024).append(" MB\n");
        
        JTextArea textArea = new JTextArea(info.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "System Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showCredits() {
        String credits = """
            LOAN CALCULATOR - DEVELOPMENT CREDITS
            =====================================
            
            Application Design & Development
            User Interface & Experience Design
            Database Architecture & Security
            Mathematical Formula Implementation
            Testing & Quality Assurance
            
            TECHNOLOGIES USED:
            • Java Swing Framework
            • PostgreSQL Database
            • JDBC Database Connectivity
            • SHA-256 Encryption
            • Image Processing APIs
            
            SPECIAL THANKS:
            • Java Community for excellent documentation
            • PostgreSQL team for robust database system
            • Open source contributors
            
            © 2024 Professional Loan Calculator
            All rights reserved.
            
            This application is designed to provide accurate
            financial calculations for educational and 
            professional use.
            """;
        
        JTextArea textArea = new JTextArea(credits);
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 350));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Credits", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void setupWindow() {
        setTitle("About - Professional Loan Calculator");
        setModal(true);
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        
        setSize(650, 500);
        setMinimumSize(new Dimension(600, 450));
    }
}