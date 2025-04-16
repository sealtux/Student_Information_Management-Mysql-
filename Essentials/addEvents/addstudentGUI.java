package Essentials.addEvents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Essentials.GUI;
import Essentials.create;

public class addstudentGUI {
    private GUI mainGUI;  
    public static final String FILE_PATH = "C:\\Users\\Admin\\Desktop\\ccc151\\students.csv";

    public addstudentGUI(GUI gui, create writer) {
        this.mainGUI = gui;  

        JDialog addStudentDialog = new JDialog();
        addStudentDialog.setTitle("Add Student");
        addStudentDialog.setModal(true);
        addStudentDialog.setSize(350, 350);
        addStudentDialog.setLayout(null);
        addStudentDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField();
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField();
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField();
        JLabel yearLevelLabel = new JLabel("Year Level:");
        String[] yearModel = {"", "1st year", "2nd year", "3rd year", "4th year"};
        JComboBox<String> yearLevelCombo = new JComboBox<>(yearModel);
        JLabel genderLabel = new JLabel("Gender:");
        String[] genderOptions = {"", "M", "F"};
        JComboBox<String> genderCombo = new JComboBox<>(genderOptions);
        JLabel programCodeLabel = new JLabel("Program Code:");

        DefaultTableModel programModel = mainGUI.getprogramModel();
        JComboBox<String> programCodeCombo = new JComboBox<>();
        programCodeCombo.addItem(""); 
        for (int i = 0; i < programModel.getRowCount(); i++) {
            programCodeCombo.addItem(programModel.getValueAt(i, 0).toString().trim());
        }

        idLabel.setBounds(20, 20, 65, 25);
        idField.setBounds(90, 20, 200, 25);
        firstNameLabel.setBounds(20, 60, 65, 25);
        firstNameField.setBounds(90, 60, 200, 25);
        lastNameLabel.setBounds(20, 100, 65, 25);
        lastNameField.setBounds(90, 100, 200, 25);
        yearLevelLabel.setBounds(20, 140, 65, 25);
        yearLevelCombo.setBounds(90, 140, 200, 25);
        genderLabel.setBounds(20, 180, 65, 25);
        genderCombo.setBounds(90, 180, 200, 25);
        programCodeLabel.setBounds(20, 220, 100, 25);
        programCodeCombo.setBounds(120, 220, 170, 25);

        JButton submitButton = new JButton("Add Student");
        submitButton.setBounds(110, 260, 130, 30);

        addStudentDialog.add(idLabel);
        addStudentDialog.add(idField);
        addStudentDialog.add(firstNameLabel);
        addStudentDialog.add(firstNameField);
        addStudentDialog.add(lastNameLabel);
        addStudentDialog.add(lastNameField);
        addStudentDialog.add(yearLevelLabel);
        addStudentDialog.add(yearLevelCombo);
        addStudentDialog.add(genderLabel);
        addStudentDialog.add(genderCombo);
        addStudentDialog.add(programCodeLabel);
        addStudentDialog.add(programCodeCombo);
        addStudentDialog.add(submitButton);

        addStudentDialog.setLocationRelativeTo(null);
        addStudentDialog.setResizable(false);

        submitButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String yearLevel = (String) yearLevelCombo.getSelectedItem();
                String selectedGender = (String) genderCombo.getSelectedItem();
                String programCode = (String) programCodeCombo.getSelectedItem();

                if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
                    yearLevel.isEmpty() || selectedGender.isEmpty() || programCode.isEmpty()) {
                    JOptionPane.showMessageDialog(addStudentDialog, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!id.matches("\\d{4}-\\d{4}")) {
                    JOptionPane.showMessageDialog(addStudentDialog, "Invalid ID format! Use YYYY-NNNN.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] parts = id.split("-");

                int year = Integer.parseInt(parts[0]);

                if (year < 2000 || parts[1].equals("0000")) {
                    JOptionPane.showMessageDialog(addStudentDialog, "Invalid ID! Year must be 2000 or greater and the numeric part cannot be 0000.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DefaultTableModel studentModel = mainGUI.getstudentModel();
                
                for (int i = 0; i < studentModel.getRowCount(); i++) {
                    if (studentModel.getValueAt(i, 0).toString().trim().equals(id)) {
                        JOptionPane.showMessageDialog(addStudentDialog, "Student ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                for (int i = 0; i < studentModel.getRowCount(); i++) {
                    if (studentModel.getValueAt(i, 1).toString().trim().equalsIgnoreCase(firstName) &&
                        studentModel.getValueAt(i, 2).toString().trim().equalsIgnoreCase(lastName)) {
                        JOptionPane.showMessageDialog(addStudentDialog, "Student already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                
                int decision = JOptionPane.showConfirmDialog(
                        addStudentDialog,
                        "Are you sure you want to add the following student?\n" +
                        "ID: " + id + "\n" +
                        "Name: " + firstName + " " + lastName + "\n" +
                        "Year Level: " + yearLevel + "\n" +
                        "Gender: " + selectedGender + "\n" +
                        "Program Code: " + programCode,
                        "Confirm Addition",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (decision == JOptionPane.YES_OPTION) {
                   
                    writer.addStudent(id, firstName, lastName, yearLevel, selectedGender, programCode);
                    
                  
                    studentModel.addRow(new Object[]{id, firstName, lastName, yearLevel, selectedGender, programCode});

                    JOptionPane.showMessageDialog(addStudentDialog, "Student added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    addStudentDialog.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addStudentDialog, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addStudentDialog.setVisible(true);
    }
}
