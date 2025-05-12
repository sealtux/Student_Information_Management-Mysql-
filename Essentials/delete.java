package Essentials;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class delete {
    
    private String url = "jdbc:mysql://localhost:3306/mydata";
    private String user = "root";
    private String password = "password";
    
    private String query = "DELETE FROM student WHERE StudentID = ?";

    public delete() {
       
    }

    public void removeRowByValue(DefaultTableModel model, String value, int columnIndex) {
        boolean found = false;
        value = value.trim();

        // Remove the row from the table model safely checking for nulls
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            Object cellValue = model.getValueAt(i, columnIndex);
            if (cellValue == null) {
                // Debugging: log null cell values
                System.out.println("Null value found at row " + i + ", column " + columnIndex);
                continue;
            }
            if (cellValue.toString().trim().equals(value)) {
                model.removeRow(i);
                found = true;
            }
        }

        if (!found) {
            System.out.println("Value not found in table: " + value);
            return;
        }

        // Now delete the row from the MySQL database
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, value);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Row deleted successfully from database.");
            } else {
                System.out.println("No row found with ID: " + value + " in database.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.fireTableDataChanged();
        System.out.println("Row successfully deleted and table model updated.");
    }
}
