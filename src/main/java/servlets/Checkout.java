package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import util.JDBCConnector;
import util.Product;

/**
 * Servlet implementation class Home
 */
@WebServlet("/checkout")
public class Checkout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Checkout() {
        super();
    }

    /**
     * Sends the client to farm_homepage.html
     * */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int user_id = Integer.parseInt(request.getParameter("user_id"));
		System.out.println("read user_id: " + user_id);
		
		JDBCConnector db = new JDBCConnector();
		int status = db.buyCart(user_id);
					
		PrintWriter out = response.getWriter();
		if (status < 0) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			String error = "Error: ";
			
			if (status == -1) {
				error += "insufficient funds. Please add more money to purchase.";
			}
			else if (status == -2) {
				error += "item(s) out of stock. Please wait until vendors have restocked to purchase.";
			}
			else if (status == -3 || status == -4) {
				error += "cart is empty. Please add products to checkout.";
			}
			else if (status == -5) {
				error += "database error occurred.";
			}
			
			out.print(error);
			out.flush();
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			out.print("success");
			out.flush();
		}
		
	}
}
