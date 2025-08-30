package com.yourcompany.clientmanagement.view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import org.jdesktop.swingx.JXDatePicker;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.yourcompany.clientmanagement.controller.ClientController;
import com.yourcompany.clientmanagement.model.Client;
import com.yourcompany.clientmanagement.controller.VersmentController;
import java.math.BigDecimal;

public class ClientForm extends JFrame {
    // Table and data components
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private ClientController controller;
    private TableRowSorter<DefaultTableModel> sorter;

    // Filter components
    private JTextField searchField;
    private JComboBox<String> typeFilterCombo;
    private JTextField minAmountField, maxAmountField;
    private JXDatePicker fromDatePicker, toDatePicker;

    // UI state
    private boolean isDarkMode = false;
    private VersmentController versmentController;

    // Column index constants
    private static final int COL_ID = 0;
    private static final int COL_NOM = 1;
    private static final int COL_ACTIVITE = 2;
    private static final int COL_ANNEE = 3;
    private static final int COL_FORME_JURIDIQUE = 4;
    private static final int COL_REGIME_FISCAL = 5;
    private static final int COL_REGIME_CNAS = 6;
    private static final int COL_RECETTE_IMPOTS = 7;
    private static final int COL_OBSERVATION = 8;
    private static final int COL_SOURCE = 9;
    private static final int COL_HONORAIRES_MOIS = 10;
    private static final int COL_MONTANT = 11;
    private static final int COL_TELEPHONE = 12;
    private static final int COL_COMPANY = 13;
    private static final int COL_ADDRESS = 14;
    private static final int COL_TYPE = 15;
    private static final int COL_PREMIER_VERSEMENT = 16;
    private static final int COL_CREATED_AT = 17;

    public ClientForm() {
        FlatLightLaf.setup();
        controller = new ClientController();
        versmentController = new VersmentController();

        initializeUI();
        setupTable();
        add(createFilterPanel(), BorderLayout.NORTH);
        setupButtons();
        loadClientData();
    }

    private void initializeUI() {
        setTitle("Client Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void setupTable() {
        String[] columnNames = {
                "ID", "Nom", "Activité", "Année",
                "Forme Juridique", "Régime Fiscal", "Régime CNAS",
                "Recette Impôts", "Observation", "Source",
                "Honoraires/Mois", "Montant Annual", "Montant Restant", "Téléphone",
                "Type", "Premier Versement", "Created At"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == COL_MONTANT)
                    return Double.class;
                if (columnIndex == COL_MONTANT + 1) // Montant Restant column
                    return String.class;
                if (columnIndex == COL_SOURCE)
                    return Integer.class;
                return String.class;
            }
        };

        clientTable = new JTable(tableModel);
        customizeTableAppearance();
        setupColumnWidths();

        // Initialize sorter
        sorter = new TableRowSorter<>(tableModel);
        clientTable.setRowSorter(sorter);

        // Hide ID column
        clientTable.removeColumn(clientTable.getColumnModel().getColumn(COL_ID));

        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void customizeTableAppearance() {
        Font tableFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font headerFont = new Font("Segoe UI", Font.BOLD, 14);

        clientTable.setFillsViewportHeight(true);
        clientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        clientTable.setFont(tableFont);
        clientTable.setRowHeight(30);
        clientTable.getTableHeader().setFont(headerFont);
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientTable.setShowGrid(true);
        clientTable.setGridColor(new Color(220, 220, 220));
        clientTable.setIntercellSpacing(new Dimension(0, 1));

        clientTable.setSelectionBackground(new Color(52, 152, 219));
        clientTable.setSelectionForeground(Color.WHITE);
    }

    private void setupColumnWidths() {
        TableColumnModel columnModel = clientTable.getColumnModel();

        columnModel.getColumn(COL_NOM - 1).setPreferredWidth(150); // Adjust for hidden ID column
        columnModel.getColumn(COL_ACTIVITE - 1).setPreferredWidth(200);
        columnModel.getColumn(COL_ANNEE - 1).setPreferredWidth(80);
        columnModel.getColumn(COL_MONTANT).setPreferredWidth(120); // Montant Annual
        columnModel.getColumn(COL_MONTANT + 1).setPreferredWidth(120); // Montant Restant

        for (int i = COL_FORME_JURIDIQUE - 1; i < columnModel.getColumnCount(); i++) {
            if (i != COL_MONTANT && i != COL_MONTANT + 1) {
                columnModel.getColumn(i).setPreferredWidth(120);
            }
        }
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Search Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(new JLabel("Quick Search:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });
        filterPanel.add(searchField, gbc);

        // Type Filter
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        filterPanel.add(new JLabel("Client Type:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        typeFilterCombo = new JComboBox<>(new String[] { "All Types", "Type A", "Type B", "Type C" });
        filterPanel.add(typeFilterCombo, gbc);

        // Amount Range
        gbc.gridx = 0;
        gbc.gridy = 2;
        filterPanel.add(new JLabel("Annual Amount:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        minAmountField = new JTextField(6);
        maxAmountField = new JTextField(6);
        amountPanel.add(new JLabel("From:"));
        amountPanel.add(minAmountField);
        amountPanel.add(new JLabel("To:"));
        amountPanel.add(maxAmountField);
        filterPanel.add(amountPanel, gbc);

        // Date Range
        gbc.gridx = 0;
        gbc.gridy = 3;
        filterPanel.add(new JLabel("Created Between:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        fromDatePicker = new JXDatePicker();
        toDatePicker = new JXDatePicker();
        fromDatePicker.setFormats("yyyy-MM-dd");
        toDatePicker.setFormats("yyyy-MM-dd");
        datePanel.add(fromDatePicker);
        datePanel.add(new JLabel("and"));
        datePanel.add(toDatePicker);
        filterPanel.add(datePanel, gbc);

        // Action Buttons
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton applyFilterButton = new JButton("Apply Filters");
        JButton clearFilterButton = new JButton("Clear Filters");
        JButton advancedFilterButton = new JButton("Advanced...");
        JButton toggleThemeButton = new JButton("🌓 Toggle Theme");

        buttonPanel.add(applyFilterButton);
        buttonPanel.add(clearFilterButton);
        buttonPanel.add(advancedFilterButton);
        buttonPanel.add(toggleThemeButton);
        filterPanel.add(buttonPanel, gbc);

        // Add action listeners
        applyFilterButton.addActionListener(e -> applyFilters());

        clearFilterButton.addActionListener(e -> {
            searchField.setText("");
            typeFilterCombo.setSelectedIndex(0);
            minAmountField.setText("");
            maxAmountField.setText("");
            fromDatePicker.setDate(null);
            toDatePicker.setDate(null);
            sorter.setRowFilter(null);
        });

        advancedFilterButton.addActionListener(e -> showAdvancedFilterDialog());
        toggleThemeButton.addActionListener(e -> toggleTheme());

        return filterPanel;
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // Text Search Filter
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            filters.add(RowFilter.orFilter(Arrays.asList(
                    RowFilter.regexFilter("(?i)" + searchText, COL_NOM),
                    RowFilter.regexFilter("(?i)" + searchText, COL_ACTIVITE),
                    RowFilter.regexFilter("(?i)" + searchText, COL_COMPANY))));
        }

        // Type Filter
        if (typeFilterCombo.getSelectedIndex() > 0) {
            String selectedType = (String) typeFilterCombo.getSelectedItem();
            filters.add(RowFilter.regexFilter("^" + selectedType + "$", COL_TYPE));
        }

        // Amount Range Filter
        try {
            if (!minAmountField.getText().isEmpty()) {
                double min = Double.parseDouble(minAmountField.getText());
                filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.AFTER, min, COL_MONTANT));
            }
            if (!maxAmountField.getText().isEmpty()) {
                double max = Double.parseDouble(maxAmountField.getText());
                filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.BEFORE, max, COL_MONTANT));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for amount range",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Date Range Filter
        Date fromDate = fromDatePicker.getDate();
        Date toDate = toDatePicker.getDate();
        if (fromDate != null || toDate != null) {
            filters.add(new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<?, ?> entry) {
                    try {
                        String dateStr = (String) entry.getValue(COL_CREATED_AT);
                        Date recordDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);

                        return (fromDate == null || recordDate.after(fromDate)) &&
                                (toDate == null || recordDate.before(toDate));
                    } catch (Exception ex) {
                        return false;
                    }
                }
            });
        }

        // Apply combined filter
        if (!filters.isEmpty()) {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        } else {
            sorter.setRowFilter(null);
        }
    }

    private void showAdvancedFilterDialog() {
        JDialog dialog = new JDialog(this, "Advanced Filters", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 5));

        JTextField specificActivityField = new JTextField();
        JComboBox<String> regimeCombo = new JComboBox<>(new String[] { "Any", "Regime A", "Regime B" });
        JCheckBox newClientsOnly = new JCheckBox("New Clients Only (Last 30 days)");

        formPanel.add(new JLabel("Specific Activity:"));
        formPanel.add(specificActivityField);
        formPanel.add(new JLabel("Fiscal Regime:"));
        formPanel.add(regimeCombo);
        formPanel.add(new JLabel(""));
        formPanel.add(newClientsOnly);

        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            List<RowFilter<Object, Object>> newFilters = new ArrayList<>();

            // Get current filter and add it to new filters
            RowFilter<? super DefaultTableModel, ? super Integer> currentFilter = sorter.getRowFilter();
            if (currentFilter != null) {
                newFilters.add((RowFilter<Object, Object>) currentFilter);
            }

            // Add specific activity filter if specified
            if (!specificActivityField.getText().isEmpty()) {
                newFilters.add(RowFilter.regexFilter(
                        "(?i)" + specificActivityField.getText(),
                        COL_ACTIVITE));
            }

            // Add regime filter if specified
            if (regimeCombo.getSelectedIndex() > 0) {
                newFilters.add(RowFilter.regexFilter(
                        "^" + regimeCombo.getSelectedItem() + "$",
                        COL_REGIME_FISCAL));
            }

            // Add new clients filter if checked
            if (newClientsOnly.isSelected()) {
                newFilters.add(new RowFilter<Object, Object>() {
                    @Override
                    public boolean include(Entry<?, ?> entry) {
                        try {
                            String dateStr = (String) entry.getValue(COL_CREATED_AT);
                            Date recordDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
                            Date thirtyDaysAgo = new Date(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000));
                            return recordDate.after(thirtyDaysAgo);
                        } catch (Exception ex) {
                            return false;
                        }
                    }
                });
            }

            // Apply the new filters
            if (!newFilters.isEmpty()) {
                sorter.setRowFilter(RowFilter.andFilter(newFilters));
            } else {
                sorter.setRowFilter(null);
            }

            dialog.dispose();
        });

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(applyButton, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void setupButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        JButton addButton = createButton("Ajouter client", e -> showAddDialog());
        JButton editButton = createButton("Modifier client", e -> showEditDialog());
        JButton deleteButton = createButton("Supprimer client", e -> deleteClient());
        JButton refreshButton = createButton("Actualiser", e -> refreshClientTable());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        button.addActionListener(listener);
        return button;
    }

    private void loadClientData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    List<Client> clients = controller.fetchAllClients();
                    SwingUtilities.invokeLater(() -> {
                        tableModel.setRowCount(0);
                        for (Client c : clients) {
                            tableModel.addRow(convertClientToRow(c));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(ClientForm.this,
                                "Erreur lors du chargement des données: " + e.getMessage(),
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }

            @Override
            protected void done() {
                clientTable.repaint();
            }
        };
        worker.execute();
    }

    private Object[] convertClientToRow(Client c) {
        // Calculate remaining amount
        BigDecimal remainingAmount = versmentController.getRemainingAmountForClient(c.getId());
        String remainingAmountStr = remainingAmount.toString() + " DA";
        
        // Add color coding info (you could use a custom renderer for this)
        if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {
            remainingAmountStr += " ✓"; // Fully paid
        } else if (remainingAmount.compareTo(new BigDecimal("1000")) < 0) {
            remainingAmountStr += " ⚠"; // Low remaining
        }
        
        return new Object[] {
                c.getId(), c.getNom(), c.getActivite(), c.getAnnee(),
                c.getFormeJuridique(), c.getRegimeFiscal(), c.getRegimeCnas(),
                c.getRecetteImpots(), c.getObservation(), c.getSource(),
                c.getHonorairesMois(), c.getMontant(), remainingAmountStr, c.getPhone(),
                c.getType(), c.getPremierVersement(), c.getCreatedAt()
        };
    }

    private void refreshClientTable() {
        loadClientData();
    }

    private void showAddDialog() {
        ClientDialog dialog = new ClientDialog(this, "Ajouter Client", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Client newClient = dialog.getClient();
            try {
                int result = controller.addClient(newClient);
                if (result > 0) {
                    refreshClientTable();
                    JOptionPane.showMessageDialog(this, "Client ajouté avec succès! ID: " + result);
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du client", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + e.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client", "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int modelRow = clientTable.convertRowIndexToModel(selectedRow);
            int clientId = (Integer) tableModel.getValueAt(modelRow, COL_ID);
            Client clientToEdit = controller.getClientById(clientId);

            if (clientToEdit != null) {
                ClientDialog dialog = new ClientDialog(this, "Modifier Client", clientToEdit);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    Client updatedClient = dialog.getClient();
                    if (controller.updateClient(updatedClient)) {
                        refreshClientTable();
                        JOptionPane.showMessageDialog(this, "Client modifié avec succès!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la modification", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Client non trouvé", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client", "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer ce client?\nCette action supprimera également tous ses versements.",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int modelRow = clientTable.convertRowIndexToModel(selectedRow);
                int clientId = (Integer) tableModel.getValueAt(modelRow, COL_ID);

                if (controller.deleteClient(clientId)) {
                    refreshClientTable();
                    JOptionPane.showMessageDialog(this, "Client supprimé avec succès!");
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + e.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ClientForm().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}