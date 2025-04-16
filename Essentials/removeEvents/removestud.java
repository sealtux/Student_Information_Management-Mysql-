package Essentials.removeEvents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Essentials.delete;
import Essentials.GUI;
import java.awt.Frame;
import java.sql.*;

public class removestud {
    
    GUI maingui;



    public removestud(GUI gui, delete de, String id) {
        this.maingui = gui;
        JTable studentTable = maingui.getStudentTable(); 

        JDialog deleteStudDialog = new JDialog((Frame) null, "Delete Student", true);
        deleteStudDialog.setLayout(null);   
        deleteStudDialog.setSize(300, 180);
        deleteStudDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JLabel infoLabel = new JLabel(" you want to delete this student?");
        JButton deleteButton = new JButton("Delete");

        infoLabel.setBounds(50, 30, 200, 25);
        deleteButton.setBounds(85, 80, 130, 25);

        deleteStudDialog.add(infoLabel);
        deleteStudDialog.add(deleteButton);
        deleteStudDialog.setLocationRelativeTo(null);
        deleteStudDialog.setResizable(false);

        deleteButton.addActionListener(e -> {
            DefaultTableModel model = maingui.getstudentModel();

            int confirmation = JOptionPane.showConfirmDialog(
                    deleteStudDialog,
                    "Are you sure you want to delete student ID: " + id + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmation == JOptionPane.YES_OPTION) {

                
              de.removeRowByValue(model, id, 0);
                
                deleteStudDialog.dispose();
            }
        });

        deleteStudDialog.setVisible(true);
    }
}
