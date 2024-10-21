package hotelreservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class reservationSys {
	 private static final String url="jdbc:postgresql://localhost:5432/HotelDB";
     private static final String username ="postgres";
     private static final String password="shiva99";
	public static void main(String[] args) {
       
       try{
        Class.forName("org.postgresql.Driver");//drivers are loaded
       } catch(ClassNotFoundException e){
                System.out.println(e.getMessage());
       }
       try{
           Connection connection = DriverManager.getConnection(url, username, password);
           while(true){
               System.out.println();
               System.out.println("HOTEL MANAGEMENT SYSTEM");
               Scanner scanner = new Scanner(System.in);
               System.out.println("1. Reserve a room");
               System.out.println("2. View Reservations");
               System.out.println("3. Get Room Number");
               System.out.println("4. Update Reservations");
               System.out.println("5. Delete Reservations");
               System.out.println("6.View Reciept ");
               System.out.println("0. Exit");
               System.out.print("Choose an option: ");
               int choice = scanner.nextInt();
               switch (choice) {
                   case 1:
                       reserveRoom(connection, scanner);
                       break;
                   case 2:
                       viewReservations(connection);
                       break;
                   case 3:
                       getRoomNumber(connection, scanner);
                       break;
                   case 4:
                       updateReservation(connection, scanner);
                       break;
                   case 5:
                       deleteReservation(connection, scanner);
                       break;
                   case 0:
                       exit();
                       scanner.close();
                       return;
                   default:
                       System.out.println("Invalid choice. Try again.");
               }
           }

       }catch (SQLException e){
           System.out.println(e.getMessage());
       } catch (InterruptedException e) {
           throw new RuntimeException(e);
       }


   }

   private static void reserveRoom(Connection connection, Scanner scanner) {
       try {
    	   int roomNumber =0;
           System.out.print("Enter guest name: ");
           String guestName = scanner.next();
           scanner.nextLine();
           boolean val = true;
           while(val) {
                System.out.print("Enter room number between 1 and 150: ");
                roomNumber = scanner.nextInt();
                String sql = "select count(roomnumber) as id from reservations";
        	   try (Statement statement = connection.createStatement()) {
        		   ResultSet resultset = statement.executeQuery(sql);
            	   resultset.next();
            	   int count = resultset.getInt("id");
            	   if(count >=1) {
            		   System.out.println("Room is already booked. please, choose other room number");
                   
            	   } else val = false;
            	   }catch (SQLException e) {
                           e.printStackTrace();
                       }
           }
       
           System.out.print("Enter contact number: ");
           String contactNumber = scanner.next();

           String sql1 = "INSERT INTO reservations (guestname, roomnumber, contactnumber) " +
                   "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";
           
           String sql2 = "select reservation_id from reservations where contactnumber='"+contactNumber+"'";
           try (Statement statement = connection.createStatement()) {
               int affectedRows = statement.executeUpdate(sql1);

               if (affectedRows > 0) {
                   System.out.println("Reservation successful!");
                   try (Statement statement1 = connection.createStatement()) {
                	   ResultSet resultset = statement1.executeQuery(sql2);
                	   resultset.next();
                	   int id = resultset.getInt("reservation_id");
                	     System.out.println("please note ur registration_id: "+id);
                   }catch (SQLException e) {
                       e.printStackTrace();
                   }
                
               } else {
                   System.out.println("Reservation failed.");
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   private static void viewReservations(Connection connection) throws SQLException {
       String sql = "SELECT reservation_id, guestname, roomnumber, contactnumber, reservation_date FROM reservations";

       try (Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {

           System.out.println("Current Reservations:");
           System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
           System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
           System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

           while (resultSet.next()) {
               int reservationId = resultSet.getInt("reservation_id");
               String guestName = resultSet.getString("guestname");
               int roomNumber = resultSet.getInt("roomnumber");
               String contactNumber = resultSet.getString("contactnumber");
               String reservationDate = resultSet.getTimestamp("reservation_date").toString();

               // Format and display the reservation data in a table-like format
               System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                       reservationId, guestName, roomNumber, contactNumber, reservationDate);
           }

           System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
       }
   }


   private static void getRoomNumber(Connection connection, Scanner scanner) {
       try {
           System.out.print("Enter reservation ID: ");
           int reservationId = scanner.nextInt();
           System.out.print("Enter guest name: ");
           String guestName = scanner.next();

           String sql = "SELECT roomnumber FROM reservations " +
                   "WHERE reservation_id = " + reservationId +
                   " AND guestname = '" + guestName + "'";

           try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

               if (resultSet.next()) {
                   int roomNumber = resultSet.getInt("roomnumber");
                   System.out.println("Room number for Reservation ID " + reservationId +
                           " and Guest " + guestName + " is: " + roomNumber);
               } else {
                   System.out.println("Reservation not found for the given ID and guest name.");
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }
   private static void updateReservation(Connection connection, Scanner scanner) {
       try {
           System.out.print("Enter reservation ID to update: ");
           int reservationId = scanner.nextInt();
           scanner.nextLine(); // Consume the newline character

           if (!reservationExists(connection, reservationId)) {
               System.out.println("Reservation not found for the given ID.");
               return;
           }

           System.out.print("Enter new guest name: ");
           String newGuestName = scanner.nextLine();
           System.out.print("Enter new room number: ");
           int newRoomNumber = scanner.nextInt();
           System.out.print("Enter new contact number: ");
           String newContactNumber = scanner.next();

           String sql = "UPDATE reservations SET guestname = '" + newGuestName + "', " +
                   "roomnumber = " + newRoomNumber + ", " +
                   "contactnumber = '" + newContactNumber + "' " +
                   "WHERE reservation_id = " + reservationId;

           try (Statement statement = connection.createStatement()) {
               int affectedRows = statement.executeUpdate(sql);

               if (affectedRows > 0) {
                   System.out.println("Reservation updated successfully!");
               } else {
                   System.out.println("Reservation update failed.");
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   private static void deleteReservation(Connection connection, Scanner scanner) {
       try {
           System.out.print("Enter reservation ID to delete: ");
           int reservationId = scanner.nextInt();

           if (!reservationExists(connection, reservationId)) {
               System.out.println("Reservation not found for the given ID.");
               return;
           }

           String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

           try (Statement statement = connection.createStatement()) {
               int affectedRows = statement.executeUpdate(sql);

               if (affectedRows > 0) {
                   System.out.println("Reservation deleted successfully!");
               } else {
                   System.out.println("Reservation deletion failed.");
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   private static boolean reservationExists(Connection connection, int reservationId) {
       try {
           String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

           try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

               return resultSet.next(); // If there's a result, the reservation exists
           }
       } catch (SQLException e) {
           e.printStackTrace();
           return false; // Handle database errors as needed
       }
   }


   public static void exit() throws InterruptedException {
       System.out.print("Exiting System");
       int i = 5;
       while(i!=0){
           System.out.print(".");
           Thread.sleep(1000);
           i--;
       }
       System.out.println();
       System.out.println("ThankYou For Using Hotel Reservation System!!!");
   }
}