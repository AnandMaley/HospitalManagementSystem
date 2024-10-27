import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HospitalInformationSystem extends JFrame {
    private JTextField nameField, ageField, genderField, historyField;
    private JButton addPatientButton, viewPatientsButton;

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost/hospital";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "maley@03";

    public HospitalInformationSystem() {
        setTitle("Hospital Information System");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        add(panel);
        placeComponents(panel, gbc);

        setVisible(true);
    }

    private void placeComponents(JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Age:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Medical History:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);
        gbc.gridy++;
        ageField = new JTextField(20);
        panel.add(ageField, gbc);
        gbc.gridy++;
        genderField = new JTextField(20);
        panel.add(genderField, gbc);
        gbc.gridy++;
        historyField = new JTextField(20);
        panel.add(historyField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        panel.add(new JLabel(" "), gbc); // spacing

        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        addPatientButton = new JButton("Add Patient");
        panel.add(addPatientButton, gbc);

        gbc.gridy++;
        panel.add(new JLabel(" "), gbc); // spacing

        gbc.gridy++;
        viewPatientsButton = new JButton("View Patients");
        panel.add(viewPatientsButton, gbc);

        addPatientButton.addActionListener(e -> addPatient());
        viewPatientsButton.addActionListener(e -> viewPatients());
    }

    private void addPatient() {
        String name = nameField.getText();
        String ageText = ageField.getText();
        String gender = genderField.getText();
        String medicalHistory = historyField.getText();

        if (name.isEmpty() || ageText.isEmpty() || gender.isEmpty() || medicalHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            int age = Integer.parseInt(ageText);

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO patients (name, age, gender, medical_history) VALUES (?, ?, ?, ?)")) {

                statement.setString(1, name);
                statement.setInt(2, age);
                statement.setString(3, gender);
                statement.setString(4, medicalHistory);

                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Patient added successfully!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding patient: " + ex.getMessage());
        }
    }

    private void viewPatients() {
        StringBuilder patientsInfo = new StringBuilder("Patients:\n");

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM patients")) {

            while (resultSet.next()) {
                patientsInfo.append("ID: ").append(resultSet.getInt("id"))
                        .append(", Name: ").append(resultSet.getString("name"))
                        .append(", Age: ").append(resultSet.getInt("age"))
                        .append(", Gender: ").append(resultSet.getString("gender"))
                        .append(", Medical History: ").append(resultSet.getString("medical_history"))
                        .append("\n");
            }

            JTextArea textArea = new JTextArea(patientsInfo.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "Patient Records", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error viewing patients: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalInformationSystem::new);
    }
}
