package busreservation;

import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;

public class BusReservation extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bus_reservation";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";
    private JTextField routeNumberField, seatNumberField;
    private JLabel feedbackLabel;

    public BusReservation() {
        setTitle("Bus Reservation System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Title Label
        JLabel titleLabel = new JLabel("Bus Reservation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.DARK_GRAY);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);

        // Empty space
        add(Box.createVerticalStrut(20));

        // Route number input field
        JPanel routePanel = new JPanel();
        routePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        routePanel.add(new JLabel("Route Number:"));
        routeNumberField = new JTextField(10);
        routePanel.add(routeNumberField);
        add(routePanel);

        // Seat number input field
        JPanel seatPanel = new JPanel();
        seatPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        seatPanel.add(new JLabel("Seat Number:"));
        seatNumberField = new JTextField(10);
        seatPanel.add(seatNumberField);
        add(seatPanel);

        // Action Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton viewBusesButton = new JButton("View Buses");
        styleButton(viewBusesButton);
        viewBusesButton.addActionListener(e -> viewBuses());
        buttonPanel.add(viewBusesButton);

        JButton bookSeatButton = new JButton("Book Seat");
        styleButton(bookSeatButton);
        bookSeatButton.addActionListener(e -> bookSeat());
        buttonPanel.add(bookSeatButton);

        JButton cancelSeatButton = new JButton("Cancel Seat");
        styleButton(cancelSeatButton);
        cancelSeatButton.addActionListener(e -> cancelSeat());
        buttonPanel.add(cancelSeatButton);

        JButton viewBookedSeatsButton = new JButton("View Booked Seats");
        styleButton(viewBookedSeatsButton);
        viewBookedSeatsButton.addActionListener(e -> viewBookedSeats());
        buttonPanel.add(viewBookedSeatsButton);

        add(buttonPanel);

        // Feedback Label
        feedbackLabel = new JLabel("");
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 16));
        feedbackLabel.setForeground(Color.RED);
        feedbackLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(feedbackLabel);

        // Set the window visible
        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(85, 170, 255));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(180, 40));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 150, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(85, 170, 255));
            }
        });
    }

    public void viewBuses() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String query = "SELECT * FROM buses";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            StringBuilder output = new StringBuilder("<html><b>Available Buses:</b><br>");
            while (rs.next()) {
                output.append("Route Number: ").append(rs.getInt("routeNumber"))
                        .append(" | Route: ").append(rs.getString("route"))
                        .append(" | Available Seats: ").append(getAvailableSeats(rs.getInt("routeNumber")))
                        .append("<br>");
            }
            output.append("</html>");
            feedbackLabel.setText(output.toString());
            feedbackLabel.setForeground(Color.BLACK);
        } catch (SQLException e) {
            e.printStackTrace();
            feedbackLabel.setText("Error fetching buses.");
            feedbackLabel.setForeground(Color.RED);
        }
    }

    public int getAvailableSeats(int routeNumber) {
        int bookedSeatsCount = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String query = "SELECT COUNT(*) FROM seat_bookings WHERE routeNumber = ? AND bookingStatus = TRUE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, routeNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                bookedSeatsCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 40 - bookedSeatsCount; // assuming all buses have 40 seats
    }

    public void bookSeat() {
        try {
            int routeNumber = Integer.parseInt(routeNumberField.getText());
            int seatNumber = Integer.parseInt(seatNumberField.getText());
            if (!isSeatAvailable(routeNumber,seatNumber)){
                feedbackLabel.setText("Seat " + seatNumber + " on route " + routeNumber + " is already booked!!");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
            if (bookSeatInDatabase(routeNumber, seatNumber)) {
                feedbackLabel.setText("Seat " + seatNumber + " on route " + routeNumber + " booked successfully!");
                feedbackLabel.setForeground(Color.GREEN);
            } else {
                feedbackLabel.setText("Failed to book seat " + seatNumber + " on route " + routeNumber + ".");
                feedbackLabel.setForeground(Color.RED);
            }
        }
                catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter valid numbers for route and seat.");
            feedbackLabel.setForeground(Color.RED);
        }
    }
    private boolean isSeatAvailable(int routeNumber, int seatNumber) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String query = "SELECT bookingStatus FROM seat_bookings WHERE routeNumber = ? AND seatNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, routeNumber);
            stmt.setInt(2, seatNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                if(rs.getBoolean("bookingStatus")){
                    return false;
                }
                 // return true if the seat is not booked
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true; // if seat is not found in the database, it is available
    }

    private boolean bookSeatInDatabase(int routeNumber, int seatNumber) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String query = "INSERT INTO seat_bookings (routeNumber, seatNumber, bookingStatus) VALUES (?, ?, TRUE)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, routeNumber);
            stmt.setInt(2, seatNumber);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void cancelSeat() {
        try {
            int routeNumber = Integer.parseInt(routeNumberField.getText());
            int seatNumber = Integer.parseInt(seatNumberField.getText());

            if (cancelSeatInDatabase(routeNumber, seatNumber)) {
                feedbackLabel.setText("Seat " + seatNumber + " on route " + routeNumber + " canceled successfully!");
                feedbackLabel.setForeground(Color.GREEN);
            } else {
                feedbackLabel.setText("Failed to cancel seat " + seatNumber + " on route " + routeNumber + ".");
                feedbackLabel.setForeground(Color.RED);
            }
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter valid numbers for route and seat.");
            feedbackLabel.setForeground(Color.RED);
        }
    }

    private boolean cancelSeatInDatabase(int routeNumber, int seatNumber) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String query = "UPDATE seat_bookings SET bookingStatus = FALSE WHERE routeNumber = ? AND seatNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, routeNumber);
            stmt.setInt(2, seatNumber);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void viewBookedSeats() {
        try {
            int routeNumber = Integer.parseInt(routeNumberField.getText());
            Set<Integer> bookedSeats = getBookedSeats(routeNumber);
            if (!bookedSeats.isEmpty()) {
                StringBuilder output = new StringBuilder("<html><b>Booked Seats:</b><br>");
                for (Integer seat : bookedSeats) {
                    output.append("Seat Number: ").append(seat).append("<br>");
                }
                output.append("</html>");
                feedbackLabel.setText(output.toString());
                feedbackLabel.setForeground(Color.BLACK);
            } else {
                feedbackLabel.setText("No seats are booked on route " + routeNumber + ".");
                feedbackLabel.setForeground(Color.RED);
            }
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter a valid route number.");
            feedbackLabel.setForeground(Color.RED);
        }
    }

    private Set<Integer> getBookedSeats(int routeNumber) {
        Set<Integer> bookedSeats = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String query = "SELECT seatNumber FROM seat_bookings WHERE routeNumber = ? AND bookingStatus = TRUE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, routeNumber);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookedSeats.add(rs.getInt("seatNumber"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookedSeats;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BusReservation());
    }
}
