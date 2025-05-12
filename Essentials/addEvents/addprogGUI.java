package Essentials.addEvents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Essentials.GUI;
import Essentials.create;
import java.awt.Frame;

public class addprogGUI {
    public static final String PROGRAM_FILE = "C:\\Users\\Admin\\Desktop\\ccc151\\program.csv";
    GUI mainGui;
    DefaultTableModel collegetable;

    public addprogGUI(GUI gui, create writer) {
        this.mainGui = gui;

        JDialog addProgramDialog = new JDialog((Frame) null, "Add Program", true);
        addProgramDialog.setSize(500, 280);
        addProgramDialog.setLayout(null);
        addProgramDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel collegecodeLabel = new JLabel("College Code:");
        JLabel programCodeLabel = new JLabel("Program Code:");
        JLabel programNameLabel = new JLabel("Program Name:");

        String[] collegeOptions = {};
        JComboBox<String> collegecodeCombo = new JComboBox<>(collegeOptions);

        JTextField programCodetext = new JTextField();
        JTextField programNameField = new JTextField();

        JButton submitButton = new JButton("Submit");

       
        collegecodeCombo.addItem(""); 
        DefaultTableModel collegetable = mainGui.getcollegeModel();
        for (int i = 0; i < collegetable.getRowCount(); i++) {
            collegecodeCombo.addItem(collegetable.getValueAt(i, 0).toString().trim());
        }
        
        collegecodeLabel.setBounds(20, 20, 100, 25);
        collegecodeCombo.setBounds(160, 20, 280, 25);
        programCodeLabel.setBounds(20, 60, 100, 25);
        programCodetext.setBounds(160, 60, 280, 25);
        programNameLabel.setBounds(20, 100, 100, 25);
        programNameField.setBounds(160, 100, 280, 25);
        submitButton.setBounds(160, 150, 150, 30);

        addProgramDialog.add(collegecodeLabel);
        addProgramDialog.add(collegecodeCombo);
        addProgramDialog.add(programCodeLabel);
        addProgramDialog.add(programCodetext);
        addProgramDialog.add(programNameLabel);
        addProgramDialog.add(programNameField);
        addProgramDialog.add(submitButton);

        addProgramDialog.setLocationRelativeTo(null);
        addProgramDialog.setResizable(false);

        submitButton.addActionListener(e -> {
            try {
                String programCode = programCodetext.getText().trim();
                String programName = programNameField.getText().trim();
                String college = (String) collegecodeCombo.getSelectedItem();

                if (programCode.isEmpty() || programName.isEmpty() || college.isEmpty()) {
                    JOptionPane.showMessageDialog(addProgramDialog, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String codeNorm = programCode;
                String nameNorm = programName;

                DefaultTableModel programModel = mainGui.getprogramModel();
                boolean duplicateFound = false;
              
                for (int i = 0; i < programModel.getRowCount(); i++) {
                String existingCode = programModel.getValueAt(i, 0).toString().trim();
                if (existingCode.equalsIgnoreCase(codeNorm)) {
                duplicateFound = true;
                break;
                }
                        }

                if (duplicateFound) {
                    JOptionPane.showMessageDialog(addProgramDialog, "A record with this Program Code already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                
                duplicateFound = false;
                for (int i = 0; i < programModel.getRowCount(); i++) {
                String existingName = programModel.getValueAt(i, 1).toString().trim();
                if (existingName.equalsIgnoreCase(nameNorm)) {
                duplicateFound = true;
                break;
                }
                    }
                if (duplicateFound) {
                    JOptionPane.showMessageDialog(addProgramDialog, "A record with this Program Name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

               
                int decision = JOptionPane.showConfirmDialog(
                        addProgramDialog,
                        "Are you sure you want to add the following program?\n" +
                        "Program Code: " + programCode + "\n" +
                        "Program Name: " + programName + "\n" +
                        "College: " + college,
                        "Confirm Addition",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (decision == JOptionPane.YES_OPTION) {
                   
                    programModel.addRow(new Object[]{programCode, programName, college});
                    writer.program(programCode, programName, college);
                    JOptionPane.showMessageDialog(addProgramDialog, "Program added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    addProgramDialog.dispose();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(addProgramDialog, "An error occurred while adding the program.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addProgramDialog.setVisible(true);
    }
}
