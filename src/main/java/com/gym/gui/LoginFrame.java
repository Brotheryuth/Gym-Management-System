package com.gym.gui;

import com.gym.model.Staff;
import com.gym.service.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * A beautifully styled, modern login frame to authenticate staff members.
 */
public class LoginFrame extends JFrame {

    private final MemberService memberService;
    private final MembershipService membershipService;
    private final MembershipPlanService membershipPlanService;
    private final PaymentService paymentService;
    private final StaffService staffService;

    // UI Components
    private final JTextField txtIdentifier;
    private final JPasswordField txtPassword;
    private final JLabel lblError;

    // Custom Theme Colors
    private final Color bgDark = new Color(24, 28, 36);       // Modern dark slate
    private final Color cardDark = new Color(33, 39, 50);     // Lighter slate for input containers
    private final Color textMuted = new Color(160, 165, 175);  // Light muted gray
    private final Color blueTheme = new Color(59, 130, 246);  // Elegant accent blue
    private final Color blueHover = new Color(37, 99, 235);   // Darker blue for button hover
    private final Color errorRed = new Color(239, 68, 68);    // Soft flat red for errors

    public LoginFrame(
        MemberService memberService,
        MembershipService membershipService,
        MembershipPlanService membershipPlanService,
        PaymentService paymentService,
        StaffService staffService
    ) {
        this.memberService = memberService;
        this.membershipService = membershipService;
        this.membershipPlanService = membershipPlanService;
        this.paymentService = paymentService;
        this.staffService = staffService;

        // Configure Frame Window
        setTitle("Gym Manager - Login");
        setSize(480, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Container Panel
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(bgDark);
        contentPane.setBorder(new EmptyBorder(30, 40, 30, 40));
        setContentPane(contentPane);

        // Header Panel (Logo/Title)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("GYM MANAGER");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Staff Authentication Portal");
        lblSubtitle.setForeground(textMuted);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblLogo);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(lblSubtitle);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Form Panel (Inputs and Labels)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // 1. Username / Phone Label
        JLabel lblIdentifier = new JLabel("Username or Phone Number");
        lblIdentifier.setForeground(textMuted);
        lblIdentifier.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblIdentifier, gbc);

        // 2. Username / Phone Input Field
        txtIdentifier = new JTextField();
        styleInputField(txtIdentifier);
        gbc.gridy = 1;
        formPanel.add(txtIdentifier, gbc);

        // 3. Password Label
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setForeground(textMuted);
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 2;
        formPanel.add(lblPassword, gbc);

        // 4. Password Input Field
        txtPassword = new JPasswordField();
        styleInputField(txtPassword);
        gbc.gridy = 3;
        formPanel.add(txtPassword, gbc);

        // 5. Error Label Panel (to dynamically display messages)
        lblError = new JLabel(" ");
        lblError.setForeground(errorRed);
        lblError.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 0, 0);
        formPanel.add(lblError, gbc);

        contentPane.add(formPanel, BorderLayout.CENTER);

        // Bottom Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 10, 0));

        JButton btnExit = UIHelper.createOutlineButton("Exit", textMuted);
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExit.addActionListener(e -> System.exit(0));

        JButton btnLogin = UIHelper.createSolidButton("Sign In", blueTheme, blueHover);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.addActionListener(e -> performLogin());

        // Enable pressing Enter in inputs to trigger login
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        txtIdentifier.addKeyListener(enterKeyAdapter);
        txtPassword.addKeyListener(enterKeyAdapter);

        buttonPanel.add(btnExit);
        buttonPanel.add(btnLogin);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void styleInputField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBackground(cardDark);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        // Clean padding and flat dark borders
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 55, 65), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    private void performLogin() {
        String identifier = txtIdentifier.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (identifier.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter username and password.");
            return;
        }

        lblError.setText(" "); // Reset error display

        // Authenticate staff asynchronously or synchronously
        try {
            Staff authenticatedStaff = staffService.authenticate(identifier, password);
            if (authenticatedStaff != null) {
                // Login successful, open MainFrame
                new MainFrame(
                    memberService,
                    membershipService,
                    membershipPlanService,
                    paymentService,
                    staffService,
                    authenticatedStaff
                ).setVisible(true);

                // Close and dispose login window
                this.dispose();
            } else {
                lblError.setText("Authentication failed. Invalid staff record.");
            }
        } catch (IllegalStateException | IllegalArgumentException ex) {
            lblError.setText(ex.getMessage());
        } catch (Exception ex) {
            lblError.setText("System Error: Unable to login.");
            ex.printStackTrace();
        }
    }
}
