package vehicle_management_system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VehicleManagementSystemGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    public VehicleManagementSystemGUI() {
        setTitle("Vehicle Management System");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel to hold the buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 4, 10, 10)); // Added gaps for better spacing

        // Button for Vehicle Management GUI
        JButton vehicleManagementButton = new JButton("Vehicles");
        vehicleManagementButton.setPreferredSize(new Dimension(150, 50));
        vehicleManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VehiclesGUI vehicleManagementGUI = new VehiclesGUI();
                vehicleManagementGUI.setVisible(true);
            }
        });
        panel.add(vehicleManagementButton);

        // Button for Customer Management GUI
        JButton customerManagementButton = new JButton("Customers");
        customerManagementButton.setPreferredSize(new Dimension(150, 50));
        customerManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomersGUI customerManagementGUI = new CustomersGUI();
                customerManagementGUI.setVisible(true);
            }
        });
        panel.add(customerManagementButton);

        // Button for Rentals GUI
        JButton rentalsButton = new JButton("Rentals");
        rentalsButton.setPreferredSize(new Dimension(150, 50));
        rentalsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RentalsGUI rentalsGUI = new RentalsGUI();
                rentalsGUI.setVisible(true);
            }
        });
        panel.add(rentalsButton);
        
        // Button for Payments GUI
        JButton paymentsButton = new JButton("Payments");
        paymentsButton.setPreferredSize(new Dimension(150, 50));
        paymentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PaymentGUI paymentsGUI = new PaymentGUI();
                paymentsGUI.setVisible(true);
            }
        });
        panel.add(paymentsButton);
        
        JButton employeesButton = new JButton("Employees");
        employeesButton.setPreferredSize(new Dimension(150, 50));
        employeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EmployeeGUI employeesGUI = new EmployeeGUI();
                employeesGUI.setVisible(true);
            }
        });
        panel.add(employeesButton);
        
        JButton reservationButton = new JButton("Reservations");
        reservationButton.setPreferredSize(new Dimension(150, 50));
        reservationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReservationsGUI reservationGUI = new ReservationsGUI();
                reservationGUI.setVisible(true);
            }
        });
        panel.add(reservationButton);
        
        JButton maintenanceButton = new JButton("Maintenance");
        maintenanceButton.setPreferredSize(new Dimension(150, 50));
        maintenanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MaintenanceGUI maintenanceGUI = new MaintenanceGUI();
                maintenanceGUI.setVisible(true);
            }
        });
        panel.add(maintenanceButton);

        // Add panel to frame
        add(panel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VehicleManagementSystemGUI systemGUI = new VehicleManagementSystemGUI();
            systemGUI.setVisible(true);
        });
    }
}

