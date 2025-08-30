package com.yourcompany.clientmanagement.view;

import com.yourcompany.clientmanagement.model.Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ClientDialog extends JDialog {
    private JTextField nomField, activiteField, anneeField;
    private JTextField formeJuridiqueField, regimeFiscalField, regimeCnasField;
    private JTextField recetteImpotsField, observationField, sourceField;
    private JTextField honorairesMoisField, montantAnnualField, phoneField;
    private JTextField companyField, addressField, typeField, premierVersementField;
    private boolean confirmed = false;
    private Client client;

    public ClientDialog(JFrame parent, String title, Client client) {
        super(parent, title, true);
        this.client = client;
        initializeUI();
        populateFields();
    }

    private void initializeUI() {
        setSize(700, 500); // Adjusted size for better fit
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Main form panel with 2x2 grid
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create input fields with placeholders
        nomField = createFieldWithPlaceholder("Entrez le nom du client");
        activiteField = createFieldWithPlaceholder("Activité principale");
        anneeField = createFieldWithPlaceholder("Année d'activité");
        formeJuridiqueField = createFieldWithPlaceholder("Forme juridique");
        regimeFiscalField = createFieldWithPlaceholder("Régime fiscal");
        regimeCnasField = createFieldWithPlaceholder("Régime CNAS");
        recetteImpotsField = createFieldWithPlaceholder("Recette impôts");
        observationField = createFieldWithPlaceholder("Observations");
        sourceField = createFieldWithPlaceholder("Source (numérique)");
        honorairesMoisField = createFieldWithPlaceholder("Honoraires/mois");
        montantAnnualField = createFieldWithPlaceholder("Montant annuel");
        phoneField = createFieldWithPlaceholder("Numéro de téléphone");
        companyField = createFieldWithPlaceholder("Nom de l'entreprise");
        addressField = createFieldWithPlaceholder("Adresse");
        typeField = createFieldWithPlaceholder("Type de client");
        premierVersementField = createFieldWithPlaceholder("Premier versement");

        // Add numeric validation where needed
        addNumericValidation(montantAnnualField);
        addNumericValidation(sourceField);

        // Add fields to form in 2x2 grid layout
        addFieldWithLabel(formPanel, "Nom*:", nomField);
        addFieldWithLabel(formPanel, "Activité*:", activiteField);
        addFieldWithLabel(formPanel, "Année:", anneeField);
        addFieldWithLabel(formPanel, "Forme Juridique:", formeJuridiqueField);
        addFieldWithLabel(formPanel, "Régime Fiscal:", regimeFiscalField);
        addFieldWithLabel(formPanel, "Régime CNAS:", regimeCnasField);
        addFieldWithLabel(formPanel, "Recette Impôts:", recetteImpotsField);
        addFieldWithLabel(formPanel, "Observation:", observationField);
        addFieldWithLabel(formPanel, "Source:", sourceField);
        addFieldWithLabel(formPanel, "Honoraires/Mois:", honorairesMoisField);
        addFieldWithLabel(formPanel, "Montant Annual:", montantAnnualField);
        addFieldWithLabel(formPanel, "Téléphone:", phoneField);
        addFieldWithLabel(formPanel, "Entreprise:", companyField);
        addFieldWithLabel(formPanel, "Adresse:", addressField);
        addFieldWithLabel(formPanel, "Type:", typeField);
        addFieldWithLabel(formPanel, "Premier Versement:", premierVersementField);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 10));

        JButton okButton = new JButton("Enregistrer");
        okButton.setPreferredSize(new Dimension(120, 35));
        okButton.addActionListener(e -> validateAndClose());

        JButton cancelButton = new JButton("Annuler");
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);
    }

    private JTextField createFieldWithPlaceholder(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }

    private void addFieldWithLabel(JPanel panel, String labelText, JTextField field) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(150, 30)); // Fixed width for alignment
        
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(field, BorderLayout.CENTER);
        
        panel.add(fieldPanel);
    }

    private void addNumericValidation(JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE || c == '.')) {
                    e.consume();
                }
            }
        });
    }

    private void populateFields() {
        if (client == null) return;

        setFieldText(nomField, client.getNom());
        setFieldText(activiteField, client.getActivite());
        setFieldText(anneeField, client.getAnnee());
        setFieldText(formeJuridiqueField, client.getFormeJuridique());
        setFieldText(regimeFiscalField, client.getRegimeFiscal());
        setFieldText(regimeCnasField, client.getRegimeCnas());
        setFieldText(recetteImpotsField, client.getRecetteImpots());
        setFieldText(observationField, client.getObservation());
        setFieldText(sourceField, client.getSource() != null ? client.getSource().toString() : "");
        setFieldText(honorairesMoisField, client.getHonorairesMois());
        setFieldText(montantAnnualField, client.getMontant() != null ? String.valueOf(client.getMontant()) : "");
        setFieldText(phoneField, client.getPhone());
        setFieldText(companyField, client.getCompany());
        setFieldText(addressField, client.getAddress());
        setFieldText(typeField, client.getType());
        setFieldText(premierVersementField, client.getPremierVersement());
    }

    private void setFieldText(JTextField field, String text) {
        if (text != null && !text.isEmpty()) {
            field.setText(text);
        }
    }

    private void validateAndClose() {
        if (nomField.getText().trim().isEmpty()) {
            showValidationError("Le champ 'Nom' est obligatoire", nomField);
            return;
        }
        
        if (activiteField.getText().trim().isEmpty()) {
            showValidationError("Le champ 'Activité' est obligatoire", activiteField);
            return;
        }
        
        if (!validateNumericField(montantAnnualField, "Montant Annual")) return;
        if (!validateNumericField(sourceField, "Source")) return;

        confirmed = true;
        dispose();
    }

    private boolean validateNumericField(JTextField field, String fieldName) {
        String text = field.getText().trim();
        if (!text.isEmpty()) {
            try {
                Double.parseDouble(text);
            } catch (NumberFormatException e) {
                showValidationError("Format invalide pour " + fieldName, field);
                return false;
            }
        }
        return true;
    }

    private void showValidationError(String message, JComponent field) {
        JOptionPane.showMessageDialog(this, message, "Validation", JOptionPane.WARNING_MESSAGE);
        field.requestFocus();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Client getClient() {
        if (!confirmed) return null;

        Client c = client != null ? client : new Client();
        
        c.setNom(nomField.getText().trim());
        c.setActivite(activiteField.getText().trim());
        c.setAnnee(anneeField.getText().trim());
        c.setFormeJuridique(formeJuridiqueField.getText().trim());
        c.setRegimeFiscal(regimeFiscalField.getText().trim());
        c.setRegimeCnas(regimeCnasField.getText().trim());
        c.setRecetteImpots(recetteImpotsField.getText().trim());
        c.setObservation(observationField.getText().trim());
        
        String sourceText = sourceField.getText().trim();
        c.setSource(sourceText.isEmpty() ? null : Integer.parseInt(sourceText));
        
        c.setHonorairesMois(honorairesMoisField.getText().trim());
        
        String montantText = montantAnnualField.getText().trim();
        c.setMontant(montantText.isEmpty() ? null : Double.parseDouble(montantText));
        
        c.setPhone(phoneField.getText().trim());
        c.setCompany(companyField.getText().trim());
        c.setAddress(addressField.getText().trim());
        c.setType(typeField.getText().trim());
        c.setPremierVersement(premierVersementField.getText().trim());
        
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        if (client == null) {
            c.setCreatedAt(currentDate);
        }
        c.setUpdatedAt(currentDate);

        return c;
    }
}