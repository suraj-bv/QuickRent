package vehicle_management_system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.Pattern;

public class CustomersGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField tfCustomerName, tfAddress, tfLisenceno, tfDOB, tfPhoneNo, tfEmail;
    private JButton btnAdd, btnDelete;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private Connection conn;

    public CustomersGUI() {
        super("Customer Form");

        // Initialize components
        tfCustomerName = new JTextField(20);
        tfAddress = new JTextField(30);
        tfLisenceno = new JTextField(10);
        tfDOB = new JTextField(10);
        tfPhoneNo = new JTextField(10);
        tfEmail = new JTextField(30);
        btnAdd = new JButton("Add");
        btnDelete = new JButton("Delete");

        // Apply validation to Name field
        ((AbstractDocument) tfCustomerName.getDocument()).setDocumentFilter(new NameDocumentFilter());

        // Back button
        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToMainMenu(); // Navigate back to the main menu
            }
        });

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(tfCustomerName);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(tfAddress);
        inputPanel.add(new JLabel("License No:"));
        inputPanel.add(tfLisenceno);
        inputPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        inputPanel.add(tfDOB);
        inputPanel.add(new JLabel("Phone No:"));
        inputPanel.add(tfPhoneNo);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(tfEmail);

        // Table model and table
        tableModel = new DefaultTableModel(new String[]{"Customer ID", "Name", "Address", "License No", "DOB", "Phone No", "Email"}, 0);
        customerTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(customerTable);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(backButton);

        // Layout
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Database connection
        conn = DBConnector.getConnection();

        // Add button action
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    addCustomer();
                    loadCustomers();
                    clearInputFields();
                }
            }
        });

        // Delete button action
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
                loadCustomers();
                clearInputFields();
            }
        });

        // Table row selection listener
        customerTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    tfCustomerName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    tfAddress.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    tfLisenceno.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    tfDOB.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    tfPhoneNo.setText(tableModel.getValueAt(selectedRow, 5).toString());
                    tfEmail.setText(tableModel.getValueAt(selectedRow, 6).toString());
                }
            }
        });

        // Frame settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        // Load customers
        loadCustomers();
    }

    private void goBackToMainMenu() {
        dispose(); // Close the current window
        showMainMenu(); // Show main menu
    }

    private void showMainMenu() {
        VehicleManagementSystemGUI systemGUI = new VehicleManagementSystemGUI();
        systemGUI.setVisible(true);
    }

    private boolean validateInput() {
        String phoneNo = tfPhoneNo.getText();

        // Validate phone number
        if (!Pattern.matches("\\d{10}", phoneNo)) {
            JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void loadCustomers() {
        try {
            String sql = "SELECT * FROM CUSTOMER";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            tableModel.setRowCount(0);

            while (resultSet.next()) {
                String customerId = resultSet.getString("Customer_id");
                String name = resultSet.getString("Name");
                String address = resultSet.getString("Address");
                String lisenceno = resultSet.getString("Lisence_no");
                String dob = resultSet.getString("DOB");
                String phone = resultSet.getString("Phone_no");
                String email = resultSet.getString("Email_id");

                tableModel.addRow(new Object[]{customerId, name, address, lisenceno, dob, phone, email});
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage());
        }
    }

    private void addCustomer() {
        String name = tfCustomerName.getText();
        String address = tfAddress.getText();
        String lisenceno = tfLisenceno.getText();
        String dob = tfDOB.getText();
        String phone = tfPhoneNo.getText();
        String email = tfEmail.getText();

        try {
            String sql = "INSERT INTO CUSTOMER (Name, Address, Lisence_no, DOB, Phone_no, Email_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, address);
            statement.setString(3, lisenceno);
            statement.setString(4, dob);
            statement.setString(5, phone);
            statement.setString(6, email);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Customer added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            String customerId = tableModel.getValueAt(selectedRow, 0).toString();

            try {
                String sql = "DELETE FROM CUSTOMER WHERE Customer_id=?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, customerId);

                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Customer deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Customer not found or delete failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearInputFields() {
        tfCustomerName.setText("");
        tfAddress.setText("");
        tfLisenceno.setText("");
        tfDOB.setText("");
        tfPhoneNo.setText("");
        tfEmail.setText("");
    }

    private static class NameDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (isValidInput(string)) {
                super.insertString(fb, offset, string, attr);
            } else {
                Toolkit.getDefaultToolkit().beep(); // Beep on invalid input
                JOptionPane.showMessageDialog(null, "Name field accepts only alphabetic characters.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (isValidInput(text)) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                Toolkit.getDefaultToolkit().beep(); // Beep on invalid input
                JOptionPane.showMessageDialog(null, "Name field accepts only alphabetic characters.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length);
        }

        private boolean isValidInput(String input) {
            return input.matches("[a-zA-Z ]*"); // Only letters and spaces allowed
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CustomersGUI();
            }
        });
    }
}
