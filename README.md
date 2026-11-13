# Laundry Appointment Management System

A console-based laundry management and booking application developed as a project fulfillment for **Object-Oriented Programming and Information Management**.

The system streamlines the monitoring of pickup and delivery laundry appointments, machine allocation, and payment processing using a layered OOP architecture, with real business logic in accordance with client demands.

## Tech Stack Used

1. **Java** (JDBC, Maven)[cite: 3]
2. **MS SQL Server**[cite: 1, 3]

## Key Features

- **Customer Booking Module:** Create Walk-In or Delivery appointments, view real-time order status and billing, generate digital payment QR codes, and cancel pending bookings[cite: 1, 3].
- **Laundry Attendant Module:** Weigh laundry loads, automatically compute required machines ($\lceil \text{Weight} / 7\text{ kg} \rceil$), assign specific physical washers/dryers, monitor all appointments, and track estimated completion times[cite: 1, 5, 6].
- **Delivery Rider Module:** View assigned delivery appointments, update delivery statuses (`Out for Delivery`, `Completed`), and process cash or digital payments[cite: 1, 3].
- **Portable Database Connection & DAO Layer:** Connects to MS SQL Server via JDBC using externalized credentials (`config.properties`) for easy multi-device setup and secure version control[cite: 3]. Implements the Data Access Object (DAO) pattern to encapsulate full CRUD operations and atomic transaction rollbacks across relational tables[cite: 1, 2].
- **Role-Based Staff Authentication:** Authenticates staff credentials against the database and uses polymorphism to dynamically route users to either the Laundry Attendant or Delivery Rider menu[cite: 1, 6].
- **Machine Inventory & Status Tracking:** Monitors real-time operational states (`Available`, `In Use`, `Under Maintenance`) for washers and dryers, automatically locking assigned machines during active cycles and releasing them upon completion[cite: 1, 5].
