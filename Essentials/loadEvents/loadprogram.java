package Essentials.loadEvents;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

import Essentials.GUI;
import Essentials.create;

public class loadprogram {
    public loadprogram(GUI gui, create writer) {
        String url = "jdbc:mysql://localhost:3306/mydata?useSSL=false";
        String username = "root";
        String password = "password";

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM program";
            ResultSet rs = stmt.executeQuery(query);

            DefaultTableModel programModel = gui.getprogramModel();
            // Clear existing rows before loading new data
            programModel.setRowCount(0);

            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                String[] data = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    data[i - 1] = rs.getString(i);
                }
                programModel.addRow(data);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error loading data from MySQL database");
            e.printStackTrace();
        }
    }
}
