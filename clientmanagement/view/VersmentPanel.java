package com.yourcompany.clientmanagement.view;

import com.yourcompany.clientmanagement.controller.VersmentController;
import com.yourcompany.clientmanagement.controller.ClientController;
import com.yourcompany.clientmanagement.model.Versment;
import com.yourcompany.clientmanagement.model.Client;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VersmentPanel extends JPanel {
    private JTable versmentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private VersmentController versmentController;
    private ClientController clientController;
    private TableRowSorter<DefaultTableModel> sorter;
    private JLabel totalLabel;
    private JPopupMenu contextMenu;

    public VersmentPanel() {
        versmentController = new VersmentController();
        clientController = new ClientController();
        initializeUI();
        setupTable();
        setupSearch();
        setupButtons();
        setupContextMenu();
        loadVersmentData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void setupTable() {
        // Table columns
        String[] columnNames = {
                "ID", "Client ID", "Nom Client", "Montant", "Type",
                "Date Paiement", "Année Concernée", "Date Création"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        versmentTable = new JTable(tableModel);
        customizeTableAppearance();
        setupColumnWidths();

        JScrollPane scrollPane = new JScrollPane(versmentTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void customizeTableAppearance() {
        Font tableFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font headerFont = new Font("Segoe UI", Font.BOLD, 14);

        versmentTable.setFillsViewportHeight(true);
        versmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        versmentTable.setFont(tableFont);
        versmentTable.setRowHeight(30);
        versmentTable.getTableHeader().setFont(headerFont);
        versmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        versmentTable.setShowGrid(true);
        versmentTable.setGridColor(new Color(220, 220, 220));
        versmentTable.setIntercellSpacing(new Dimension(0, 1));

        versmentTable.setSelectionBackground(new Color(52, 152, 219));
        versmentTable.setSelectionForeground(Color.WHITE);
    }

    private void setupColumnWidths() {
        TableColumnModel columnModel = versmentTable.getColumnModel();

        columnModel.getColumn(0).setPreferredWidth(50); // ID
        columnModel.getColumn(1).setPreferredWidth(80); // Client ID
        columnModel.getColumn(2).setPreferredWidth(200); // Nom Client
        columnModel.getColumn(3).setPreferredWidth(100); // Montant
        columnModel.getColumn(4).setPreferredWidth(120); // Type
        columnModel.getColumn(5).setPreferredWidth(120); // Date Paiement
        columnModel.getColumn(6).setPreferredWidth(120); // Année Concernée
        columnModel.getColumn(7).setPreferredWidth(150); // Date Création
    }

    private void setupSearch() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JLabel searchLabel = new JLabel("🔍 Rechercher:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        totalLabel = new JLabel("Total: 0.00 DA");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(new Color(46, 125, 50));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(totalLabel);

        add(searchPanel, BorderLayout.NORTH);

        // Initialize sorter
        sorter = new TableRowSorter<>(tableModel);
        versmentTable.setRowSorter(sorter);

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterTable(searchField.getText());
            }
        });
    }

    private void setupButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton addButton = createButton("Ajouter Versement", e -> showAddDialog());
        JButton editButton = createButton("Modifier", e -> showEditDialog());
        JButton deleteButton = createButton("Supprimer", e -> deleteVersment());
        JButton refreshButton = createButton("Actualiser", e -> refreshVersmentTable());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupContextMenu() {
        contextMenu = new JPopupMenu();
        
        JMenuItem printReceiptItem = new JMenuItem("Imprimer bon de versement");
        printReceiptItem.addActionListener(e -> printVersmentReceipt());
        
        contextMenu.add(printReceiptItem);
        
        // Add mouse listener to table for right-click
        versmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = versmentTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < versmentTable.getRowCount()) {
                        versmentTable.setRowSelectionInterval(row, row);
                    } else {
                        versmentTable.clearSelection();
                    }
                    contextMenu.show(versmentTable, e.getX(), e.getY());
                }
            }
        });
    }

    private void printVersmentReceipt() {
        int selectedRow = versmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un versement", "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = versmentTable.convertRowIndexToModel(selectedRow);
        int versmentId = (Integer) tableModel.getValueAt(modelRow, 0);
        Versment versment = versmentController.getVersmentById(versmentId);

        if (versment != null) {
            // Here you would implement the actual printing logic
            // For now, we'll just show a confirmation dialog
            Client client = clientController.getClientById(versment.getClientId());
            String clientName = client != null ? client.getNom() + " " + client.getPrenom() : "Client inconnu";
            
            String receiptText = "Bon de Versement\n\n" +
                    "Client: " + clientName + "\n" +
                    "Montant: " + versment.getMontant() + " DA\n" +
                    "Type: " + versment.getType() + "\n" +
                    "Date Paiement: " + versment.getDatePaiement().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                    "Année Concernée: " + versment.getAnneeConcernee() + "\n\n" +
                    "Merci pour votre confiance!";
            
            JOptionPane.showMessageDialog(this, receiptText, "Bon de Versement - Prévisualisation", JOptionPane.INFORMATION_MESSAGE);
            
            // In a real application, you would:
            // 1. Create a proper receipt template
            // 2. Use a printing API (like Java's PrinterJob)
            // 3. Handle the actual printing process
        }
    }

    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        button.addActionListener(listener);
        return button;
    }

    private void loadVersmentData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                List<Versment> versments = versmentController.fetchAllVersments();
                tableModel.setRowCount(0);

                // Use a mutable wrapper for BigDecimal
                final BigDecimal[] total = { BigDecimal.ZERO };

                for (Versment v : versments) {
                    tableModel.addRow(convertVersmentToRow(v));
                    if (v.getMontant() != null) {
                        total[0] = total[0].add(v.getMontant());
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    totalLabel.setText("Total des versements: " + total[0].toString() + " DA");
                });

                return null;
            }

            @Override
            protected void done() {
                versmentTable.repaint();
            }
        };
        worker.execute();
    }

    private Object[] convertVersmentToRow(Versment v) {
        // Get client name
        Client client = clientController.getClientById(v.getClientId());
        String clientName = client != null
                ? client.getNom() + " " + (client.getPrenom() != null ? client.getPrenom() : "")
                : "Client inconnu";

        return new Object[] {
                v.getId(),
                v.getClientId(),
                clientName,
                v.getMontant(),
                v.getType(),
                v.getDatePaiement() != null ? v.getDatePaiement().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "",
                v.getAnneeConcernee(),
                v.getCreatedAt() != null ? v.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""
        };
    }

    private void filterTable(String query) {
        if (query.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
            } catch (Exception e) {
                // Invalid regex - ignore
            }
        }
    }

    private void refreshVersmentTable() {
        loadVersmentData();
    }

    private void showAddDialog() {
        VersmentDialog dialog = new VersmentDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Ajouter Versement",
                null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Versment newVersment = dialog.getVersment();
            int result = versmentController.addVersment(newVersment);
            if (result > 0) {
                refreshVersmentTable();
                JOptionPane.showMessageDialog(this, 
                    "Versement ajouté avec succès!\n" +
                    "Le montant annuel du client a été mis à jour automatiquement.");
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = versmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un versement", "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = versmentTable.convertRowIndexToModel(selectedRow);
        int versmentId = (Integer) tableModel.getValueAt(modelRow, 0);
        Versment versmentToEdit = versmentController.getVersmentById(versmentId);

        if (versmentToEdit != null) {
            VersmentDialog dialog = new VersmentDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Modifier Versement", versmentToEdit);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Versment updatedVersment = dialog.getVersment();
                if (versmentController.updateVersment(updatedVersment)) {
                    refreshVersmentTable();
                    JOptionPane.showMessageDialog(this, 
                        "Versement modifié avec succès!\n" +
                        "Le montant annuel du client a été ajusté automatiquement.");
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteVersment() {
        int selectedRow = versmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un versement", "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer ce versement?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = versmentTable.convertRowIndexToModel(selectedRow);
            int versmentId = (Integer) tableModel.getValueAt(modelRow, 0);

            if (versmentController.deleteVersment(versmentId)) {
                refreshVersmentTable();
                JOptionPane.showMessageDialog(this, 
                    "Versement supprimé avec succès!\n" +
                    "Le montant annuel du client a été restauré automatiquement.");
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}