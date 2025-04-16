package Essentials.removeEvents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Essentials.delete;
import Essentials.GUI;
import java.sql.*;

public class removeprogram {
    private GUI maingui;
    private delete de;
    private DefaultTableModel programModel;
    private DefaultTableModel studentTableModel;
    private String programCode;

    private String url = "jdbc:mysql://localhost:3306/mydata";
    private String user = "root";
    private String password = "password";
    private String query = "DELETE FROM program WHERE ProgramCode = ?";
    private String query2 = "DELETE FROM student WHERE ProgramCode = ?";

    public removeprogram(GUI gui, delete de, String programCode) {
        // Set dependencies and store the program code to delete
        setDependencies(gui, de);
        this.programCode = programCode;

        // Confirm deletion with the user
        int confirmation = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to delete program code: " + programCode + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            deleteProgram();
            try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(query); 
            PreparedStatement stmt2 = conn.prepareStatement(query2)){

           // Use parameter index 1 for the StudentID (not columnIndex)
           stmt.setString(1, programCode);
           stmt2.setString(1, programCode);

            
           int rowsAffected1 = stmt.executeUpdate();
           int rowsAffected2 = stmt2.executeUpdate();

           if (rowsAffected1 > 0) {
            System.out.println("Program row deleted successfully from database.");
        } else {
            System.out.println("No row found in program table with ProgramCode: " + programCode);
        }

        if (rowsAffected2 > 0) {
            System.out.println("Student row(s) deleted successfully from database.");
        } else {
            System.out.println("No student row found with ProgramCode: " + programCode);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error deleting data from the database.");
    }
}
    }

    private void setDependencies(GUI gui, delete de) {
        this.maingui = gui;
        this.de = de;
    }

    private void deleteProgram() {
        // Remove matching record from the student table
        // (Assuming column index 5 in the student table holds program codes.)
        studentTableModel = maingui.getstudentModel();
        final int studentColumnIndex = 5;
        if (studentTableModel == null) {
            System.err.println("Error: Student table model is null.");
        } else {

           de.removeRowByValue(studentTableModel, programCode, studentColumnIndex);
        }
        
        // Remove matching record from the program table
        // (Assuming column index 0 in the program table holds program codes.)
        programModel = maingui.getprogramModel();
        if (programModel == null) {
            System.err.println("Error: Program table model is null.");
        } else {
            de.removeRowByValue(programModel, programCode, 0);
        }

        JOptionPane.showMessageDialog(null, "Program deleted successfully.");
    }
}
