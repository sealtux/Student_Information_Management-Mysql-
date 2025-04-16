package Essentials.loadEvents;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

import Essentials.create;
import Essentials.GUI;

public class loadstudent {
   public loadstudent(GUI gui, create write){
   String url = "jdbc:mysql://localhost:3306/mydata?useSSL=false"; 
   String username = "root";
   String password = "password";

    try {
            
        Connection conn = DriverManager.getConnection(url, username, password);

        Statement stm = conn.createStatement();
        String query = "SELECT * FROM student";
        ResultSet rs = stm.executeQuery(query);
        
        DefaultTableModel model = gui.getstudentModel();
        int columnCount = rs.getMetaData().getColumnCount();
        while (rs.next()) {
                String[] data = new String[columnCount];

                for(int i = 1; i<=columnCount;i++){
                data[i-1] = rs.getString(i);
                }
                model.addRow(data);
            }
        } catch (Exception e) {
            System.out.println("No database were found");

        }
    }
}
