package Essentials.modifiedEvents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Essentials.GUI;
import java.awt.Frame;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class modifyprogram {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydata?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    public modifyprogram(GUI gui, String oldCode, String oldName, String oldCollegeCode) {
        int rowIndex = findRowIndex(gui.getprogramModel(), oldCode);
        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(null, "Program not found in the table!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        openModifyDialog(gui, oldCode, oldName, oldCollegeCode, rowIndex);
    }
    
    public modifyprogram(GUI gui) {
        JTable programTable = gui.getprogramTable();
        
        JDialog selectDialog = new JDialog((Frame) null, "Modify Program", true);
        selectDialog.setLayout(null);
        selectDialog.setSize(350, 220);
        selectDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel infoLabel = new JLabel("Select a row and press 'Modify':");
        JButton modifyButton = new JButton("Modify");

        infoLabel.setBounds(50, 30, 200, 25);
        modifyButton.setBounds(100, 80, 130, 25);

        selectDialog.add(infoLabel);
        selectDialog.add(modifyButton);
        selectDialog.setLocationRelativeTo(null);
        selectDialog.setResizable(false);

        modifyButton.addActionListener(e -> {
            DefaultTableModel programModel = gui.getprogramModel();
            int selectedRow = programTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(selectDialog, "Please select a row first!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
           
            int modelRow = selectedRow;
            String programCode = programModel.getValueAt(modelRow, 0).toString().trim();
            String programName = programModel.getValueAt(modelRow, 1).toString().trim();
            String collegeCode = programModel.getValueAt(modelRow, 2).toString().trim();

            selectDialog.dispose();
            openModifyDialog(gui, programCode, programName, collegeCode, modelRow);
        });

        selectDialog.setVisible(true);
    }

    private int findRowIndex(DefaultTableModel programModel, String programCode) {
        for (int i = 0; i < programModel.getRowCount(); i++) {
            if (programModel.getValueAt(i, 0).toString().trim().equals(programCode)) {
                return i;
            }
        }
        return -1;
    }

    private void openModifyDialog(GUI gui, String oldCode, String oldName, String oldCollegeCode, int rowIndex) {
        JDialog editDialog = new JDialog((JFrame) null, "Edit Program", true);
        editDialog.setSize(350, 280);
        editDialog.setLayout(null);
        editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel codeLabel = new JLabel("Program Code:");
        JTextField codeField = new JTextField(oldCode);
        JLabel nameLabel = new JLabel("Program Name:");
        JTextField nameField = new JTextField(oldName);
        JLabel collegeLabel = new JLabel("College Code:");

        JComboBox<String> collegeComboBox = new JComboBox<>(getCollegeCodes(gui));
        collegeComboBox.setSelectedItem(oldCollegeCode);

        codeLabel.setBounds(20, 20, 100, 25);
        codeField.setBounds(130, 20, 150, 25);
        nameLabel.setBounds(20, 60, 100, 25);
        nameField.setBounds(130, 60, 150, 25);
        collegeLabel.setBounds(20, 100, 100, 25);
        collegeComboBox.setBounds(130, 100, 150, 25);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(130, 160, 100, 30);

        editDialog.add(codeLabel);
        editDialog.add(codeField);
        editDialog.add(nameLabel);
        editDialog.add(nameField);
        editDialog.add(collegeLabel);
        editDialog.add(collegeComboBox);
        editDialog.add(updateButton);

        updateButton.addActionListener(ae -> {
            String newCode = codeField.getText().trim();
            String newName = nameField.getText().trim();
            String newCollegeCode = (String) collegeComboBox.getSelectedItem();

            
            if (!newCode.matches(".*[a-zA-Z].*")) {
                JOptionPane.showMessageDialog(editDialog, "Program Code must contain a value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newName.matches(".*[a-zA-Z].*")) {
                JOptionPane.showMessageDialog(editDialog, "Program Name must contain a value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (programExists(gui.getprogramModel(), newCode, newName, rowIndex)) {
                JOptionPane.showMessageDialog(editDialog, "A program with this code or name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                editDialog,
                "Are you sure you want to update this program?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
               
                DefaultTableModel model = gui.getprogramModel();
                model.setValueAt(newCode, rowIndex, 0);
                model.setValueAt(newName, rowIndex, 1);
                model.setValueAt(newCollegeCode, rowIndex, 2);

           
                updateProgramRecordInDatabase(oldCode, newCode, newName, newCollegeCode);
                updateStudentRecordsInDatabase(oldCode, newCode);

                
                updateStudentTableModel(gui, oldCode, newCode);

                JOptionPane.showMessageDialog(editDialog, "Program record updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                editDialog.dispose();
            }
        });

        editDialog.setLocationRelativeTo(null);
        editDialog.setResizable(false);
        editDialog.setVisible(true);
    }

    private String[] getCollegeCodes(GUI gui) {
        DefaultTableModel collegeModel = gui.getcollegeModel();
        List<String> collegeCodes = new ArrayList<>();
        for (int i = 0; i < collegeModel.getRowCount(); i++) {
            collegeCodes.add(collegeModel.getValueAt(i, 0).toString().trim());
        }
        return collegeCodes.toArray(new String[0]);
    }

    private boolean programExists(DefaultTableModel model, String code, String name, int ignoreRow) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i == ignoreRow) continue;
            String existingCode = model.getValueAt(i, 0).toString().trim();
            String existingName = model.getValueAt(i, 1).toString().trim();
            
            if (existingCode.equals(code) || existingName.equals(name)) {
                return true;
            }
        }
        return false;
    }

  
    private void updateProgramRecordInDatabase(String oldCode, String newCode, String newName, String newCollegeCode) {
        String sqlProgram = "UPDATE program SET ProgramCode = ?, ProgramName = ?, CollegeCode = ? WHERE ProgramCode = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sqlProgram)) {
             
            pstmt.setString(1, newCode);
            pstmt.setString(2, newName);
            pstmt.setString(3, newCollegeCode);
            pstmt.setString(4, oldCode);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Program record updated successfully in the database.");
            } else {
                System.out.println("No program record found for ProgramCode: " + oldCode);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating the program record in the database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

  
    private void updateStudentRecordsInDatabase(String oldCode, String newCode) {
        String sqlStudent = "UPDATE student SET ProgramCode = ? WHERE ProgramCode = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sqlStudent)) {
             
            pstmt.setString(1, newCode);
            pstmt.setString(2, oldCode);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student records updated with new ProgramCode in the database.");
            } else {
                System.out.println("No student records required updating for ProgramCode: " + oldCode);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating student records in the database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void updateStudentTableModel(GUI gui, String oldCode, String newCode) {
        DefaultTableModel studentModel = gui.getstudentModel();
        int programCodeColumn = 5;  // Adjust this index if your ProgramCode column is at a different index.
        for (int i = 0; i < studentModel.getRowCount(); i++) {
            String currentCode = studentModel.getValueAt(i, programCodeColumn).toString().trim();
            if (currentCode.equals(oldCode)) {
                studentModel.setValueAt(newCode, i, programCodeColumn);
            }
        }
        System.out.println("Student table model updated with new ProgramCode.");
    }
}
