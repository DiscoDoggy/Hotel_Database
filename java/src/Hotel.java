/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Hotel {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Hotel 
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Hotel(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Hotel

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement ();

      ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   public int getNewUserID(String sql) throws SQLException {
      Statement stmt = this._connection.createStatement ();
      ResultSet rs = stmt.executeQuery (sql);
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }
   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Hotel.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Hotel esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Hotel object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Hotel (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Hotels within 30 units");
                System.out.println("2. View Rooms");
                System.out.println("3. Book a Room");
                System.out.println("4. View recent booking history");

                //the following functionalities basically used by managers
                System.out.println("5. Update Room Information");
                System.out.println("6. View 5 recent Room Updates Info");
                System.out.println("7. View booking history of the hotel");
                System.out.println("8. View 5 regular Customers");
                System.out.println("9. Place room repair Request to a company");
                System.out.println("10. View room repair Requests history");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewHotels(esql); break;
                   case 2: viewRooms(esql); break;
                   case 3: bookRooms(esql, Integer.parseInt(authorisedUser)); break;
                   case 4: viewRecentBookingsfromCustomer(esql, Integer.parseInt(authorisedUser)); break;
                   case 5: updateRoomInfo(esql, Integer.parseInt(authorisedUser)); break;
                   case 6: viewRecentUpdates(esql, Integer.parseInt(authorisedUser)); break;
                   case 7: viewBookingHistoryofHotel(esql, Integer.parseInt(authorisedUser)); break;
                   case 8: viewRegularCustomers(esql, Integer.parseInt(authorisedUser)); break;
                   case 9: placeRoomRepairRequests(esql, Integer.parseInt(authorisedUser)); break;
                   case 10: viewRoomRepairHistory(esql, Integer.parseInt(authorisedUser)); break;
                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Hotel esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine(); 
         String type="Customer";
			String query = String.format("INSERT INTO USERS (name, password, userType) VALUES ('%s','%s', '%s')", name, password, type);
         esql.executeUpdate(query);
         System.out.println ("User successfully created with userID = " + esql.getNewUserID("SELECT last_value FROM users_userID_seq"));
         
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Hotel esql){
      try{
         System.out.print("\tEnter username ");
         String user_name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM Users WHERE name = '%s' AND password = '%s'", user_name, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0){

		String query_userID = String.format("SELECT userID FROM Users WHERE name = '%s';", user_name);
		List<List<String>> user_id_list = esql.executeQueryAndReturnResult(query_userID);
			//System.out.println(user_id_list.size());
		String userID = user_id_list.get(0).get(0);

		System.out.print("\n\nQUERIED USER ID: " + userID + "\n\n");

            return userID;
	   }
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewHotels(Hotel esql) {
   	try{
		System.out.print("\tTo list hotels within 30 units, please enter a latitude: ");
		Scanner scanner = new Scanner(System.in);
		double user_latitude = scanner.nextDouble();

		System.out.print("\tPlease enter a longitude: ");
		double user_longitude = scanner.nextDouble();

		System.out.println("You entered: " + user_latitude +  ", " + user_longitude + "\n");	
		
		String query = String.format ("SELECT hotelName FROM Hotel WHERE calculate_distance('%f', '%f', latitude, longitude) < 30;", user_latitude, user_longitude);
			
		int row_count = esql.executeQueryAndPrintResult(query);	
	}

	catch (Exception e){
		System.err.println(e.getMessage());
	}
   
   }


   public static boolean isValidDate(String user_input_date){
   	SimpleDateFormat date_format = new SimpleDateFormat("M/dd/yyyy");

	date_format.setLenient(false);

	try{ //date is valid
		Date date = date_format.parse(user_input_date);
		return true;
	
	}

	catch(Exception e) { //date is invalid with M/dd/yyyy format. Retry
		date_format.applyPattern("MM/dd/yyyy");
		
		try { //date is valid
			Date date = date_format.parse(user_input_date);
			return true;
		}

		catch (Exception failed_date) { //date is invalid
			return false;
		}
	}
   }


   public static void viewRooms(Hotel esql) {
   	try {
		System.out.print("\tTo browse the available rooms at a hotel, please enter a hotel ID: ");
		Scanner scanner = new Scanner(System.in);
		int user_hotel_id = scanner.nextInt();

		System.out.print("\tPlease enter a date to check availability: ");
		String user_date = in.readLine();
		
		if (isValidDate(user_date) == false) {
			System.out.print("\tInvalid date. Please enter a date in the format MM/dd/yyyy or M/dd/yyyy.\n");
			return;
		}

		String query = String.format("SELECT roomNumber, price\n" +
						"FROM Rooms R\n" +
						"WHERE R.hotelID = '%d' AND roomNumber NOT IN(\n" +
					       		"SELECT roomNumber\n" +
							"FROM RoomBookings\n" +
							"WHERE '%d' = hotelID AND bookingDate = '%s');", user_hotel_id,user_hotel_id,  user_date);
		int row_count = esql.executeQueryAndPrintResult(query);
	
	}
	catch (Exception e) {
		System.err.println(e.getMessage());
	}
   
   }
   public static void bookRooms(Hotel esql, int user_id) {
   	try{
		Scanner scanner = new Scanner(System.in);

		System.out.print("\tTo book a room, please first input a hotel ID: ");
		int user_hotel_id = scanner.nextInt();
		
		System.out.print("\tPlease enter a room number: ");
		int user_room_number = scanner.nextInt();

		System.out.print("\tPlease enter a date you would like to book the room for: ");
		String user_date = in.readLine();

		if(isValidDate(user_date) == false) {
			System.out.print("\tInvalid date. Please enter a date in the format MM/dd/yyyy or M/dd/yyyy.\n");
			return;
		}

		String query = String.format("SELECT price\n" +
						"FROM Rooms\n" +
						"WHERE Rooms.HotelID = '%d' AND Rooms.roomNumber = '%d' AND Rooms.roomNumber NOT IN (\n" +
							"SELECT roomNumber\n" +
							"FROM RoomBookings\n" +
							"WHERE hotelID = '%d' AND RoomBookings.roomNumber = '%d' AND bookingDate = '%s');", user_hotel_id, user_room_number, 
							user_hotel_id, user_room_number, user_date);

		int row_count = esql.executeQueryAndPrintResult(query);
	
		if(row_count == 0) {
			System.out.print("\n\tNo rooms with your specifications found. Please enter an available hotel room for a specific date.\n\n");
		}

		else {
			String update_bookings = String.format("INSERT INTO RoomBookings (customerID, hotelID, roomNumber, bookingDate) VALUES('%d', '%d', '%d', '%s');", user_id, user_hotel_id, user_room_number, user_date);
			esql.executeUpdate(update_bookings);

			String checking_query = String.format("SELECT * FROM RoomBookings");
			int check_rows = esql.executeQueryAndPrintResult(checking_query);
		}
	
	}
   	
	catch(Exception e) {
		System.err.println(e.getMessage());
	}


   }




   public static void viewRecentBookingsfromCustomer(Hotel esql, int UserID) {
	try {
		String query = String.format("SELECT RB.hotelID, RB.roomNumber, R.price, RB.bookingDate\n" +
						"FROM RoomBookings RB\n" +
						"INNER JOIN Rooms R ON RB.hotelID = R.hotelID AND RB.roomNumber = R.roomNumber\n" +
						"WHERE RB.customerID = '%d'\n" +
						"ORDER BY RB.bookingDate DESC LIMIT 5;", UserID);
		int row_count = esql.executeQueryAndPrintResult(query);
				
	
	
	}


	catch(Exception e) {
		System.err.println(e.getMessage());
	}	
   
   }



   public static void updateRoomInfo(Hotel esql, int UserID) {
	try{
		//System.out.println("\n\nUPDATE ROOM INFO\n\n");
		//System.out.print("\n\nUSER ID: " + UserID);
		if(!checkIfManager(esql, UserID)) {
			System.out.print("\n\nPermission DENIED. Not a manager.\n\n");
			return;
		}

		Scanner scanner = new Scanner(System.in);
		
		System.out.print("\tTo update room information, please first input a hotel ID: ");
		int mgmr_hotel_id = scanner.nextInt();

		if(!checkIfManagesHotel(esql, UserID, mgmr_hotel_id)) {
			System.out.print("\n\nPermission DENIED. You do not manage this hotel.\n\n");
			return;
		}

		System.out.print("\tPlease enter a room number: ");
		int mgmr_room_number = scanner.nextInt();
		

		System.out.print("\tPlease enter a price to set the room to: ");
		int mgmr_price = scanner.nextInt();
		
		System.out.print("\tPlease enter a image URL to set the room to: ");
		String mgmr_image_url = in.readLine();

		String update_room_command = String.format("UPDATE Rooms\n" +
							   "SET price = '%d', imageURL = '%s'\n" +
							   "WHERE hotelID = '%d' AND roomNumber = '%d';", mgmr_price, mgmr_image_url, mgmr_hotel_id, mgmr_room_number);

		esql.executeUpdate(update_room_command);
		updateChangeLogs(esql, UserID, mgmr_hotel_id, mgmr_room_number);

		int row_cnt = esql.executeQueryAndPrintResult("SELECT * FROM Rooms ORDER BY hotelID ASC, roomNumber ASC");
		System.out.println("\n\n------------------\n\n");
		row_cnt = esql.executeQueryAndPrintResult("SELECT * FROM RoomUpdatesLog");
	}

	catch (Exception e) {
		System.err.println(e.getMessage());
	}

   }

   public static boolean checkIfManager(Hotel esql, int UserID) {
   	try{
		//System.out.println("USER ID as int: " + UserID);
		//System.out.print("UserID as String: " + Integer.toString(UserID) + "\n\n");
		String query_manager = String.format("SELECT * FROM Users WHERE userID = '%s' AND (userType = 'manager' OR userType = 'admin');", Integer.toString(UserID));

		int row_count = esql.executeQueryAndPrintResult(query_manager);


		if(row_count > 0) {
			return true;
		}

		else {
			return false;
		}

	}

	catch(Exception e) {
		System.err.println(e.getMessage());
		return false;
	}
   
   }

   public static boolean checkIfManagesHotel(Hotel esql, int managerID, int hotelID) {
	try{
	   
		String query_hotel_mgmr = String.format("SELECT hotelName FROM Hotel WHERE managerUserID = '%s' AND hotelID = '%d';", Integer.toString(managerID), hotelID);
		
		int row_count = esql.executeQuery(query_hotel_mgmr);

		if(row_count > 0) {
			return true;
		}

		else {
			return false;
		}
	}

	catch(Exception e) {
		System.err.println(e.getMessage());
		return false;
	}
   }

   public static void updateChangeLogs(Hotel esql, int managerID, int hotelID, int roomNumber) {
   	try{
		String currentDateAndTime = getCurrTime();
		String update_log_stmt = String.format ("INSERT INTO RoomUpdatesLog (managerID, hotelID, roomNumber, updatedOn)\n" +
							"VALUES('%s', '%d', '%d', '%s');", Integer.toString(managerID), hotelID, roomNumber, currentDateAndTime);

		esql.executeUpdate(update_log_stmt);
	}

	catch (Exception e) {
		System.err.println(e.getMessage());
	}
   }


   public static String getCurrTime() {
   	LocalDateTime currentDateTime = LocalDateTime.now();
	DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	String formattedDateTime = currentDateTime.format(dateTimeFormatter);

	return formattedDateTime;
   }

   public static void viewRecentUpdates(Hotel esql, int managerID) {
   	try {
		if(!checkIfManager(esql, managerID)) {
			System.out.print("\n\nPermission DENIED. Not a manager. \n\n");
			return;
		}

		Scanner scanner = new Scanner(System.in);
		//System.out.print("\tTo view recent updates, please enter the hotel ID of the hotel you manage: ");
		//int mgmr_hotel_id = scanner.nextInt();

		String query = String.format("SELECT *\n" +
					     "FROM RoomUpdatesLog\n" +
					     "WHERE  managerID = '%s'\n" +
					     "ORDER BY updateNumber DESC LIMIT 5;", Integer.toString(managerID));

		int row_count = esql.executeQueryAndPrintResult(query);
	
	}

	catch(Exception e) {
		System.err.println(e.getMessage());
	}
   
   
   
   }
   public static void viewBookingHistoryofHotel(Hotel esql, int userID) {
      try {
	    if(!checkIfManager(esql, userID)){
		System.out.println("You are not a manager, you do not have access to this option.\n");
	    	return;
	    }	
            System.out.print("\tDo you want to input a date range? (y | n): ");
            String choice = in.readLine();

            while(!choice.equals("y") && !choice.equals("n")){
               System.out.println("\tNot a valid input.");
               System.out.print("\tDo you want to input a date range? (y | n): ");
               choice = in.readLine();
            }

            String query = "";
            if(choice.equals("y")){
               	System.out.print("\tPlease enter the beginning Date Range: ");
		String date_start = in.readLine();
		
               if (isValidDate(date_start) == false) {
                  System.out.print("\tInvalid date. Please enter a date in the format MM/dd/yyyy or M/dd/yyyy.\n");
                  return;
               }

               System.out.print("\tPlease enter the Ending Date Range: ");
	       String date_end = in.readLine();
		
               if (isValidDate(date_end) == false) {
                  System.out.print("\tInvalid date. Please enter a date in the format MM/dd/yyyy or M/dd/yyyy.\n");
                  return;
               }

		//System.out.println(date_start);
		//System.out.println(date_end);
	       query = String.format("SELECT R.bookingID, R.hotelID, R.roomNumber, R.bookingDate "+
					"FROM RoomBookings R, Users U " +
					"WHERE U.userID = R.customerID AND R.bookingDate >= DATE '%s' AND R.bookingDate <= DATE '%s' AND R.hotelID IN (SELECT H2.hotelID " +
											"FROM Hotel H2 " +
											"WHERE H2.managerUserID = %s)", date_start, date_end, userID);
		//esql.executeQueryAndPrintResult(query);
	       			
            }else{
               query = String.format("SELECT R.bookingID, R.hotelID, R.roomNumber, R.bookingDate " +
                                    "FROM RoomBookings R, Users U " +
                                    "WHERE U.userID = R.customerID AND R.hotelID IN (SELECT H2.hotelID " + 
                                                                                    "FROM Hotel H2 " +
                                                                                    "WHERE H2.managerUserID = %s)", userID);
            }
		esql.executeQueryAndPrintResult(query);
	    
      }catch(Exception e){
         System.out.println(e.getMessage());
      }
   }
	
   public static void viewRegularCustomers(Hotel esql, int userID) {
      try {
	 if(!checkIfManager(esql, userID)){
		System.out.println("\tYou do not have access to this option.");
		return;
	}
         System.out.print("\tEnter Hotel ID: ");
    	 Scanner scanner = new Scanner(System.in);
	 int hotelID = scanner.nextInt();
	
	//Check if hotel exists
	String checkHotel = String.format("SELECT COUNT(*) FROM Hotel H WHERE H.hotelID = %s", hotelID);
	int numRows = esql.executeQuery(checkHotel);
	 if(numRows == 0){
		System.out.println("\tThis hotel does not exist.\n");
		return;
	 }

	//check if manages hotel
         if(!checkIfManagesHotel(esql, userID, hotelID)){
            System.out.println("\tYou do not manage that hotel.\n");
            return;
         }

         String query = String.format("SELECT R.customerID " +
                                       "FROM RoomBookings R " + 
                                       "GROUP BY R.customerID " +
                                       "ORDER BY COUNT(R.customerID) DESC " +
                                       "LIMIT 5");
	esql.executeQueryAndPrintResult(query);	
      }catch(Exception e){
         System.out.println(e.getMessage());
      }
   }
   public static void placeRoomRepairRequests(Hotel esql, int userID) {
	try {
	 
 	 if(!checkIfManager(esql, userID)){
	 	System.out.println("\tYou are not a manager.\n");
		return;
	 }        

	 //hotelID, roomNumber, companyID
         System.out.print("\tEnter Hotel ID: ");
    	 Scanner scanner = new Scanner(System.in);
	 int hotelID = scanner.nextInt();

	 //Check if this hotel exists and if the user manages this hotel
	 String checkHotel = String.format("SELECT COUNT(*) FROM Hotel H WHERE H.hotelID = %s", hotelID);
	 int numRows = esql.executeQuery(checkHotel);

	 if(numRows == 0 || !checkIfManagesHotel(esql, userID, hotelID)){
	 	System.out.println("\tHotel either does not exist or you do not manage this hotel.\n");
	 	return;
	 }
	
         System.out.print("\tEnter Room Number: ");
         int roomNumber = scanner.nextInt();
 	 
	 String checkRoom = String.format("SELECT COUNT(*) FROM Rooms R WHERE R.hotelID = %s AND R.roomNumber = %s", hotelID, roomNumber);
	 numRows = esql.executeQuery(checkRoom);

	 if(numRows == 0){
	 	System.out.println("\tThis room does not exist for this Hotel.\n");
	 	return;
	 }
	 
         System.out.print("\tEnter Company ID: ");
         int companyID = scanner.nextInt();

	 String checkCompany = String.format("SELECT COUNT(*) FROM MaintenanceCompany M WHERE M.companyID = %s", companyID);
	 numRows = esql.executeQuery(checkCompany);

	 if(numRows == 0){
	 	System.out.println("\tCompany does not exist.\n");
	 	return;
	 }
         //LocalDate currentDate = LocalDate.now(); 
	 //DateFormatter dateFormatter = DateFormatter.ofPattern("MM/dd/yyyy");
         //String day = currentDate.format(dateFormatter);
	
	 String day = getCurrTime();
         String query = String.format("INSERT INTO RoomRepairs VALUES (DEFAULT, %s, %s, %s, '%s')", companyID, hotelID, roomNumber, day);
	 esql.executeUpdate(query);
	
         String temp = String.format("SELECT MAX(R.repairID) FROM RoomRepairs R");
	 List<List<String>> temp2 = esql.executeQueryAndReturnResult(temp);
	 query = String.format("INSERT INTO RoomRepairRequests VALUES (DEFAULT, %s, %s)", userID, temp2.get(0).get(0));
										 //"FROM RoomRepairs R)", userID); 
										 //"WHERE R.repairID >= ALL R.repairID)", userID);
         esql.executeUpdate(query);
      }catch (Exception e) {
 	 System.err.println(e.getMessage());
      }

   }

   public static void viewRoomRepairHistory(Hotel esql, int userID) {
      try{
	 if(!checkIfManager(esql, userID)){
	 	System.out.println("\tYou are not a Manager.\n");
		return;
	 }

         System.out.print("\tWhat is the Hotel ID? ");
    	 Scanner scanner = new Scanner(System.in);
	 int hotelID = scanner.nextInt();

	 String checkHotel = String.format("SELECT COUNT (*) FROM Hotel H WHERE H.hotelID = %s", hotelID);
	 int numRows = esql.executeQuery(checkHotel);

 	 if(numRows == 0 || !checkIfManagesHotel(esql, userID, hotelID)){
		System.out.println("\tHotel either does not exist or you do not manage this hotel.\n");
		return;
	 }
		
         String query = String.format("SELECT R.companyID, R.hotelID, R.roomNumber, R.RepairDate FROM RoomRepairs R, Hotel H WHERE R.hotelID = H.hotelID AND managerUserID = %s", userID);
         esql.executeQueryAndPrintResult(query);
      }catch (Exception e){
         System.err.println(e.getMessage());
      }

   }

}//end Hotel
