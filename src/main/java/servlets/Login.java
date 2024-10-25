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
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Login() {
        super();
    }
    
    /**
     * Sends the client to farm_login.html
     * */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.getRequestDispatcher("farm_login.html").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		System.out.println("read username: " + username);
		System.out.println("read password: " + password);
		
		JDBCConnector db = new JDBCConnector();
		int user_id = db.loginUser(username, password);
		
		PrintWriter out = response.getWriter();
		
		if (user_id < 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Username/password incorrect";
			out.print(error);
			out.flush();
		}
		else {
			User user = db.getUserInfo(user_id);
			Gson gson = new Gson();
			String userJSON = gson.toJson(user);
			System.out.println("sending user object: " + userJSON);
			response.setStatus(HttpServletResponse.SC_OK);
			out.print(userJSON);
			out.flush();
		}
	}
}
