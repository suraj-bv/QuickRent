package vehicle_management_system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate; // Import LocalDate
import java.time.format.DateTimeFormatter; // Import DateTimeFormatter

public class EmployeeGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    // Swing components
    private JTextField txtName, txtPosition, txtPhoneNo, txtEmailId, txtAddress, txtHireDate;
    private JTable table;
    private DefaultTableModel tableModel;

    private void goBackToMainMenu() {
        dispose(); // Close the current window
        showMainMenu(); // Show main menu
    }

    private void showMainMenu() {
        VehicleManagementSystemGUI systemGUI = new VehicleManagementSystemGUI();
        systemGUI.setVisible(true);
    }

    public EmployeeGUI() {
        // Set up the frame
        setTitle("Employee Form");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create and set up components
        JPanel panel = new JPanel(new GridLayout(7, 2));

        panel.add(new JLabel("Name:"));
        txtName = new JTextField();
        panel.add(txtName);

        panel.add(new JLabel("Position:"));
        txtPosition = new JTextField();
        panel.add(txtPosition);

        panel.add(new JLabel("Phone No:"));
        txtPhoneNo = new JTextField();
        panel.add(txtPhoneNo);

        panel.add(new JLabel("Email ID:"));
        txtEmailId = new JTextField();
        panel.add(txtEmailId);

        panel.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        panel.add(txtAddress);

        panel.add(new JLabel("Hire Date (YYYY-MM-DD):"));
        txtHireDate = new JTextField();
        txtHireDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); // Set current date
        panel.add(txtHireDate);

        add(panel, BorderLayout.NORTH);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");
        JButton btnBacktomainmenu = new JButton("Back to Main Menu");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBacktomainmenu);

        add(buttonPanel, BorderLayout.SOUTH);

        // Table to display employee data
        tableModel = new DefaultTableModel(new String[]{"Employee ID", "Name", "Position", "Phone No", "Email ID", "Address", "Hire Date"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Add action listeners
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (validateFields()) {
                    addEmployee();
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });

        btnBacktomainmenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBackToMainMenu();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtPosition.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    txtPhoneNo.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    txtEmailId.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    txtAddress.setText(tableModel.getValueAt(selectedRow, 5).toString());
                    txtHireDate.setText(tableModel.getValueAt(selectedRow, 6).toString());
                }
            }
        });

        retrieveEmployees();  // Load data initially
    }

    private boolean validateFields() {
        if (txtName.getText().isEmpty()) {
            showError("Name field is empty.");
            return false;
        }
        if (txtPosition.getText().isEmpty()) {
            showError("Position field is empty.");
            return false;
        }
        if (txtPhoneNo.getText().isEmpty()) {
            showError("Phone No field is empty.");
            return false;
        }
        if (txtEmailId.getText().isEmpty()) {
            showError("Email ID field is empty.");
            return false;
        }
        if (txtAddress.getText().isEmpty()) {
            showError("Address field is empty.");
            return false;
        }
        if (txtHireDate.getText().isEmpty()) {
            showError("Hire Date field is empty.");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addEmployee() {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO EMPLOYEE (Name, Position, Phone_no, Email_id, Address, Hire_date) VALUES (?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, txtName.getText());
            stmt.setString(2, txtPosition.getText());
            stmt.setString(3, txtPhoneNo.getText());
            stmt.setString(4, txtEmailId.getText());
            stmt.setString(5, txtAddress.getText());
            stmt.setString(6, txtHireDate.getText());
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showSuccessMessage("Employee added successfully");
                retrieveEmployees();
                clearFields();
            } else {
                showError("Failed to add employee");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error adding employee to database: " + e.getMessage());
        }
    }

    private void deleteEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String employeeId = tableModel.getValueAt(selectedRow, 0).toString();

            try (Connection conn = DBConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM EMPLOYEE WHERE Employee_id=?")) {

                stmt.setString(1, employeeId);
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    showSuccessMessage("Employee deleted successfully");
                } else {
                    showError("Employee not found or delete failed");
                }
                retrieveEmployees();
                clearFields();
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error deleting employee from database: " + e.getMessage());
            }
        } else {
            showError("Please select an employee to delete.");
        }
    }

    private void retrieveEmployees() {
        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM EMPLOYEE")) {

            tableModel.setRowCount(0);  // Clear the table
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("Employee_id"),
                        rs.getString("Name"),
                        rs.getString("Position"),
                        rs.getString("Phone_no"),
                        rs.getString("Email_id"),
                        rs.getString("Address"),
                        rs.getString("Hire_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error retrieving employees: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtPosition.setText("");
        txtPhoneNo.setText("");
        txtEmailId.setText("");
        txtAddress.setText("");
        txtHireDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); // Reset to current date
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EmployeeGUI().setVisible(true);
            }
        });
    }
}
