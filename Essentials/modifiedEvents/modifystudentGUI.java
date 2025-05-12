package Essentials.modifiedEvents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Essentials.GUI;
import java.sql.*;

public class modifystudentGUI {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydata?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    
    private GUI maingui;
    private DefaultTableModel model;
    private int rowIndex; // store for firing updates
    
    public modifystudentGUI(GUI gui, String id, String firstName, String lastName, String yearLevel, String gender, String programCode) {
        this.maingui = gui;
        this.model = maingui.getstudentModel();
        
        int selectedRow = maingui.getStudentTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a row to modify.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Convert view index to model index
        this.rowIndex = maingui.getStudentTable().convertRowIndexToModel(selectedRow);
        modifiedFrame(rowIndex, id, firstName, lastName, yearLevel, gender, programCode);
    }
    
    private void modifiedFrame(int rowIndex, String id, String firstName, String lastName, String yearLevel, String gender, String programCode) {
        JDialog editDialog = new JDialog((java.awt.Frame) null, "Edit Student", true);
        editDialog.setSize(350, 350);
        editDialog.setLayout(null);
        editDialog.setLocationRelativeTo(null);
        editDialog.setResizable(false);
        
        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField(id);
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(firstName);
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(lastName);
        JLabel yearLevelLabel = new JLabel("Year Level:");
        JLabel genderLabel = new JLabel("Gender:");
        JLabel programCodeLabel = new JLabel("Program Code:");
        
        String[] yearModel = {"", "1st year", "2nd year", "3rd year", "4th year"};
        JComboBox<String> yearLevelCombo = new JComboBox<>(yearModel);
        yearLevelCombo.setSelectedItem(yearLevel);
        
        String[] genderOptions = {"", "M", "F"};
        JComboBox<String> genderCombo = new JComboBox<>(genderOptions);
        genderCombo.setSelectedItem(gender);
        
        DefaultTableModel programModel = maingui.getprogramModel();
        JComboBox<String> programCodeCombo = new JComboBox<>();
        for (int i = 0; i < programModel.getRowCount(); i++) {
            Object obj = programModel.getValueAt(i, 0);
            if (obj != null) programCodeCombo.addItem(obj.toString().trim());
        }
        programCodeCombo.setSelectedItem(programCode);
        
        JButton updateButton = new JButton("Update");
        
        idLabel.setBounds(20, 20, 100, 25);
        idField.setBounds(130, 20, 150, 25);
        firstNameLabel.setBounds(20, 60, 100, 25);
        firstNameField.setBounds(130, 60, 150, 25);
        lastNameLabel.setBounds(20, 100, 100, 25);
        lastNameField.setBounds(130, 100, 150, 25);
        yearLevelLabel.setBounds(20, 140, 100, 25);
        yearLevelCombo.setBounds(130, 140, 150, 25);
        genderLabel.setBounds(20, 180, 100, 25);
        genderCombo.setBounds(130, 180, 150, 25);
        programCodeLabel.setBounds(20, 220, 100, 25);
        programCodeCombo.setBounds(130, 220, 150, 25);
        updateButton.setBounds(130, 260, 100, 30);
        
        editDialog.add(idLabel);
        editDialog.add(idField);
        editDialog.add(firstNameLabel);
        editDialog.add(firstNameField);
        editDialog.add(lastNameLabel);
        editDialog.add(lastNameField);
        editDialog.add(yearLevelLabel);
        editDialog.add(yearLevelCombo);
        editDialog.add(genderLabel);
        editDialog.add(genderCombo);
        editDialog.add(programCodeLabel);
        editDialog.add(programCodeCombo);
        editDialog.add(updateButton);
        
        updateButton.addActionListener(e -> {
            String newId = idField.getText().trim();
            String newFirstName = firstNameField.getText().trim();
            String newLastName = lastNameField.getText().trim();
            String newYearLevel = (String) yearLevelCombo.getSelectedItem();
            String newGender = (String) genderCombo.getSelectedItem();
            String newProgramCode = (String) programCodeCombo.getSelectedItem();
            
            // All fields must be filled.
            if (newId.isEmpty() || newFirstName.isEmpty() || newLastName.isEmpty() ||
                newYearLevel.isEmpty() || newGender.isEmpty() || newProgramCode.isEmpty()) {
                JOptionPane.showMessageDialog(editDialog, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newId.matches("^(?!0000)\\d{4}-\\d{4}$")) {
                JOptionPane.showMessageDialog(editDialog, "Invalid ID format. It should be YYYY-NNNN.", "Invalid ID", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int yearPart = Integer.parseInt(newId.substring(0, 4));
            if (yearPart < 2000) {
                JOptionPane.showMessageDialog(editDialog, "The year in the ID must be 2000 or greater.", "Invalid ID", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Duplicate check
            for (int i = 0; i < model.getRowCount(); i++) {
                if (i == rowIndex) continue;
                Object idObj = model.getValueAt(i, 0);
                Object fnObj = model.getValueAt(i, 1);
                Object lnObj = model.getValueAt(i, 2);
                String existingId = (idObj != null) ? idObj.toString().trim() : "";
                String existingFirstName = (fnObj != null) ? fnObj.toString().trim() : "";
                String existingLastName = (lnObj != null) ? lnObj.toString().trim() : "";
                if (existingId.equals(newId) ||
                    (existingFirstName.equalsIgnoreCase(newFirstName) && existingLastName.equalsIgnoreCase(newLastName))) {
                    JOptionPane.showMessageDialog(editDialog, "A record with this inputted value already exists.", "Duplicate Record", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
           
            int choice = JOptionPane.showConfirmDialog(
                editDialog,
                "Are you sure you want to update this record?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                model.setValueAt(newId, rowIndex, 0);
                model.setValueAt(newFirstName, rowIndex, 1);
                model.setValueAt(newLastName, rowIndex, 2);
                model.setValueAt(newYearLevel, rowIndex, 3);
                model.setValueAt(newGender, rowIndex, 4);
                model.setValueAt(newProgramCode, rowIndex, 5);

                updateStudentRecordsInDatabase(id, newId, newFirstName, newLastName, newYearLevel, newGender, newProgramCode);
                // Refresh row
                model.fireTableRowsUpdated(rowIndex, rowIndex);

                JOptionPane.showMessageDialog(editDialog, "Student record updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                editDialog.dispose();
            }
        });
        
        editDialog.setVisible(true);
    }

    private void updateStudentRecordsInDatabase(
            String oldId,
            String newId,
            String firstName,
            String lastName,
            String yearLevel,
            String gender,
            String programCode) {
        String sql = "UPDATE student SET " +
                     "StudentID = ?, " +
                     "FirstName = ?, " +
                     "LastName = ?, " +
                     "YearLevel = ?, " +
                     "Gender = ?, " +
                     "ProgramCode = ? " +
                     "WHERE StudentID = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newId);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, yearLevel);
            pstmt.setString(5, gender);
            pstmt.setString(6, programCode);
            pstmt.setString(7, oldId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student record updated successfully in the database.");
            } else {
                System.out.println("No student found with ID: " + oldId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error updating student records in the database: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}