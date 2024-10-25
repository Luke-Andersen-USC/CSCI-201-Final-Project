package util;

public class User {

	public int user_id;
	public String username;
	public String password;
	public int account_type;
	public double current_balance;
	public double current_profit;
	public int products_purchased;

	public User(int user_id, String username, String password, int account_type, double current_balance,
			double current_profit, int products_purchased) {
		this.user_id = user_id;
		this.username = username;
		this.password = password;
		this.account_type = account_type;
		this.current_balance = current_balance;
		this.current_profit = current_profit;
		this.products_purchased = products_purchased;
	}

	public User(String username, String password, int account_type) {
		this.username = username;
		this.password = password;
		this.account_type = account_type;
	}
	
	public User() {
		this.user_id = 0;
		this.username = null;
		this.password = null;
		this.account_type = -1;
		this.current_balance = -1;
		this.current_profit = -1;
		this.products_purchased = -1;
	}

	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", username=" + username + ", password=" + password + ", account_type="
				+ account_type + ", current_balance=" + current_balance + ", current_profit=" + current_profit
				+ ", products_purchased=" + products_purchased + "]";
	}

	

}
