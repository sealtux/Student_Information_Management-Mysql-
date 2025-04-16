package Essentials;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class create {

    String url = "jdbc:mysql://localhost:3306/mydata?useSSL=false";
    String user = "root";
    String password = "password";
    Connection conn = null;
    PreparedStatement pstmt = null;  
  
    public create() {
    
        
    }


public void addStudent(String id, String firstName, String lastName, String yearLevel, String gender, String programCode) {

        try {
           
            conn = DriverManager.getConnection(url, user, password);
            
            
            String sql = "INSERT INTO student (StudentID, FirstName, LastName, YearLevel, Gender, ProgramCode) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            
            
            pstmt.setString(1, id);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, yearLevel);
            pstmt.setString(5, gender);
            pstmt.setString(6, programCode);
            
          
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new student was inserted successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error writing to the student file.");
        } finally {
       
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void program( String programCode, String programName, String college) {
      
    

        try {
            conn = DriverManager.getConnection(url, user, password);
            String sql = "INSERT INTO program (ProgramCode,ProgramName,CollegeCode) Values(?,?,?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, programCode);
            pstmt.setString(2, programName);
            pstmt.setString(3, college);
            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("A new student was inserted successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error writing to the student file.");
        } finally {
       
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        
        } 
    
    }

public void college( String collegecode, String collegename){
    
    try{
        conn = DriverManager.getConnection(url, user, password);
        String sql = "INSERT INTO college (CollegeCode,CollegeName) Values(?,?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, collegecode);
        pstmt.setString(2,collegename);

        int rowsInserted = pstmt.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("A new college code was inserted successfully!");
        }
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error writing to the college code.");
    } finally {
   
        try {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    
    } 


}
}
