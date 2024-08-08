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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaymentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> rentalIdComboBox;
    private JTextField paymentDateField;
    private JTextField amountField;
    private JComboBox<String> paymentMethodComboBox;
    private JComboBox<String> paymentStatusComboBox;
    private JTextField customerNameField;
    private JTextField vehicleNameField;
    private JButton submitButton, backButton;
    private JTable paymentsTable;
    private DefaultTableModel tableModel;
    private Map<String, String[]> rentalDetailsMap;

    private void goBackToMainMenu() {
        dispose(); // Close the current window
        showMainMenu(); // Show main menu
    }

    private void showMainMenu() {
        VehicleManagementSystemGUI systemGUI = new VehicleManagementSystemGUI();
        systemGUI.setVisible(true);
    }

    public PaymentGUI() {
        rentalDetailsMap = new HashMap<>();
        createForm();
        populateRentalIdComboBox();
        fetchPayments();
    }

    private void createForm() {
        setTitle("Payment Form");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center the frame

        JPanel formPanel = new JPanel(new GridLayout(7, 2));

        // Labels and text fields
        formPanel.add(new JLabel("Rental ID:"));
        rentalIdComboBox = new JComboBox<>();
        formPanel.add(rentalIdComboBox);

        formPanel.add(new JLabel("Customer Name:"));
        customerNameField = new JTextField();
        customerNameField.setEditable(false);
        formPanel.add(customerNameField);

        formPanel.add(new JLabel("Vehicle Name:"));
        vehicleNameField = new JTextField();
        vehicleNameField.setEditable(false);
        formPanel.add(vehicleNameField);

        formPanel.add(new JLabel("Payment Date (YYYY-MM-DD):"));
        paymentDateField = new JTextField();

        // Set current date as default in paymentDateField
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());
        paymentDateField.setText(currentDate);

        formPanel.add(paymentDateField);

        formPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        amountField.setEditable(false); // make the amount field non-editable
        formPanel.add(amountField);

        formPanel.add(new JLabel("Payment Method:"));
        paymentMethodComboBox = new JComboBox<>(new String[]{"UPI", "Cash", "Credit/Debit Card", "Net Banking"});
        formPanel.add(paymentMethodComboBox);

        formPanel.add(new JLabel("Payment Status:"));
        paymentStatusComboBox = new JComboBox<>(new String[]{"Paid", "Waiting"});
        formPanel.add(paymentStatusComboBox);

        // Add form panel to the top
        add(formPanel, BorderLayout.NORTH);

        // Submit and back buttons
        submitButton = new JButton("Submit");
        backButton = new JButton("Back to Main Menu");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitPayment();
                fetchPayments();
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

        // Payments table
        tableModel = new DefaultTableModel(new String[]{"Payment ID", "Rental ID", "Payment Date", "Amount", "Payment Method", "Payment Status"}, 0);
        paymentsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        add(scrollPane, BorderLayout.CENTER);

        rentalIdComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String rentalId = (String) rentalIdComboBox.getSelectedItem();
                    String[] details = rentalDetailsMap.get(rentalId);
                    customerNameField.setText(details[0]);
                    vehicleNameField.setText(details[1]);
                    amountField.setText(details[2]);
                }
            }
        });

        setVisible(true);
    }

    private void submitPayment() {
        String rentalId = (String) rentalIdComboBox.getSelectedItem();
        String paymentDate = paymentDateField.getText();
        String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();
        String paymentStatus = (String) paymentStatusComboBox.getSelectedItem();

        // Fetch the total cost from the vehicle table
        String amount = amountField.getText();
        if (amount == null || amount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error fetching total cost.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insert payment into database
        try (Connection connection = DBConnector.getConnection()) {
            String query = "INSERT INTO payment (Rental_id, Payment_date, Amount, Payment_method, Payment_status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, rentalId);
            statement.setString(2, paymentDate);
            statement.setString(3, amount);
            statement.setString(4, paymentMethod);
            statement.setString(5, paymentStatus);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Payment successfully added.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error inserting payment into database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        rentalIdComboBox.setSelectedIndex(0);
        paymentDateField.setText("");
        amountField.setText("");
        paymentMethodComboBox.setSelectedIndex(0);
        paymentStatusComboBox.setSelectedIndex(0);
        customerNameField.setText("");
        vehicleNameField.setText("");
    }

    private void populateRentalIdComboBox() {
        try (Connection connection = DBConnector.getConnection()) {
            String query = "SELECT r.Rental_id, c.Name, v.Vehicle_name, r.Total_cost " +
                           "FROM rentals r " +
                           "JOIN customer c ON r.Customer_id = c.Customer_id " +
                           "JOIN vehicle v ON r.Vehicle_id = v.Vehicle_id";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String rentalId = resultSet.getString("Rental_id");
                String customerName = resultSet.getString("Name");
                String vehicleName = resultSet.getString("Vehicle_name");
                String totalCost = resultSet.getString("Total_cost");

                rentalDetailsMap.put(rentalId, new String[]{customerName, vehicleName, totalCost});
                rentalIdComboBox.addItem(rentalId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching rental IDs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchPayments() {
        try (Connection connection = DBConnector.getConnection()) {
            String query = "SELECT Payment_id, Rental_id, Payment_date, Amount, Payment_method, Payment_status FROM payment";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            tableModel.setRowCount(0); // Clear existing data
            while (resultSet.next()) {
                String paymentId = resultSet.getString("Payment_id");
                String rentalId = resultSet.getString("Rental_id");
                String paymentDate = resultSet.getString("Payment_date");
                String amount = resultSet.getString("Amount");
                String paymentMethod = resultSet.getString("Payment_method");
                String paymentStatus = resultSet.getString("Payment_status");
                tableModel.addRow(new Object[]{paymentId, rentalId, paymentDate, amount, paymentMethod, paymentStatus});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching payments.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PaymentGUI());
    }
}
