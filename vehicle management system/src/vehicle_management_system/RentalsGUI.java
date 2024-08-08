package vehicle_management_system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RentalsGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel tableModel;
    private Map<Integer, String> customersMap;
    private Map<Integer, String> vehiclesMap;
    private VehiclesGUI vehicleManagementGUI;

    private JComboBox<String> customerComboBox;
    private JComboBox<String> vehicleComboBox;
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<String> statusComboBox;
    private JButton addButton;
    private JButton deleteButton;
    private JButton backButton;

    private void goBackToMainMenu() {
        dispose();
        showMainMenu();
    }

    private void showMainMenu() {
        VehicleManagementSystemGUI systemGUI = new VehicleManagementSystemGUI();
        systemGUI.setVisible(true);
    }

    public RentalsGUI() {
        setTitle("Rentals Form");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        customersMap = new HashMap<>();
        vehiclesMap = new HashMap<>();
        vehicleManagementGUI = new VehiclesGUI();

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Rental_id");
        tableModel.addColumn("Customer_name");
        tableModel.addColumn("Vehicle_name");
        tableModel.addColumn("Rental_start_date");
        tableModel.addColumn("Rental_end_date");
        tableModel.addColumn("Total_cost");
        tableModel.addColumn("Status");

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));

        customerComboBox = new JComboBox<>();
        vehicleComboBox = new JComboBox<>();
        startDateField = new JTextField();
        endDateField = new JTextField();
        statusComboBox = new JComboBox<>(new String[]{"Booked", "Waiting"});

        // Set current date as default in startDateField
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());
        startDateField.setText(currentDate);

        inputPanel.add(new JLabel("Customer:"));
        inputPanel.add(customerComboBox);
        inputPanel.add(new JLabel("Vehicle:"));
        inputPanel.add(vehicleComboBox);
        inputPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        inputPanel.add(startDateField);
        inputPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        inputPanel.add(endDateField);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusComboBox);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Rental");
        deleteButton = new JButton("Delete Rental");
        backButton = new JButton("Back to Main Menu");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadDataFromDatabase();
        populateInputFields();

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRental();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRental();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToMainMenu();
            }
        });
    }

    private void loadDataFromDatabase() {
        loadCustomersMap();
        loadVehiclesMap();

        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rentals")) {

            while (rs.next()) {
                int rentalId = rs.getInt("Rental_id");
                int customerId = rs.getInt("Customer_id");
                int vehicleId = rs.getInt("Vehicle_id");
                Date rentalStart = rs.getDate("Rental_start_date");
                Date rentalEnd = rs.getDate("Rental_end_date");
                double totalCost = rs.getDouble("Total_cost");
                String status = rs.getString("Status");

                String customerName = customersMap.get(customerId);
                String vehicleName = vehiclesMap.get(vehicleId);

                tableModel.addRow(new Object[]{rentalId, customerName, vehicleName, rentalStart, rentalEnd, totalCost, status});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data from database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCustomersMap() {
        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Customer_id, Name FROM customer")) {

            while (rs.next()) {
                int customerId = rs.getInt("Customer_id");
                String customerName = rs.getString("Name");
                customersMap.put(customerId, customerName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customers data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadVehiclesMap() {
        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Vehicle_id, Vehicle_name FROM vehicle")) {

            while (rs.next()) {
                int vehicleId = rs.getInt("Vehicle_id");
                String vehicleName = rs.getString("Vehicle_name");
                vehiclesMap.put(vehicleId, vehicleName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading vehicles data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateInputFields() {
        for (String customerName : customersMap.values()) {
            customerComboBox.addItem(customerName);
        }

        for (String vehicleName : vehiclesMap.values()) {
            vehicleComboBox.addItem(vehicleName);
        }
    }

    private void addRental() {
        try {
            String customerName = (String) customerComboBox.getSelectedItem();
            String vehicleName = (String) vehicleComboBox.getSelectedItem();
            String startDateStr = startDateField.getText();
            String endDateStr = endDateField.getText();
            String status = (String) statusComboBox.getSelectedItem();

            if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Start Date and End Date cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int customerId = getKeyByValue(customersMap, customerName);
            int vehicleId = getKeyByValue(vehiclesMap, vehicleName);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
            long diff = diffInMillies / (24 * 60 * 60 * 1000);

            double dailyRate = vehicleManagementGUI.getDailyRate(vehicleId);
            double totalCost = diff * dailyRate;

            try (Connection conn = DBConnector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO rentals (Customer_id, Vehicle_id, Rental_start_date, Rental_end_date, Total_cost, Status) VALUES (?, ?, ?, ?, ?, ?)")) {

                pstmt.setInt(1, customerId);
                pstmt.setInt(2, vehicleId);
                pstmt.setDate(3, new java.sql.Date(startDate.getTime()));
                pstmt.setDate(4, new java.sql.Date(endDate.getTime()));
                pstmt.setDouble(5, totalCost);
                pstmt.setString(6, status);
                pstmt.executeUpdate();

                tableModel.addRow(new Object[]{null, customerName, vehicleName, startDate, endDate, totalCost, status});

                JOptionPane.showMessageDialog(this, "Rental added successfully");

                clearInputFields();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding rental to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error parsing dates: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRental() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a rental to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int rentalId = (int) table.getValueAt(selectedRow, 0); // Assuming Rental_id is in the first column
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM rentals WHERE Rental_id = ?")) {

            pstmt.setInt(1, rentalId);
            pstmt.executeUpdate();

            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Rental deleted successfully");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting rental from database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearInputFields() {
        startDateField.setText("");
        endDateField.setText("");
        customerComboBox.setSelectedIndex(0);
        vehicleComboBox.setSelectedIndex(0);
        statusComboBox.setSelectedIndex(0);
    }

    private <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RentalsGUI app = new RentalsGUI();
            app.setVisible(true);
        });
    }
}
