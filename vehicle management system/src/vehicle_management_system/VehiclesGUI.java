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
import java.sql.Statement;

public class VehiclesGUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField vehicleNameField, modelField, yearField, regNoField, dailyRentalRateField;
    private JComboBox<String> typeComboBox, availabilityStatusComboBox;
    
    public double getDailyRate(int vehicleId) {
        double dailyRate = 0.0;
        String query = "SELECT Daily_rental_rate FROM Vehicle WHERE Vehicle_id = ?";
        
        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setInt(1, vehicleId);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                dailyRate = resultSet.getDouble("Daily_rental_rate");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving daily rental rate.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return dailyRate;
    }

    public VehiclesGUI() {
        setTitle("Vehicles Form");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToMainMenu(); // Navigate back to the main menu
            }
        });

        // Table model with column names
        String[] columnNames = {"Vehicle_id", "Type", "Vehicle_name", "Model", "Year", "Reg_no", "Daily_rental_rate", "Availability_status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for form inputs
        JPanel inputPanel = new JPanel(new GridLayout(8, 2));

        inputPanel.add(new JLabel("Type:"));
        typeComboBox = new JComboBox<>(new String[]{"Two wheeler", "Three wheeler", "Four wheeler"});
        inputPanel.add(typeComboBox);

        inputPanel.add(new JLabel("Vehicle Name:"));
        vehicleNameField = new JTextField();
        inputPanel.add(vehicleNameField);

        inputPanel.add(new JLabel("Model:"));
        modelField = new JTextField();
        inputPanel.add(modelField);

        inputPanel.add(new JLabel("Year:"));
        yearField = new JTextField();
        inputPanel.add(yearField);

        inputPanel.add(new JLabel("Reg no:"));
        regNoField = new JTextField();
        inputPanel.add(regNoField);

        inputPanel.add(new JLabel("Daily rental rate (/day):"));
        dailyRentalRateField = new JTextField();
        inputPanel.add(dailyRentalRateField);

        inputPanel.add(new JLabel("Availability status:"));
        availabilityStatusComboBox = new JComboBox<>(new String[]{"Available", "Not Available"});
        inputPanel.add(availabilityStatusComboBox);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Vehicle");
        JButton deleteButton = new JButton("Delete Vehicle");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        // Add input panel and button panel to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data from database
        loadDataFromDatabase();

        // Add button action listener
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addVehicleToDatabase();
            }
        });

        // Delete button action listener
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteVehicleFromDatabase();
            }
        });
    }

    private void goBackToMainMenu() {
        dispose(); // Close the current window
        showMainMenu(); // Show main menu
    }

    private void showMainMenu() {
        VehicleManagementSystemGUI systemGUI = new VehicleManagementSystemGUI();
        systemGUI.setVisible(true);
    }

    private void loadDataFromDatabase() {
        try (Connection connection = DBConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Vehicle")) {

            // Clear existing data
            tableModel.setRowCount(0);

            // Load data from ResultSet into table model
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("Vehicle_id"),
                        resultSet.getString("Type"),
                        resultSet.getString("Vehicle_name"),
                        resultSet.getString("Model"),
                        resultSet.getInt("Year"),
                        resultSet.getString("Reg_no"),
                        resultSet.getDouble("Daily_rental_rate"),
                        resultSet.getString("Availability_status")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data from database.");
        }
    }

    private void addVehicleToDatabase() {
        String query = "INSERT INTO Vehicle (Type, Vehicle_name, Model, Year, Reg_no, Daily_rental_rate, Availability_status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Validate input fields
            if (yearField.getText().trim().isEmpty() ||
                    dailyRentalRateField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
                return;
            }

            preparedStatement.setString(1, (String) typeComboBox.getSelectedItem());
            preparedStatement.setString(2, vehicleNameField.getText());
            preparedStatement.setString(3, modelField.getText());
            preparedStatement.setInt(4, Integer.parseInt(yearField.getText()));
            preparedStatement.setString(5, regNoField.getText());
            preparedStatement.setDouble(6, Double.parseDouble(dailyRentalRateField.getText()));
            preparedStatement.setString(7, (String) availabilityStatusComboBox.getSelectedItem());

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Vehicle added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Clear input fields
                typeComboBox.setSelectedIndex(0);
                vehicleNameField.setText("");
                modelField.setText("");
                yearField.setText("");
                regNoField.setText("");
                dailyRentalRateField.setText("");

                // Reload data
                loadDataFromDatabase();
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding vehicle to database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteVehicleFromDatabase() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int vehicleId = (int) tableModel.getValueAt(selectedRow, 0);
            String query = "DELETE FROM Vehicle WHERE Vehicle_id = ?";

            try (Connection connection = DBConnector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, vehicleId);
                int rowsDeleted = preparedStatement.executeUpdate();

                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Vehicle deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Reload data
                    loadDataFromDatabase();
                } else {
                    JOptionPane.showMessageDialog(this, "Vehicle not found or delete failed", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting vehicle from database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VehiclesGUI().setVisible(true);
            }
        });
    }
}
