package Essentials.addEvents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Essentials.GUI;
import Essentials.create;
import java.awt.Frame;

public class addcollegeGUI {
    private GUI mainGUI;

    public addcollegeGUI(GUI mainGUI, create writer) {
        this.mainGUI = mainGUI; 

        JDialog addCollegeDialog = new JDialog((Frame) null, "Add College", true);
        addCollegeDialog.setSize(350, 200);
        addCollegeDialog.setLayout(null);
        addCollegeDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JButton submit = new JButton("Submit");
        submit.setBounds(110, 120, 130, 25);

        JLabel collegecode = new JLabel("College Code:");
        JLabel collegename = new JLabel("College Name:");
        collegecode.setBounds(20, 20, 100, 25);
        collegename.setBounds(20, 70, 100, 25);

        JTextField collegecodetext = new JTextField();
        collegecodetext.setBounds(110, 20, 180, 25);

        JTextField collegenametext = new JTextField();
        collegenametext.setBounds(110, 70, 180, 25);

        addCollegeDialog.add(collegecode);
        addCollegeDialog.add(collegename);
        addCollegeDialog.add(collegecodetext);
        addCollegeDialog.add(collegenametext);
        addCollegeDialog.add(submit);

        submit.addActionListener(e -> {
            String collegeco = collegecodetext.getText().trim();
            String collegena = collegenametext.getText().trim();

            if (collegeco.isEmpty() || collegena.isEmpty()) {
                JOptionPane.showMessageDialog(addCollegeDialog, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DefaultTableModel model = mainGUI.getcollegeModel();
            boolean exists = false;
            boolean existscollegename = false;
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).toString().trim().equals(collegeco)) {
                    exists = true;
                    break;
                }
            }
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 1).toString().trim().equals(collegena)) {
                    existscollegename = true;
                    break;
                }
            }

            if (exists) {
                JOptionPane.showMessageDialog(addCollegeDialog, "Record with College Code " + collegeco + " already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                addCollegeDialog.dispose();
                return;
            }

            if (existscollegename) {
                JOptionPane.showMessageDialog(addCollegeDialog, "Record with College Name " + collegena + " already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                addCollegeDialog.dispose();
                return;
            }

            
            int decision = JOptionPane.showConfirmDialog(
                    addCollegeDialog,
                    "Are you sure you want to add this college?\nCode: " + collegeco + "\nName: " + collegena,
                    "Confirm Addition",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (decision == JOptionPane.YES_OPTION) {
               
                writer.college(collegeco, collegena);

               
                if (mainGUI != null) {
                    model.addRow(new Object[]{collegeco, collegena});
                } 

                JOptionPane.showMessageDialog(addCollegeDialog, "College added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                addCollegeDialog.dispose();
            }
            
        });

        addCollegeDialog.setLocationRelativeTo(null);
        addCollegeDialog.setResizable(false);
        addCollegeDialog.setVisible(true);
    }
}
