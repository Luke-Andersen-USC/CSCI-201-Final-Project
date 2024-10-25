package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.User;

public class JDBCConnector {
	
	private static Connection connection = null;
	private static String mySQLusername = "root", mySQLpassword = "password";
	
	/**
	 * See src/main/sql/DATABASE_SETUP_INSTRUCTIONS.txt for how to setup the database
	 * and get it to work on your computer with this code.
	 */
	public JDBCConnector() {
		
		try {
			if (connection == null) {				
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://localhost/BirdsAndBees?user=" + mySQLusername + "&password=" + mySQLpassword);
				System.out.println("Successfully connected to the database");
			}
		} catch (ClassNotFoundException e) {
	        System.out.println("Couldn't read MySQL Driver class. Make sure the JAR is included in webapp/WEB-INF/lib: " + e.getMessage());
	        e.printStackTrace();
	    } catch (SQLException e) {
	        System.out.println("SQLException occurred when trying to connect to the database: " + e.getMessage());
	        e.printStackTrace();
	    }
		
	}
	
	public void close() {
		try {
			connection.close();
			connection = null;
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}
	}
	
	/**
	 * Inserts a new user into the database.
	 * 
	 * @return 0 if successful, -1 if username already exists
	 */
	public int insertNewUser(User user) {

		int status = 0;

		try {
			String sql = "INSERT INTO Users (username, password, account_type) VALUES (?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, user.username);
			statement.setString(2, user.password);
			statement.setInt(3, user.account_type);
			statement.executeUpdate();
			
			status = loginUser(user.username, user.password);
		} 
		
		catch (SQLException e) {
			// handle duplicate entries
			String message = e.getMessage();
			if (message.contains("Duplicate entry")) {
				status = -1;
			} 
			else {
				// other problem occurred
				System.out.println("SQLException: " + e.getMessage());
			}
		}
		return status;
	} 
	
	/**
	 * Returns user_id if successful, -1 if username doesn't exist, -2 if
	 * password is incorrect
	 */
	public int loginUser(String username, String password) {
		int status = 0;
		
		try {
			String sql = "SELECT user_id, password FROM Users WHERE username=?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, username);

			ResultSet results = statement.executeQuery();

			if (!results.next()) { // username does not exist
				status = -1;
			} 
			else if (!results.getString(2).equals(password)) { // password is wrong
				status = -2;
			} 
			else {
				status = results.getInt(1); // status = userID
			}
		} 
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}
		return status;
	}
	
	/**
	 * Returns a User object with all user info
	 */
	public User getUserInfo(int user_id) {
		User user = new User();
		
		try {
			String sql = "SELECT * FROM Users WHERE user_id=?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			ResultSet results = statement.executeQuery();
			results.next();
			user = new User(results.getInt(1),
							results.getString(2),
							results.getString(3),
							results.getInt(4),
							results.getDouble(5),
							results.getDouble(6),
							results.getInt(7));
		}
		catch (SQLException e) {
			System.out.println("SQLException in getUserInfo(" + user_id + "): " + e.getMessage());
		}
		return user;
	}
	
	/**
	 * Returns a Product object with all product info
	 */
	public Product getProductInfo(int product_id) {
		Product product = null;
		
		try {
			String sql = "SELECT * FROM Products WHERE product_id=?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, product_id);
			ResultSet results = statement.executeQuery();
			if(!results.next()) {
				return null;
			}
			product = new Product(results.getInt(1),
								results.getString(2),
								results.getDouble(3),
								results.getInt(4),
								results.getString(5),
								results.getInt(6));
		}
		catch (SQLException e) {
			System.out.println("SQLException in getProductInfo(" + product_id + "): " + e.getMessage());
		}
		return product;
	}

	/**
	 * Returns the user's balance, or -1 if error
	 * */
	public double getUserBalance(int user_id) {
		double status = 0;
		
		try {
			String sql = "SELECT current_balance FROM Users WHERE user_id=?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);

			ResultSet results = statement.executeQuery();

			if (!results.next()) { // user_id does not exist
				status = -1;
			} 
			else {
				status = results.getDouble(1); // status = current_balance
			}
		} 
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			status = -1;
		}
		return status;
	}
	
	/**
	 * Sets user's balance. Returns 0 if success, -1 if error
	 * */
	public int setUserBalance(int user_id, double new_balance) {
		int status = 0;
		
		try {
			String sql = "UPDATE Users SET current_balance = ? WHERE user_id = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setDouble(1, new_balance);
			statement.setInt(2, user_id);
			statement.executeUpdate();
		} 
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			status = -1;
		}
		return status;
	}
	
	/**
	 * Adds product into the database. Returns 0 if successful, -1 if error
	 * */
	public int addProduct(Product p) {
		int status = 0;
		
		try {
			String sql = "INSERT INTO Products (name, price, vendor_id, image_url, quantity_available) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, p.name);
			statement.setDouble(2, p.price);
			statement.setInt(3, p.vendor_id);
			statement.setString(4, p.image_url);
			statement.setInt(5, p.quantity);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			status = -1;
		}
		return status;
	}
	
	/**
	 * Removes product from the database. Returns 0 if successful, -1 if error
	 * */
	public int removeProduct(int product_id) {
		int status = 0;
		
		try {
			String sql = "DELETE FROM Products WHERE product_id = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, product_id);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			status = -1;
		}
		return status;
	}
	
	/**
	 * Returns list of vendor's products (empty if none)
	 */
	public List<Product> getVendorProducts(int vendor_id) {
		List<Product> products = new ArrayList<Product>();
		
		try {
			String sql = "SELECT * FROM Products WHERE vendor_id=?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, vendor_id);
			
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				Product p = new Product(results.getInt(1),
										results.getString(2),
										results.getDouble(3),
										results.getInt(4),
										results.getString(5),
										results.getInt(6));
				
				products.add(p);
			}
		}
		catch (SQLException e) {
			System.out.println("SQLException occurred: " + e.getMessage());
		}
		
		return products;
	}
	
	/**
	 * Returns list of all products (empty if none)
	 */
	public List<Product> getAllProducts() {
		List<Product> products = new ArrayList<Product>();
		
		try {
			String sql = "SELECT * FROM Products";
			PreparedStatement statement = connection.prepareStatement(sql);			
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				Product p = new Product(results.getInt(1),
										results.getString(2),
										results.getDouble(3),
										results.getInt(4),
										results.getString(5),
										results.getInt(6));
				
				products.add(p);
			}
		}
		catch (SQLException e) {
			System.out.println("SQLException occurred: " + e.getMessage());
		}
		
		return products;
	}
	
	public List<Product> getUserPurchasedProducts(int user_id) {
		List<Product> purchasedProducts = new ArrayList<Product>();
		
		
	}
	
	/**
	 * Adds product to user's cart (or adds quantity to the current quantity if the product is
	 * already in user's cart). Returns 0 if success, -1 if error
	 * */
	public int addProductToCart(int user_id, int product_id, int quantity) {
		int status = 0;
		
		try {
			String sql = "INSERT INTO Carts (user_id, product_id, quantity, purchased) "
					   + "VALUES (?, ?, ?, ?) "
					   + "ON DUPLICATE KEY UPDATE Carts.quantity = Carts.quantity + ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			statement.setInt(2, product_id);
			statement.setInt(3, quantity);
			statement.setBoolean(4, false);
			statement.setInt(5, quantity);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			e.printStackTrace();
			status = -1;
		}
		return status;
	}
	
	/**
	 * Removes product from user's cart. Returns 0 if success, -1 if error
	 */
	public int removeProductFromCart(int user_id, int product_id) {
		int status = 0;
		
		try {
			String sql = "DELETE FROM Carts WHERE user_id = ? AND product_id = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			statement.setInt(2, product_id);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			status = -1;
		}
		return status;
	}
	
	/**
	 * Returns a list of all Products in a users cart (empty if none)
	 */
	public List<Product> getUserCart(int user_id) {
		List<Product> products = new ArrayList<Product>();
		
		try {
			String sql = "SELECT p.product_id, p.name, p.price, p.vendor_id, p.image_url, c.quantity "
					   + "FROM Products p JOIN Carts c ON p.product_id = c.product_id "
					   + "WHERE c.user_id = ? AND c.purchased = 0";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				Product p = new Product(results.getInt(1),
										results.getString(2),
										results.getDouble(3),
										results.getInt(4),
										results.getString(5),
										results.getInt(6));
				
				products.add(p);
			}
		}
		catch (SQLException e) {
			System.out.println("SQLException occurred: " + e.getMessage());
			e.printStackTrace();
		}
		
		return products;
	}
	
	/**
	 * Buys all items in the user's cart. 
	 * First, it:
	 *  - Checks if cart is empty
	 *  - Checks user's balance
	 *  - Checks the total cost of the cart
	 *  - Checks if all items are in stock
	 * If there are sufficient funds: 
	 *  - Updates user's current_balance
	 *  - Updates user's products_purchased
	 *  - Updates vendor's current_profit
	 *  - Updates product's quantity_available
	 *  - Updates all items in user's cart to purchased = true
	 *  
	 *  @return 0 if successful, -1 if insufficient funds, -2 if insufficient stock,
	 *  -3 if invalid user_id (or the user's cart is empty), -4 if cart is empty,
	 *  -5 if other error occurred.
	 */
	public int buyCart(int user_id) {
		int status = 0;
		
		try {
			// check user's current balance
			String sql = "SELECT current_balance FROM Users WHERE user_id = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			ResultSet results = statement.executeQuery();
			
			double current_balance;
			if (results.next()) {
				current_balance = results.getDouble(1);
			}	
			else {
				return -3;
			}
			
			// check if cart is empty
			sql = "SELECT * FROM Carts WHERE user_id = ? AND purchased = false";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			results = statement.executeQuery();
			if (!results.next()) { // cart is empty
				return -4;
			}
			
			// get total cost of cart
			sql = "SELECT SUM(c.quantity * p.price) "
				+ "FROM Products p JOIN Carts c ON p.product_id = c.product_id "
				+ "WHERE c.user_id = ? AND c.purchased = false";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			results = statement.executeQuery();
			
			
			double total_cost;
			if (results.next()) {
				total_cost = results.getDouble(1);
			}
			else {
				return -3;
			}
			
			if (total_cost > current_balance) {
				return -1;
			}
			
			// count the number of rows where the quantity requested is more than the current stock
			sql = "SELECT COUNT(*) "
				+ "FROM Products p JOIN Carts c ON p.product_id = c.product_id "
				+ "WHERE c.user_id = ? AND c.purchased = false AND c.quantity > p.quantity_available";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			results = statement.executeQuery();
			results.next();
			if (results.getInt(1) > 0)  {
				return -2;
			}
				
			/**
			 * if we've made it here we've passed all the checks 
			 * */
			
			// update user's balance
			sql = "UPDATE Users SET current_balance = current_balance - ? WHERE user_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setDouble(1, total_cost);
			statement.setInt(2, user_id);
			statement.executeUpdate();
			
			// update the number of products user purchased by first getting the total number of
			// products in the cart
			sql = "SELECT SUM(quantity) FROM Carts WHERE user_id = ? AND purchased = false GROUP BY user_id";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			results = statement.executeQuery();
			results.next();
			int numProductsInCart = results.getInt(1);
			
			System.out.println("items in user's cart: " + numProductsInCart);
			
			sql = "UPDATE Users SET products_purchased = products_purchased + ? WHERE user_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setDouble(1, numProductsInCart);
			statement.setInt(2, user_id);
			statement.executeUpdate();
			
			// update each vendor's profit (cursed SQL statement)
			sql = "UPDATE "
				+ "("
				+ "	  SELECT p.vendor_id, SUM(c.quantity * p.price) AS vendor_profit "
				+ "	  FROM BirdsAndBees.Carts c JOIN BirdsAndBees.Products p ON c.product_id = p.product_id "
				+ "	  WHERE c.user_id = ? AND c.purchased = false "
				+ "	  GROUP BY p.vendor_id "
				+ ") AS vp "
				+ "JOIN BirdsAndBees.Users ON Users.user_id = vp.vendor_id "
				+ "SET current_profit = current_profit + vendor_profit";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			statement.executeUpdate();
			
			// updates the quantity available of each purchased product
			sql = "UPDATE BirdsAndBees.Products p "
				+ "JOIN BirdsAndBees.CARTS c ON p.product_id = c.product_id "
				+ "SET p.quantity_available = p.quantity_available - c.quantity "
				+ "WHERE c.user_id = ? AND c.purchased = false ";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			statement.executeUpdate();
			
			// get all unpurchased items
			sql = "SELECT * FROM Carts WHERE user_id = ? AND purchased = false";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			results = statement.executeQuery();
			
			// update quantities for purchased items
			sql = "INSERT INTO Carts (user_id, product_id, quantity, purchased) "
					   + "VALUES (?, ?, ?, ?) "
					   + "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
			statement = connection.prepareStatement(sql);
			while (results.next()) {
				statement.setInt(1, user_id);
				statement.setInt(2, results.getInt(2));
				statement.setInt(3, results.getInt(3));
				statement.setBoolean(4, true);
				statement.setInt(5, results.getInt(3));
				statement.executeUpdate();
			}
			
			// delete unpurchased items
			sql = "DELETE FROM Carts WHERE user_id = ? AND purchased = false";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			statement.executeUpdate();
			
		}
		catch (SQLException e) {
			System.out.println("SQLException occurred in buyCart(): " + e.getMessage());
			status = -5;
		}
		
		return status;
	}
	
	/***
	 * For testing
	 */
	public static void main(String[] args) {
		JDBCConnector db = new JDBCConnector();
		
//		System.out.println(db.insertNewUser(new User("nate", "password", 0)));
//		System.out.println(db.insertNewUser(new User("jerry", "fish", 0)));
//		System.out.println(db.insertNewUser(new User("farmer_jeff", "ilovecarrots", 1)));
//		
//		int bob = db.insertNewUser(new User("farmer_bob", "cornisthebest", 1));
//		System.out.println(bob);
//		
//		Product carrots = new Product("Carrots", 1.50, 3, "img/carrots", 100);
//		Product lettuce = new Product("Lettuce", 2.50, 3, "img/lettuce", 300);
//		Product corn = new Product("Corn", 3.75, 4, "img/corn", 50);
////		
//		System.out.println(db.addProduct(carrots));
//		System.out.println(db.addProduct(lettuce));
//		System.out.println(db.addProduct(corn));
		
//		List<Product> jeffsProducts = db.getVendorProducts(3);
//		for (Product p : jeffsProducts) {
//			System.out.println(p.toString());
//		}
		
		//db.addProductToCart(2, 2, 40);
		//db.removeProductFromCart(2, 2);
		
		
//		System.out.println(db.setUserBalance(1, 1000.0));
//		System.out.println(db.addProductToCart(1, 7, 4));
//		System.out.println(db.addProductToCart(1, 7, 1));
//		System.out.println(db.addProductToCart(1, 9, 2));
		
//		System.out.println(db.addProductToCart(1, 7, 1));
//		System.out.println(db.buyCart(1));
//		System.out.println(db.addProductToCart(1, 7, 4));
			
//		System.out.println("Elapsed time: " + (endTime - startTime) + " ms");
		
		db.addProductToCart(1, 10, 10);

	}
}
