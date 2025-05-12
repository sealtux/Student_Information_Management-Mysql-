package Essentials.modifiedEvents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Essentials.GUI;
import java.awt.Frame;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class modifycollege {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydata?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    
    public modifycollege(GUI gui, String oldCode, String oldName) {
        int rowIndex = findRowIndex(gui.getcollegeModel(), oldCode);
        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(null, "College not found in the table!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        openModifyDialog(gui, oldCode, oldName, rowIndex);
    }

   
    public modifycollege(GUI gui) {
        JTable collegeTable = gui.getcollegeTable();

        JDialog modifyDialog = new JDialog((Frame) null, "Modify College", true);
        modifyDialog.setLayout(null);
        modifyDialog.setSize(350, 220);
        modifyDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel infoLabel = new JLabel("Select a row and press 'Modify':");
        JButton modifyButton = new JButton("Modify");

        infoLabel.setBounds(50, 30, 200, 25);
        modifyButton.setBounds(100, 80, 130, 25);

        modifyDialog.add(infoLabel);
        modifyDialog.add(modifyButton);
        modifyDialog.setLocationRelativeTo(null);
        modifyDialog.setResizable(false);

        modifyButton.addActionListener(e -> {
            DefaultTableModel collegeModel = gui.getcollegeModel();
            int selectedRow = collegeTable.convertRowIndexToModel(collegeTable.getSelectedRow());
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(modifyDialog, "Please select a row first!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String collegeCode = collegeModel.getValueAt(selectedRow, 0).toString().trim();
            String collegeName = collegeModel.getValueAt(selectedRow, 1).toString().trim();

            modifyDialog.dispose();
            openModifyDialog(gui, collegeCode, collegeName, selectedRow);
        });

        modifyDialog.setVisible(true);
    }

    private int findRowIndex(DefaultTableModel collegeModel, String collegeCode) {
        for (int i = 0; i < collegeModel.getRowCount(); i++) {
            if (collegeModel.getValueAt(i, 0).toString().trim().equals(collegeCode)) {
                return i;
            }
        }
        return -1;
    }

    private void openModifyDialog(GUI gui, String oldCode, String oldName, int rowIndex) {
        JDialog editDialog = new JDialog((JFrame) null, "Edit College", true);
        editDialog.setSize(350, 250);
        editDialog.setLayout(null);
        editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel codeLabel = new JLabel("College Code:");
        JTextField codeField = new JTextField(oldCode);
        JLabel nameLabel = new JLabel("College Name:");
        JTextField nameField = new JTextField(oldName);

        codeLabel.setBounds(20, 20, 100, 25);
        codeField.setBounds(130, 20, 150, 25);
        nameLabel.setBounds(20, 60, 100, 25);
        nameField.setBounds(130, 60, 150, 25);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(130, 120, 100, 30);

        editDialog.add(codeLabel);
        editDialog.add(codeField);
        editDialog.add(nameLabel);
        editDialog.add(nameField);
        editDialog.add(updateButton);

        updateButton.addActionListener(ae -> {
            String newCode = codeField.getText().trim();
            String newName = nameField.getText().trim();

         
            if (!newCode.matches(".*[a-zA-Z].*")) {
                JOptionPane.showMessageDialog(editDialog, "College Code must contain a value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newName.matches(".*[a-zA-Z].*")) {
                JOptionPane.showMessageDialog(editDialog, "College Name must contain a value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (collegeExists(gui.getcollegeModel(), newCode, newName, rowIndex)) {
                JOptionPane.showMessageDialog(editDialog, "A college with this code or name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(editDialog,
                    "Are you sure you want to update this college?", "Confirm Update",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                
                DefaultTableModel collegeModel = gui.getcollegeModel();
                collegeModel.setValueAt(newCode, rowIndex, 0);
                collegeModel.setValueAt(newName, rowIndex, 1);

                
                updateCollegeRecordInDatabase(oldCode, newCode, newName);
                updateProgramsCollegeCodeInDatabase(oldCode, newCode);
                
                updateProgramsCollegeCodeInTableModel(gui, oldCode, newCode);

                JOptionPane.showMessageDialog(editDialog, "College record updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                editDialog.dispose();
            }
        });

        editDialog.setLocationRelativeTo(null);
        editDialog.setResizable(false);
        editDialog.setVisible(true);
    }

    private boolean collegeExists(DefaultTableModel model, String code, String name, int ignoreRow) {
        String codeKey = code.trim().toLowerCase();
        String nameKey = name.trim().toLowerCase();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i == ignoreRow) continue;
            String existingCode = model.getValueAt(i, 0).toString().trim().toLowerCase();;
            String existingName = model.getValueAt(i, 1).toString().trim().toLowerCase();;
            if (existingCode.equals(codeKey) || existingName.equals(nameKey)) {
                return true;
            }
        }
        return false;
    }

 
    private void updateCollegeRecordInDatabase(String oldCode, String newCode, String newName) {
        String sql = "UPDATE college SET CollegeCode = ?, CollegeName = ? WHERE CollegeCode = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, newCode);
            pstmt.setString(2, newName);
            pstmt.setString(3, oldCode);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("College record updated successfully in the database.");
            } else {
                System.out.println("No college record found for CollegeCode: " + oldCode);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating college record in the database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void updateProgramsCollegeCodeInDatabase(String oldCollegeCode, String newCollegeCode) {
        String sql = "UPDATE program SET CollegeCode = ? WHERE CollegeCode = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, newCollegeCode);
            pstmt.setString(2, oldCollegeCode);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Program records updated with new CollegeCode in the database.");
            } else {
                System.out.println("No program records required updating for CollegeCode: " + oldCollegeCode);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating program records in the database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

  
    private void updateProgramsCollegeCodeInTableModel(GUI gui, String oldCollegeCode, String newCollegeCode) {
        DefaultTableModel programModel = gui.getprogramModel();
        
        int collegeCodeCol = 2;
        for (int i = 0; i < programModel.getRowCount(); i++) {
            String currentCode = programModel.getValueAt(i, collegeCodeCol).toString().trim();
            if (currentCode.equals(oldCollegeCode)) {
                programModel.setValueAt(newCollegeCode, i, collegeCodeCol);
            }
        }
        System.out.println("Program table model updated with new CollegeCode.");
    }
}
