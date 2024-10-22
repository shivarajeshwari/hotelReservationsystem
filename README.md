Hotel_Reservation_System 
operations permormed in this project:
Reserve a Room: Easily make new reservations by providing guest details, room numbers, and contact information.
         also checks if the specified room is empty or not and returns 

View Reservations: Get an overview of all current reservations, including guest names, room numbers, contact details, and reservation dates.

Edit Reservation Details: Update guest names, room numbers, and contact information for existing reservations based on reservation id provided.

Delete Reservations: Remove reservations that are no longer needed.

Prerequisites
Java Development Kit (JDK)
Postgre Database
Postgre Connector/J (Java)

setup:
1. Download and then add it to libraries of eclipse postgre sql jdbc driver.
2. Configure your postgre sql database settings in the HotelReservationSystem.java file:

private static final String DB_URL = "jdbc:postgresql://localhost:5432/HotelDB";
private static final String DB_USER = "your_username";
private static final String DB_PASSWORD = "your_password";

Compile and run the application

Follow the on-screen menu options to use the system.
