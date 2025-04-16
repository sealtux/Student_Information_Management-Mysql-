<<<<<<< HEAD
package Essentials;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class delete {
    // Database connection parameters
    private String url = "jdbc:mysql://localhost:3306/mydata";
    private String user = "root";
    private String password = "password";
    // DELETE query with one parameter for StudentID
    private String query = "DELETE FROM student WHERE StudentID = ?";

    public delete() {
        // Constructor - can be used for initializations if needed
    }

    /**
     * Removes a row from both the GUI table model and the MySQL database.
     * 
     * @param model       The DefaultTableModel instance representing the GUI table.
     * @param value       The StudentID value to delete.
     * @param columnIndex The column index in the table model where the StudentID is located.
     */
    public void removeRowByValue(DefaultTableModel model, String value, int columnIndex) {
        boolean found = false;
        value = value.trim();

        // Remove the row from the table model
        for (int i = model.getRowCount() - 1; i >= 0; i--) { 
            if (model.getValueAt(i, columnIndex).toString().trim().equals(value)) {
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

            // Use parameter index 1 for the StudentID (not columnIndex)
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

        // Notify the model that data has changed.
        model.fireTableDataChanged();
        System.out.println("Row successfully deleted and table model updated.");
    }
}
=======
package Essentials;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class delete {
    // Database connection parameters
    private String url = "jdbc:mysql://localhost:3306/mydata";
    private String user = "root";
    private String password = "password";
    // DELETE query with one parameter for StudentID
    private String query = "DELETE FROM student WHERE StudentID = ?";

    public delete() {
        // Constructor - can be used for initializations if needed
    }

    /**
     * Removes a row from both the GUI table model and the MySQL database.
     * 
     * @param model       The DefaultTableModel instance representing the GUI table.
     * @param value       The StudentID value to delete.
     * @param columnIndex The column index in the table model where the StudentID is located.
     */
    public void removeRowByValue(DefaultTableModel model, String value, int columnIndex) {
        boolean found = false;
        value = value.trim();

        // Remove the row from the table model
        for (int i = model.getRowCount() - 1; i >= 0; i--) { 
            if (model.getValueAt(i, columnIndex).toString().trim().equals(value)) {
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

            // Use parameter index 1 for the StudentID (not columnIndex)
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

        // Notify the model that data has changed.
        model.fireTableDataChanged();
        System.out.println("Row successfully deleted and table model updated.");
    }
}
>>>>>>> 5747080 (Implemented the Mysql)
