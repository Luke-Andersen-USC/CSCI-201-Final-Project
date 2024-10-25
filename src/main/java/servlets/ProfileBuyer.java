package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.JDBCConnector;

/**
 * Servlet implementation class Home
 */
@WebServlet("/buyer")
public class ProfileBuyer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ProfileBuyer() {
        super();
    }

    /**
     * Sends the client to farm_homepage.html
     * */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("farm_homepage.html").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		getUserBalance(int user_id()

//		String action = request.getParameter("action");
		int user_id = Integer.parseInt(request.getParameter("user_id"));
		double amount_added = Integer.parseInt(request.getParameter("amount_added"));
		
//		System.out.println("read action: " + action);
		System.out.println("read user_id: " + user_id);
		System.out.println("read amount_added: " + amount_added);
		
		JDBCConnector db = new JDBCConnector();
		int status = 0;
		
		double status_og = db.getUserBalance(user_id);
		if (status_og < 0) {
			status = -1;
		}
		else {
			status = db.setUserBalance(user_id, amount_added + status_og);
		}
	

		PrintWriter out = response.getWriter();
		if (status < 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Bad Add/Remove";
			out.print(error);
			out.flush();
		}
		else {
//			User user = db.getUserInfo(user_id);
//			Gson gson = new Gson();
//			String userJSON = gson.toJson(user);
			response.setStatus(HttpServletResponse.SC_OK);
			out.print("Succesful Add/Remove");
			out.flush();
		}
	}
}
