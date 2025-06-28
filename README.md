# 🚆 Online Train Reservation System

## 📌 Objective

The **Online Reservation System** is a Java Swing-based desktop application designed to let users:
- Register and log in.
- Book train tickets by selecting trains, journey dates, and seat count.
- View their reservations in a tabular format.
- Cancel bookings using their PNR number.
- Allow admins to manage trains and oversee bookings.

This project aims to automate and streamline the railway ticket booking process.

---

## 🔨 Tools & Technologies

- **Programming Language:** Java (JDK 8+)
- **GUI:** Java Swing
- **Database:** MySQL
- **Database Connector:** MySQL Connector/J
- **IDE:** NetBeans or IntelliJ IDEA (recommended for building & debugging)

---

## ⚙️ Steps Performed

1. **Database Design:**
   - Created MySQL tables for `users`, `trains`, and `reservations` with appropriate keys and constraints.
   - Used `AUTO_INCREMENT` for primary keys like `pnr` and `id`.

2. **Database Connection:**
   - Built a reusable `DBConnection.java` class for establishing connections with MySQL.

3. **User Authentication:**
   - Developed `LoginFrame.java` for login functionality.
   - Built `RegisterForm.java` for new user registration.

4. **Booking System:**
   - Implemented `BookingDialog.java` for booking tickets.
   - Integrated reservation logic in `ReservationService.java`.

5. **User Dashboard:**
   - Developed `UserDashboard.java` to display current user reservations and allow ticket cancellation.
   - Included `CancelReservationFrame.java` to handle reservation cancellations with PNR lookup.

6. **Admin Dashboard:**
   - Built `AdminDashboard.java` for admin operations like viewing, adding, and editing train details.

7. **Data Access Layer:**
   - Created DAOs (`UserDAO`, `TrainDAO`, `ReservationDAO`) to handle all database queries and updates.

8. **Exception Handling:**
   - Added proper validation, error dialogs, and SQL exception management across the application.

---

## 📂 Project Structure

src/
├── DBConnection.java
├── LoginFrame.java
├── RegisterForm.java
├── UserDashboard.java
├── AdminDashboard.java
├── BookingDialog.java
├── CancelReservationFrame.java
├── ReservationService.java
├── UserDAO.java
├── TrainDAO.java
├── ReservationDAO.java
└── model/
├── User.java
├── Train.java
└── Reservation.java

## 🛠️ How to Run

1. **Set up the MySQL database:**
   - Create a database, e.g., `reservation_system`.
   - Import your tables (`users`, `trains`, `reservations`) using the provided SQL schema.

2. **Configure the database connection:**
   - Edit `DBConnection.java` with your MySQL credentials and database URL.

3. **Compile and run:**
   - Compile all Java files:
     ```bash
     javac -d bin src/*.java src/model/*.java
     ```
   - Run the main class:
     ```bash
     java -cp bin LoginFrame
     ```

4. **Use the UI:**
   - Log in as an existing user or admin, or register as a new user.

---

## ✅ Outcome

- Users can register, book, view, and cancel train tickets with a user-friendly GUI.
- Admins can manage trains and oversee reservations.
- All operations are securely stored and updated in the MySQL database.

---

### 🎉 Enjoy using your Online Train Reservation System!
