package edu.neu.mgen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SweetSpotAnalysisGUI extends JFrame {
    private JTextField numOfStrategiesField;
    private JTextField numOfFactorsField;
    private JPanel inputPanel;
    private JTable resultTable; 
    private DefaultTableModel tableModel; 

    public SweetSpotAnalysisGUI() {
        createView();

        setTitle("Sweet Spot Analysis GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600); 
        setLocationRelativeTo(null);
        setResizable(true); 
    }

    private void createView() {
        JPanel panel = new JPanel();
        getContentPane().add(panel);

        panel.setLayout(new BorderLayout());

        inputPanel = new JPanel(new GridLayout(0, 2));
        panel.add(inputPanel, BorderLayout.NORTH);

        // Create components for user input
        inputPanel.add(new JLabel("Enter the number of strategies: "));
        numOfStrategiesField = new JTextField();
        inputPanel.add(numOfStrategiesField);

        inputPanel.add(new JLabel("Enter the number of factors: "));
        numOfFactorsField = new JTextField();
        inputPanel.add(numOfFactorsField);

        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEnter();
            }
        });
        panel.add(enterButton, BorderLayout.CENTER);

        
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable); 
        panel.add(scrollPane, BorderLayout.SOUTH); 
    }

    private void onEnter() {
        int numOfStrategies;
        int numOfFactors;
        try {
            numOfStrategies = Integer.parseInt(numOfStrategiesField.getText());
            numOfFactors = Integer.parseInt(numOfFactorsField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for strategies and factors.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Initialize arrays based on user input
        String[] factorNames = new String[numOfFactors];
        String[][] factorValues = new String[numOfStrategies][numOfFactors];
        String[] strategyNames = new String[numOfStrategies];
        String[] factorTypes = new String[numOfFactors];
    
        for (int i = 0; i < numOfFactors; i++) {
            factorNames[i] = JOptionPane.showInputDialog("Enter factor " + (i + 1) + " name:");
        
            String factorType;
            do {
                factorType = JOptionPane.showInputDialog("Enter factor " + factorNames[i] + " type (text/numeric):");
                if (!factorType.equalsIgnoreCase("text") && !factorType.equalsIgnoreCase("numeric")) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid factor type. Please enter 'text' or 'numeric'.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } while (!factorType.equalsIgnoreCase("text") && !factorType.equalsIgnoreCase("numeric")); // Validate factor type
            
            factorTypes[i] = factorType; // Store factor type
        }
    
        for (int i = 0; i < numOfStrategies; i++) {
            strategyNames[i] = JOptionPane.showInputDialog("Enter Strategy " + (i + 1) + " name:");
            for (int j = 0; j < numOfFactors; j++) {
                if ("text".equalsIgnoreCase(factorTypes[j])) {
                    String input;
                    do {
                        input = JOptionPane.showInputDialog("Enter factor " + factorNames[j] + " value (high/medium/low):");
                        if (!isValidChoice(input)) {
                            JOptionPane.showMessageDialog(this,
                                    "Input Type is wrong. Please enter correct input.",
                                    "Input Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } while (!isValidChoice(input));
    
                    factorValues[i][j] = input;
                } else {
                    String input;
                    do {
                        input = JOptionPane.showInputDialog("Enter factor " + factorNames[j] + " value:");
                        if (!isNumeric(input)) {
                            JOptionPane.showMessageDialog(this,
                                    "Input Type is wrong. Please enter a numeric value.",
                                    "Input Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } while (!isNumeric(input));
    
                    factorValues[i][j] = input;
                }
            }
        }
    
        // Sort strategies based on factors
        sortStrategies(strategyNames, factorValues, factorTypes, numOfFactors);
    
        // Display the table in resultTable
        displayResults(factorNames, strategyNames, factorValues);
    }
    
    private boolean isValidChoice(String choice) {
        return "high".equalsIgnoreCase(choice) || "medium".equalsIgnoreCase(choice) || "low".equalsIgnoreCase(choice);
    }
    
    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    

    private void displayResults(String[] factorNames, String[] strategyNames, String[][] factorValues) {
        tableModel.setRowCount(0); 
        tableModel.setColumnCount(0); 

        tableModel.addColumn("Strategy/Factor");
        for (String factorName : factorNames) {
            tableModel.addColumn(factorName);
        }

        for (int i = 0; i < strategyNames.length; i++) {
            Object[] row = new Object[factorNames.length + 1];
            row[0] = strategyNames[i]; 
            System.arraycopy(factorValues[i], 0, row, 1, factorValues[i].length);
            tableModel.addRow(row); 
        }
        JOptionPane.showMessageDialog(this,
            "Strategy "+strategyNames[0]+" is the best Strategy!!",
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void sortStrategies(String[] strategyNames, String[][] factorValues, String[] factorTypes, int numOfFactors) {
        for (int i = 0; i < strategyNames.length - 1; i++) {
            for (int j = i + 1; j < strategyNames.length; j++) {
                if (compareStrategies(factorValues[i], factorValues[j], factorTypes, numOfFactors) > 0) {
                    // Swap strategy names
                    String tempName = strategyNames[i];
                    strategyNames[i] = strategyNames[j];
                    strategyNames[j] = tempName;

                    // Swap factor values
                    String[] tempValues = factorValues[i];
                    factorValues[i] = factorValues[j];
                    factorValues[j] = tempValues;
                }
            }
        }
        JOptionPane.showMessageDialog(this,
            "Sweet Spot Analysis completed Successfully!!!",
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private int compareStrategies(String[] factors1, String[] factors2, String[] factorTypes, int numOfFactors) {
        for (int i = 0; i < numOfFactors; i++) {
            int comparisonResult;
            if ("numeric".equalsIgnoreCase(factorTypes[i])) {
                int num1 = Integer.parseInt(factors1[i]);
                int num2 = Integer.parseInt(factors2[i]);
                comparisonResult = Integer.compare(num1, num2); // Sort in Ascending order
            } else {
                comparisonResult = factors1[i].compareToIgnoreCase(factors2[i]);
            }

            if (comparisonResult != 0) {
                return comparisonResult;
            }
        }
        // If all factors are same, compare by strategy name
        return factors1[0].compareToIgnoreCase(factors2[0]);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SweetSpotAnalysisGUI().setVisible(true);
            }
        });
    }
}
