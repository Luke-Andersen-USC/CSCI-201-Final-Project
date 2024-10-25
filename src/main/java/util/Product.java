package util;

/**
 * Product class. 
 * NOTE: quantity is either quantity_available (stock)
 * or quantity in a user's cart depending on context.
 */
public class Product {
	public int product_id;
	public String name;
	public double price;
	public int vendor_id;
	public String image_url;
	public int quantity;

	public Product(int product_id, String name, double price, int vendor_id, String image_url, int quantity) {
		this.product_id = product_id;
		this.name = name;
		this.price = price;
		this.vendor_id = vendor_id;
		this.image_url = image_url;
		this.quantity = quantity;
	}
	
	public Product(String name, double price, int vendor_id, String image_url, int quantity) {
		this.name = name;
		this.price = price;
		this.vendor_id = vendor_id;
		this.image_url = image_url;
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Product [product_id=" + product_id + ", name=" + name + ", price=" + price + ", vendor_id=" + vendor_id
				+ ", image_url=" + image_url + ", quantity=" + quantity + "]";
	}

}
