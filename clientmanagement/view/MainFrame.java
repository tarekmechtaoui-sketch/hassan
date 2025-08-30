package com.yourcompany.clientmanagement.view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private boolean isDarkMode = false;
    private JTabbedPane tabbedPane;

    public MainFrame() {
        // Set initial theme
        FlatLightLaf.setup();

        initializeUI();
        setupTabs();
        setupMenuBar();
    }

    private void initializeUI() {
        setTitle("Gestion des Clients et Versements");
        GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .setFullScreenWindow(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void setupTabs() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Client management tab
        ClientForm clientForm = new ClientForm();
        // Remove the frame functionality and use as panel
        clientForm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        clientForm.setVisible(false);

        JPanel clientPanel = (JPanel) clientForm.getContentPane();
        tabbedPane.addTab("📋 Gestion des Clients", clientPanel);

        // Versment management tab
        VersmentPanel versmentPanel = new VersmentPanel();
        tabbedPane.addTab("💰 Gestion des Versements", versmentPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // View menu
        JMenu viewMenu = new JMenu("Affichage");
        JMenuItem toggleThemeItem = new JMenuItem("🌓 Changer le thème");
        toggleThemeItem.addActionListener(e -> toggleTheme());
        viewMenu.add(toggleThemeItem);

        // Help menu
        JMenu helpMenu = new JMenu("Aide");
        JMenuItem aboutItem = new JMenuItem("À propos");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void toggleTheme() {
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } else {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);
            isDarkMode = !isDarkMode;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Système de Gestion des Clients et Versements\n" +
                        "Version 1.0\n" +
                        "Développé avec Java Swing",
                "À propos",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MainFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}