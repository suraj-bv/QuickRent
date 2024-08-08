package vehicle_management_system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ReservationsGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> customerIdComboBox;
    private JComboBox<String> vehicleIdComboBox;
    private JTextField reservationChargeField;
    private JTextField reservationStartDateField;
    private JTextField reservationEndDateField;
    private JComboBox<String> reservationStatusComboBox;
    private JTextField customerNameField;
    private JTextField vehicleNameField;
    private JButton submitButton, backButton;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;
    private Map<String, String> customerDetailsMap = new HashMap<>();
    private Map<String, String> vehicleDetailsMap = new HashMap<>();

    public ReservationsGUI() {
        createForm();
        populateCustomerIdComboBox();
        populateVehicleIdComboBox();
        fetchReservations();
    }

    private void createForm() {
        setTitle("Reservation Form");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center the frame on the screen

        JPanel formPanel = new JPanel(new GridLayout(8, 2));

        // Labels and text fields
        formPanel.add(new JLabel("Customer ID:"));
        customerIdComboBox = new JComboBox<>();
        customerIdComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String customerId = (String) customerIdComboBox.getSelectedItem();
                    customerNameField.setText(customerDetailsMap.get(customerId));
                }
            }
        });
        formPanel.add(customerIdComboBox);

        formPanel.add(new JLabel("Customer Name:"));
        customerNameField = new JTextField();
        customerNameField.setEditable(false);
        formPanel.add(customerNameField);

        formPanel.add(new JLabel("Vehicle ID:"));
        vehicleIdComboBox = new JComboBox<>();
        vehicleIdComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String vehicleId = (String) vehicleIdComboBox.getSelectedItem();
                    vehicleNameField.setText(vehicleDetailsMap.get(vehicleId));
                }
            }
        });
        formPanel.add(vehicleIdComboBox);

        formPanel.add(new JLabel("Vehicle Name:"));
        vehicleNameField = new JTextField();
        vehicleNameField.setEditable(false);
        formPanel.add(vehicleNameField);

        formPanel.add(new JLabel("Reservation Charge:"));
        reservationChargeField = new JTextField();
        formPanel.add(reservationChargeField);

        formPanel.add(new JLabel("Reservation Start Date (YYYY-MM-DD):"));
        reservationStartDateField = new JTextField();
        formPanel.add(reservationStartDateField);

        formPanel.add(new JLabel("Reservation End Date (YYYY-MM-DD):"));
        reservationEndDateField = new JTextField();
        formPanel.add(reservationEndDateField);

        formPanel.add(new JLabel("Reservation Status:"));
        reservationStatusComboBox = new JComboBox<>(new String[]{"Active", "Completed", "Cancelled"});
        formPanel.add(reservationStatusComboBox);

        // Add form panel to the top
        add(formPanel, BorderLayout.NORTH);

        // Submit and back buttons
        submitButton = new JButton("Submit");
        backButton = new JButton("Back to Main Menu");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitReservation();
                fetchReservations();
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

        // Reservations table
        tableModel = new DefaultTableModel(new String[]{"Reservation ID", "Customer ID", "Vehicle ID", "Reservation Charge", "Start Date", "End Date", "Status"}, 0);
        reservationsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void submitReservation() {
        String customerId = (String) customerIdComboBox.getSelectedItem();
        String vehicleId = (String) vehicleIdComboBox.getSelectedItem();
        String reservationCharge = reservationChargeField.getText();
        String startDate = reservationStartDateField.getText();
        String endDate = reservationEndDateField.getText();
        String status = (String) reservationStatusComboBox.getSelectedItem();

        // Insert reservation into database
        try (Connection connection = DBConnector.getConnection()) {
            String query = "INSERT INTO RESERVATIONS (Customer_id, Vehicle_id, Reservation_charge, Reservation_start_date, Reservation_end_date, Reservation_status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, customerId);
            statement.setString(2, vehicleId);
            statement.setString(3, reservationCharge);
            statement.setString(4, startDate);
            statement.setString(5, endDate);
            statement.setString(6, status);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Reservation successfully added.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error inserting reservation into database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        customerIdComboBox.setSelectedIndex(0);
        vehicleIdComboBox.setSelectedIndex(0);
        customerNameField.setText("");
        vehicleNameField.setText("");
        reservationChargeField.setText("");
        reservationStartDateField.setText("");
        reservationEndDateField.setText("");
        reservationStatusComboBox.setSelectedIndex(0);
    }

    private void populateCustomerIdComboBox() {
        try (Connection connection = DBConnector.getConnection()) {
            String query = "SELECT Customer_id, Name FROM customer";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String customerId = resultSet.getString("Customer_id");
                String customerName = resultSet.getString("Name");
                customerDetailsMap.put(customerId, customerName);
                customerIdComboBox.addItem(customerId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching customer IDs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateVehicleIdComboBox() {
        try (Connection connection = DBConnector.getConnection()) {
            String query = "SELECT Vehicle_id, Vehicle_name FROM vehicle";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String vehicleId = resultSet.getString("Vehicle_id");
                String vehicleName = resultSet.getString("Vehicle_name");
                vehicleDetailsMap.put(vehicleId, vehicleName);
                vehicleIdComboBox.addItem(vehicleId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching vehicle IDs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchReservations() {
        try (Connection connection = DBConnector.getConnection()) {
            String query = "SELECT Reservation_id, Customer_id, Vehicle_id, Reservation_charge, Reservation_start_date, Reservation_end_date, Reservation_status FROM reservations";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            tableModel.setRowCount(0); // Clear existing data
            while (resultSet.next()) {
                String reservationId = resultSet.getString("Reservation_id");
                String customerId = resultSet.getString("Customer_id");
                String vehicleId = resultSet.getString("Vehicle_id");
                String reservationCharge = resultSet.getString("Reservation_charge");
                String startDate = resultSet.getString("Reservation_start_date");
                String endDate = resultSet.getString("Reservation_end_date");
                String status = resultSet.getString("Reservation_status");
                tableModel.addRow(new Object[]{reservationId, customerId, vehicleId, reservationCharge, startDate, endDate, status});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching reservations.", "Error", JOptionPane.ERROR_MESSAGE);
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
        SwingUtilities.invokeLater(() -> new ReservationsGUI());
    }
}
