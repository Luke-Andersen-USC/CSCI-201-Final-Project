package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import util.JDBCConnector;
import util.Product;

/**
 * Servlet implementation class Cart
 */
@WebServlet("/Cart")
public class Cart extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Cart() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		int user_id = Integer.parseInt(request.getParameter("user_id"));

		System.out.println("read action: " + action);
		System.out.println("read user_id: " + user_id);

		JDBCConnector db = new JDBCConnector();
		int status = 0;
		PrintWriter out = response.getWriter();

		if (action.equals("get")) {
			List<Product> cart = db.getUserCart(user_id);
			String cartJSON = new Gson().toJson(cart);
			out.write(cartJSON);
			out.flush();
		} else {
			int product_id = Integer.parseInt(request.getParameter("product_id"));
			System.out.println("read product_id: " + product_id);
			if (action.equals("add")) {

				int quantity = Integer.parseInt(request.getParameter("quantity"));
				System.out.println("read quantity: " + quantity);

				status = db.addProductToCart(user_id, product_id, quantity);
			} else {
				status = db.removeProductFromCart(user_id, product_id);
			}

			if (status < 0) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				String error = "Bad Add/Remove";
				out.print(error);
				out.flush();
			} else {
				response.setStatus(HttpServletResponse.SC_OK);
				if (action.equals("add")) {
					Gson gson = new Gson();
					Product product = db.getProductInfo(product_id);
					String productJSON = gson.toJson(product);
					out.print(productJSON);
				} else {
					out.print("ok");
				}
				out.flush();
			}
		}
	}

}
