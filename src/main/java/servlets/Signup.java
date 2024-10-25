package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import util.JDBCConnector;
import util.User;

/**
 * Servlet implementation class Home
 */
@WebServlet("/signup")
public class Signup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Signup() {
        super();
    }

    /**
     * Sends the client to farm_signup.html
     * */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.getRequestDispatcher("farm_signup.html").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String account_type_string = request.getParameter("account_type");
		int account_type = 0;
		
		if (account_type_string != null && !account_type_string.isEmpty()) {
			account_type = Integer.parseInt(account_type_string);
		} else {
			account_type = 0; //default buyer type
		}
		
		
		System.out.println("read username: " + username);
		System.out.println("read password: " + password);
		System.out.println("read account-type: " + account_type);
		
		JDBCConnector db = new JDBCConnector();
		int status = db.insertNewUser(new User(username, password, account_type));
		
		PrintWriter out = response.getWriter();
		
		if (status < 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Username already exists";
			out.print(error);
			out.flush();
		}
		else {
			User user = db.getUserInfo(db.loginUser(username, password));
			Gson gson = new Gson();
			String userJSON = gson.toJson(user);
			
			response.setStatus(HttpServletResponse.SC_OK);
			out.print(userJSON);
			out.flush();
		}
		
	}
}
