package Essentials.removeEvents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Essentials.delete;
import Essentials.GUI;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class removecollege {

    private GUI maingui;
    private delete de;
    private DefaultTableModel collegeModel;
    private String collegeCode;

   
    private String url = "jdbc:mysql://localhost:3306/mydata";
    private String user = "root";
    private String password = "password";

    public removecollege(GUI gui, delete de, String collegeCode) {
        this.maingui = gui;
        this.de = de;
        this.collegeCode = collegeCode;


        collegeModel = maingui.getcollegeModel();
        int selectedRow = findCollegeRow(collegeModel, collegeCode);
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "College not found in the table!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
      
        int confirmation = JOptionPane.showConfirmDialog(
            null,
            "Are you sure you want to delete college: " + collegeCode + "?\n"
          + "This will cascade delete all programs under this college and all students under those programs.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirmation == JOptionPane.YES_OPTION) {
          
            deleteCollegeFromGUI(selectedRow);
            
            cascadeDeleteCollegeFromDB();
        }
    }

    private int findCollegeRow(DefaultTableModel model, String collegeCode) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().trim().equals(collegeCode)) {
                return i;
            }
        }
        return -1;
    }


 
    private void deleteCollegeFromGUI(int collegeRowIndex) {
     
        List<String> programsToDelete = getProgramsByCollege(maingui, collegeCode);
        System.out.println("Programs to delete for college " + collegeCode + ": " + programsToDelete);
        
        
        removeStudentsByPrograms(maingui, de, programsToDelete);
      
        removeAllProgramsByCollege(maingui, programsToDelete, de);
       
        removeCollegeRecord(maingui, collegeCode, collegeRowIndex, de);
        
        JOptionPane.showMessageDialog(null, "College, its programs, and associated students deleted successfully from GUI.");
    }

   
    private void cascadeDeleteCollegeFromDB() {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

           
            List<String> programCodes = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT ProgramCode FROM program WHERE CollegeCode = ?")) {
                ps.setString(1, collegeCode);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        programCodes.add(rs.getString("ProgramCode"));
                    }
                }
            }
            
           
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM student WHERE ProgramCode = ?")) {
                for (String progCode : programCodes) {
                    ps.setString(1, progCode);
                    ps.executeUpdate();
                    System.out.println("Deleted students for program: " + progCode);
                }
            }
            
          
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM program WHERE CollegeCode = ?")) {
                ps.setString(1, collegeCode);
                int programsDeleted = ps.executeUpdate();
                System.out.println("Deleted " + programsDeleted + " program(s) under college " + collegeCode);
            }

            
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM college WHERE CollegeCode = ?")) {
                ps.setString(1, collegeCode);
                int collegesDeleted = ps.executeUpdate();
                if (collegesDeleted > 0) {
                    System.out.println("College " + collegeCode + " deleted successfully from database.");
                } else {
                    System.out.println("No college found with CollegeCode: " + collegeCode);
                }
            }
            
            conn.commit();
            JOptionPane.showMessageDialog(null, "Cascading deletion completed successfully in the database.");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting data from the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

  
    private List<String> getProgramsByCollege(GUI gui, String deletedCollege) {
        DefaultTableModel programModel = gui.getprogramModel();
        List<String> programs = new ArrayList<>();
        for (int i = 0; i < programModel.getRowCount(); i++) {
          
            String college = programModel.getValueAt(i, 2).toString().trim();
            if (college.equals(deletedCollege)) {
                String progCode = programModel.getValueAt(i, 0).toString().trim();
                programs.add(progCode);
                System.out.println("Found program " + progCode + " for college " + deletedCollege);
            }
        }
        return programs;
    }
        
   
    private void removeStudentsByPrograms(GUI gui, delete de, List<String> programsToDelete) {
        DefaultTableModel studentModel = gui.getstudentModel();
        if (studentModel == null) {
            System.err.println("Error: Student table model is null.");
            return;
        }
       
      
        int programColumnIndex = 5; 
        if (programColumnIndex >= studentModel.getColumnCount()) {
            System.err.println("Error: Invalid column index for program codes in student table.");
            return;
        }
        
        for (String programCode : programsToDelete) {
            System.out.println("Attempting to remove student rows with program code: " + programCode);
            for (int i = studentModel.getRowCount() - 1; i >= 0; i--) {
                String cellValue = studentModel.getValueAt(i, programColumnIndex).toString().trim();
                if (cellValue.equalsIgnoreCase(programCode)) {
                    System.out.println("Removing student row at index " + i + " with program code: " + cellValue);
                    studentModel.removeRow(i);
                }
            }
        }
        studentModel.fireTableDataChanged();
    }
    private void removeAllProgramsByCollege(GUI gui, List<String> programsToDelete, delete de) {
        DefaultTableModel programModel = gui.getprogramModel();
        List<String[]> updatedProgramData = new ArrayList<>();

        for (int i = 0; i < programModel.getRowCount(); i++) {
            String progCode = programModel.getValueAt(i, 0).toString().trim();
            if (!programsToDelete.contains(progCode)) {
                int columnCount = programModel.getColumnCount();
                String[] rowData = new String[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    rowData[j] = programModel.getValueAt(i, j).toString();
                }
                updatedProgramData.add(rowData);
            } else {
                System.out.println("Deleting program: " + progCode);
            }
        }
       
        refreshTable(programModel, updatedProgramData);
    }

 
    private void removeCollegeRecord(GUI gui, String collegeCode, int selectedRow, delete de) {
        DefaultTableModel collegeModel = gui.getcollegeModel();
        List<String[]> updatedCollegeData = new ArrayList<>();

        for (int i = 0; i < collegeModel.getRowCount(); i++) {
            if (i != selectedRow) { 
                int columnCount = collegeModel.getColumnCount();
                String[] rowData = new String[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    rowData[j] = collegeModel.getValueAt(i, j).toString();
                }
                updatedCollegeData.add(rowData);
            } else {
                System.out.println("Deleting college record at row: " + i);
            }
        }
      
        refreshTable(collegeModel, updatedCollegeData);
    }

    
    private void refreshTable(DefaultTableModel model, List<String[]> updatedData) {
        model.setRowCount(0);
        for (String[] row : updatedData) {
            model.addRow(row);
        }
        model.fireTableDataChanged();
    }
}
