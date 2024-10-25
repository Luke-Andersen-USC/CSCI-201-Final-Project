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
import util.Product;

/**
 * Servlet implementation class Home
 */
@WebServlet("/vendor")
public class ProfileVendor extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ProfileVendor() {
        super();
    }

    /**
     * Sends the client to farm_homepage.html
     * */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.getRequestDispatcher("farm_homepage.html").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		String name = request.getParameter("name");
		int price = Integer.parseInt(request.getParameter("price"));
		String image_url = request.getParameter("image");
		int quantity = Integer.parseInt(request.getParameter("quantityAvailable"));
		int vendor_id = Integer.parseInt(request.getParameter("user_id"));
		
		Product product = new Product(name, price, vendor_id, image_url, quantity);
		JDBCConnector db = new JDBCConnector();
		int status = db.addProduct(product);
		
		PrintWriter out = response.getWriter();
		
		if (status < 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Product Exists";
			out.print(error);
			out.flush();
		}
		else {
//			User user = db.getUserInfo(db.loginUser(username, password));
			Gson gson = new Gson();
			String userJSON = gson.toJson(product);
			
			response.setStatus(HttpServletResponse.SC_OK);
			out.print(userJSON);
			out.flush();
		}
	}
}
