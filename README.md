🚌 Bus Reservation System (Java Swing + MySQL)
📌 Project Overview

The Bus Reservation System is a desktop-based application developed using Java Swing for the graphical user interface and MySQL for database management.

The system allows users to view available buses, check seat availability, book seats, cancel reservations, and view booked seats in real time using database-driven operations.

This project demonstrates practical implementation of JDBC connectivity, CRUD operations, GUI development, and database design.

🚀 Key Features

View available buses with seat count

Real-time seat availability tracking

Book seat with validation

Cancel seat functionality

View booked seats by route

Database-driven backend (persistent storage)

Input validation and error handling

Interactive GUI with styled buttons

🛠 Technologies Used

Java

Java Swing (GUI)

MySQL

JDBC

Object-Oriented Programming (OOP)

🧠 Concepts Implemented

JFrame-based GUI design

Event Handling using ActionListener

JDBC connection using DriverManager

PreparedStatement (prevents SQL injection)

SQL Operations: SELECT, INSERT, UPDATE, COUNT

Set (HashSet) for seat management

Exception Handling

Event Dispatch Thread using SwingUtilities.invokeLater()

🗄 Database Design

Database Name: bus_reservation

Tables Used:

1️⃣ buses
routeNumber – INT (Primary Key)
route – VARCHAR

2️⃣ seat_bookings
routeNumber – INT
seatNumber – INT
bookingStatus – BOOLEAN

▶️ How to Run the Project

Install MySQL Server

Create database: CREATE DATABASE bus_reservation;

Create required tables

Update database credentials in the code:
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";

Run BusReservation.java in any Java IDE (NetBeans / IntelliJ / Eclipse)

📂 Project Structure

Bus-Reservation-System
├── src/
│ └── busreservation/
│ └── BusReservation.java
├── build.xml
├── manifest.mf
└── README.md

🔮 Future Enhancements

Admin authentication system

Payment integration

Dynamic seat layout visualization

Online deployment using web technologies

Report generation system

👨‍💻 Developer

Madduri Sujay Reddy