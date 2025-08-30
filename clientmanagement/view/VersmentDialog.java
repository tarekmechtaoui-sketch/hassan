based on search text
            List<ClientItem> filtered = allClientItems.stream()
                .filter(item -> item.toString().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
            
            for (ClientItem item : filtered) {
                clientModel.addElement(item);
            }
        }
        
        clientComboBox.setPopupVisible(true);
        ((JTextField)clientComboBox.getEditor().getEditorComponent()).setText(searchText);
    }

    private void addFormField(JPanel panel, String label, JComponent field) {
        panel.add(new JLabel(label));
        panel.add(field);
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

    private void loadClients() {
        List<Client> clients = clientController.fetchAllClients();
        allClientItems = clients.stream()
            .map(ClientItem::new)
            .collect(Collectors.toList());
        
        for (ClientItem item : allClientItems) {
            clientModel.addElement(item);
        }
                // Update remaining amount if this is the only match
               
    }

    private void populateFields() {
        if (versment == null) return;

        // Select the correct client
        for (int i = 0; i < clientComboBox.getItemCount(); i++) {
            ClientItem item = clientComboBox.getItemAt(i);
            if (item.getClient().getId() == versment.getClientId()) {
                clientComboBox.setSelectedIndex(i);
                updateRemainingAmount(item.getClient().getId());
                break;
            }
        }

        montantField.setText(versment.getMontant() != null ? versment.getMontant().toString() : "");
        typeComboBox.setSelectedItem(versment.getType());
        
        if (versment.getDatePaiement() != null) {
            datePaiementField.setText(versment.getDatePaiement().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        
        anneeConcerneeField.setText(versment.getAnneeConcernee());
    }

    private void updateRemainingAmount(int clientId) {
        try {
            BigDecimal remaining = versmentController.getRemainingAmountForClient(clientId);
            remainingAmountLabel.setText("Montant restant: " + remaining.toString() + " DA");
            
            // Change color based on remaining amount
            if (remaining.compareTo(BigDecimal.ZERO) == 0) {
                remainingAmountLabel.setForeground(new Color(244, 67, 54)); // Red - fully paid
                remainingAmountLabel.setText("Montant restant: " + remaining.toString() + " DA (Entièrement payé)");
            } else if (remaining.compareTo(new BigDecimal("1000")) < 0) {
                remainingAmountLabel.setForeground(new Color(255, 152, 0)); // Orange - low remaining
            } else {
                remainingAmountLabel.setForeground(new Color(46, 125, 50)); // Green - good remaining
            }
        } catch (Exception e) {
            remainingAmountLabel.setText("Montant restant: Erreur de calcul");
            remainingAmountLabel.setForeground(Color.RED);
        }
    }

    private void updateRemainingAmountPreview() {
        if (remainingAmountLabel == null) {
            return;
        }
        
        Object selected = clientComboBox.getSelectedItem();
        if (selected instanceof ClientItem) {
            ClientItem selectedClient = (ClientItem) selected;
            try {
                BigDecimal currentRemaining = versmentController.getRemainingAmountForClient(selectedClient.getClient().getId());
                String amountText = montantField.getText().trim();
                
                if (!amountText.isEmpty()) {
                    BigDecimal newVersmentAmount = new BigDecimal(amountText);
                    BigDecimal afterVersment = currentRemaining.subtract(newVersmentAmount);
                    
                    if (afterVersment.compareTo(BigDecimal.ZERO) < 0) {
                        remainingAmountLabel.setText("Montant restant: " + currentRemaining.toString() + 
                            " DA → " + afterVersment.toString() + " DA (Dépassement!)");
                        remainingAmountLabel.setForeground(Color.RED);
                    } else {
                        remainingAmountLabel.setText("Montant restant: " + currentRemaining.toString() + 
                            " DA → " + afterVersment.toString() + " DA");
                        remainingAmountLabel.setForeground(new Color(46, 125, 50));
                    }
                } else {
                    updateRemainingAmount(selectedClient.getClient().getId());
                }
            } catch (NumberFormatException e) {
                updateRemainingAmount(selectedClient.getClient().getId());
            }
        }
    }

    private void validateAndClose() {
        // Validate required fields
        Object selected = clientComboBox.getSelectedItem();
        if (selected == null || (selected instanceof String && ((String)selected).isEmpty())) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client valide", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!(selected instanceof ClientItem)) {
            // Try to find matching client
            String searchText = selected.toString();
            for (ClientItem item : allClientItems) {
                if (item.toString().equalsIgnoreCase(searchText)) {
                    clientComboBox.setSelectedItem(item);
                    selected = item;
                    break;
                }
            }
            
            if (!(selected instanceof ClientItem)) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client valide", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (montantField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le montant est obligatoire", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (typeComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Le type est obligatoire", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (datePaiementField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La date de paiement est obligatoire", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (anneeConcerneeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "L'année concernée est obligatoire", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate amount format
        try {
            new BigDecimal(montantField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format de montant invalide", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate date format
        try {
            LocalDate.parse(datePaiementField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format de date invalide (YYYY-MM-DD)", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate that versment amount doesn't exceed remaining amount (optional warning)
        if (selected instanceof ClientItem) {
            ClientItem selectedClient = (ClientItem) selected;
            try {
                BigDecimal currentRemaining = versmentController.getRemainingAmountForClient(selectedClient.getClient().getId());
                BigDecimal versmentAmount = new BigDecimal(montantField.getText().trim());
                
                if (versmentAmount.compareTo(currentRemaining) > 0) {
                    int choice = JOptionPane.showConfirmDialog(this,
                        "Le montant du versement (" + versmentAmount + " DA) dépasse le montant restant (" + 
                        currentRemaining + " DA).\n\nVoulez-vous continuer quand même?",
                        "Montant dépassé",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    
                    if (choice != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
            } catch (NumberFormatException e) {
                // Amount validation will be handled below
            }
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Versment getVersment() {
        if (!confirmed) return null;

        Versment v = versment != null ? versment : new Versment();
        
        Object selected = clientComboBox.getSelectedItem();
        if (!(selected instanceof ClientItem)) {
            return null;
        }
        
        ClientItem selectedClient = (ClientItem) selected;
        v.setClientId(selectedClient.getClient().getId());
        v.setMontant(new BigDecimal(montantField.getText().trim()));
        v.setType((String) typeComboBox.getSelectedItem());
        v.setDatePaiement(LocalDate.parse(datePaiementField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        v.setAnneeConcernee(anneeConcerneeField.getText().trim());
        
        // Set creation time for new versments
        if (versment == null) {
            v.setCreatedAt(LocalDateTime.now());
        }

        return v;
    }
}