package vehicle_management_system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MaintenanceGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> vehicleIdComboBox;
    private JTextField vehicleNameField;
    private JTextField serviceDateField;
    private JTextField descriptionField;
    private JTextField costField;
    private JTextField nextScheduleField;
    private JButton submitButton, backButton;
    private JTable maintenanceTable;
    private DefaultTableModel tableModel;
    private Map<String, String> vehicleDetailsMap; // Map to store vehicle details

    public MaintenanceGUI() {
        vehicleDetailsMap = new HashMap<>(); // Initialize the map
        createForm();
        populateVehicleIdComboBox();
        fetchMaintenance();
    }

    private void createForm() {
        setTitle("Maintenance Form");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center the frame on the screen

        JPanel formPanel = new JPanel(new GridLayout(6, 2));

        // Labels and text fields
        formPanel.add(new JLabel("Vehicle ID:"));
        vehicleIdComboBox = new JComboBox<>();
        vehicleIdComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When a vehicle ID is selected, update the vehicle name field
                String selectedVehicleId = (String) vehicleIdComboBox.getSelectedItem();
                if (selectedVehicleId != null && !selectedVehicleId.isEmpty()) {
                    String vehicleName = vehicleDetailsMap.get(selectedVehicleId);
                    vehicleNameField.setText(vehicleName);
                }
            }
        });
        formPanel.add(vehicleIdComboBox);

        formPanel.add(new JLabel("Vehicle Name:"));
        vehicleNameField = new JTextField();
        vehicleNameField.setEditable(false); // Make it non-editable
        formPanel.add(vehicleNameField);

        formPanel.add(new JLabel("Service Date (YYYY-MM-DD):"));
        serviceDateField = new JTextField(LocalDate.now().toString()); // Set the current date
        formPanel.add(serviceDateField);

        formPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        formPanel.add(descriptionField);

        formPanel.add(new JLabel("Cost:"));
        costField = new JTextField();
        formPanel.add(costField);

        formPanel.add(new JLabel("Next Schedule (YYYY-MM-DD):"));
        nextScheduleField = new JTextField();
        formPanel.add(nextScheduleField);

        // Add form panel to the top
        add(formPanel, BorderLayout.NORTH);

        // Submit and back buttons
        submitButton = new JButton("Submit");
        backButton = new JButton("Back to Main Menu");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitMaintenance();
                fetchMaintenance();
                clearFields();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToMainMenu(); // Navigate back to the main menu
            }
        });

        // Add buttons to the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Maintenance table
        tableModel = new DefaultTableModel(new String[]{"Maintenance ID", "Vehicle ID", "Service Date", "Description", "Cost", "Next Schedule"}, 0);
        maintenanceTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(maintenanceTable);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void submitMaintenance() {
        String vehicleId = (String) vehicleIdComboBox.getSelectedItem();
        String serviceDate = serviceDateField.getText();
        String description = descriptionField.getText();
        String cost = costField.getText();
        String nextSchedule = nextScheduleField.getText();

        // Insert maintenance record into database
        try (Connection connection = DBConnector.getConnection()) {
            String query = "INSERT INTO MAINTENANCE (Vehicle_id, Service_date, Description, Cost, Next_schedule) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, vehicleId);
            statement.setString(2, serviceDate);
            statement.setString(3, description);
            statement.setString(4, cost);
            statement.setString(5, nextSchedule);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Maintenance record successfully added.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error inserting maintenance record into database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        vehicleIdComboBox.setSelectedIndex(0);
        vehicleNameField.setText("");
        serviceDateField.setText(LocalDate.now().toString()); // Reset to current date
        descriptionField.setText("");
        costField.setText("");
        nextScheduleField.setText("");
    }

    private void populateVehicleIdComboBox() {
        try (Connection connection = DBConnector.getConnection()) {
            String query = "SELECT Vehicle_id, Vehicle_name FROM vehicle";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String vehicleId = resultSet.getString("Vehicle_id");
                String vehicleName = resultSet.getString("Vehicle_name");
                vehicleIdComboBox.addItem(vehicleId);
                vehicleDetailsMap.put(vehicleId, vehicleName); // Store in map
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching vehicle IDs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchMaintenance() {
        try (Connection connection = DBConnector.getConnection()) {
            String query = "SELECT Maintenance_id, Vehicle_id, Service_date, Description, Cost, Next_schedule FROM maintenance";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            tableModel.setRowCount(0); // Clear existing data
            while (resultSet.next()) {
                String maintenanceId = resultSet.getString("Maintenance_id");
                String vehicleId = resultSet.getString("Vehicle_id");
                String serviceDate = resultSet.getString("Service_date");
                String description = resultSet.getString("Description");
                String cost = resultSet.getString("Cost");
                String nextSchedule = resultSet.getString("Next_schedule");
                tableModel.addRow(new Object[]{maintenanceId, vehicleId, serviceDate, description, cost, nextSchedule});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching maintenance records.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBackToMainMenu() {
        dispose(); // Close the current window
        showMainMenu(); // Show main menu
    }

    private void showMainMenu() {
        VehicleManagementSystemGUI systemGUI = new VehicleManagementSystemGUI();
        systemGUI.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MaintenanceGUI());
    }
}
