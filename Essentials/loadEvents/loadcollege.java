package Essentials.loadEvents;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

import Essentials.GUI;
import Essentials.create;

public class loadcollege {
    public loadcollege(GUI gui, create write) {
      
        String url = "jdbc:mysql://localhost:3306/mydata?useSSL=false"; 
        String username = "root";
        String password = "password"; 
     

        try {
            
            Connection conn = DriverManager.getConnection(url,username,password);
            
           
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM college"; 
            ResultSet rs = stmt.executeQuery(query);
            
          
            DefaultTableModel collegemodel = gui.getcollegeModel();
            
           
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                
                String[] data = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    data[i - 1] = rs.getString(i);
                }
                
                collegemodel.addRow(data);
            }
            
           
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading data from MySQL database");
        }
    }
}
