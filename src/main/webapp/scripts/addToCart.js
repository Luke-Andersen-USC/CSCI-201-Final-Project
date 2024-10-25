function renderPage(cart) {
	cart = JSON.parse(cart);
	console.log('cart:', cart);

	cart.forEach(item => {
		addItem(item);
	});

	bindRemoveBtns() //allows us to remove items
	updateTotal(); // Update the total price	

	document.querySelector('#cart-delete-all').onclick = function() {
		document.querySelectorAll('.product-container').forEach(product => {
			const p_id = product.id;
			$.ajax({
				url: 'Cart',
				method: 'POST',
				data: {
					user_id: JSON.parse(sessionStorage.user).user_id,
					action: 'remove',
					product_id: p_id
				},
				success: function() {
					product.remove();
					updateTotal();
				},
				error: function(xhr, status, error) {
					// Handle unsuccessful login (e.g., show an error message)
					console.error(error);
				}
			});
		});
	}
}

function addItem(item) {
	
	console.log('adding item', item);
	
	// Create HTML elements.
	const itemContainer = document.createElement('div')
	const li = document.createElement('li');
	const img = document.createElement('img');
	const spanRemove = document.createElement('span');
	const spanItem = document.createElement('span');
	const spanPrice = document.createElement('span');
	const spanQuantity = document.createElement('span');
	const priceBold = document.createElement('b');

	// Add classes for new elements.
	itemContainer.classList.add('product-container');
	itemContainer.id = item.product_id;
	li.classList.add('list-group-item', 'cart-item');
	img.classList.add('cart-item-image');
	spanPrice.classList.add('price');
	priceBold.classList.add('price-bold');
	spanQuantity.classList.add('span-quantity');
	spanRemove.classList.add('cart-remove', 'oi', 'oi-circle-x');

	// Set attributes and properties.
	spanRemove.title = 'Remove';
	spanItem.innerHTML = item.name;
	img.src = 'img/' + item.image_url;
	img.alt = item.name + ' Product Image';
	spanQuantity.innerHTML = item.quantity;
	priceBold.innerHTML = '$' + item.price;

	// append bold price to span price
	spanPrice.appendChild(priceBold);

	// Add elements to the list item.
	li.appendChild(spanRemove);
	li.appendChild(img);
	li.appendChild(spanItem);
	li.appendChild(spanQuantity);
	li.appendChild(spanPrice);

	//Add list item to the container
	itemContainer.appendChild(li);

	// Add itemContainer to the cart list.
	document.querySelector('#cart-list').appendChild(itemContainer);
}

//adds remove buttons to everything in cart
function bindRemoveBtns() {
	document.querySelectorAll('.cart-remove').forEach(button => {
		button.onclick = function() {

			const p_id = button.parentNode.parentNode.id;
			console.log('')

			// remove product from database
			$.ajax({
				url: 'Cart',
				method: 'POST',
				data: {
					user_id: JSON.parse(sessionStorage.user).user_id,
					action: 'remove',
					product_id: p_id
				},
				success: function() {
					button.parentNode.remove();
					updateTotal();
				},
				error: function(xhr, status, error) {
					// Handle unsuccessful request (e.g., show an error message)
					console.error(error);
					event.preventDefault();
				}
			});	
		} // END removeBtns[i].onclick
	}); // END for loop
} // END bindRemoveBtns()

//updates total price
function updateTotal() {
	let total = 0;
	
	document.querySelectorAll('.cart-item').forEach(product => {
		const price = product.querySelector('.price-bold');
		const quantity = product.querySelector('.span-quantity');
		total += Number(price.innerHTML.slice(1)) * Number(quantity.innerHTML);
	});
	
	document.getElementById('total-amount').innerHTML = `Total: $${total}`;
}
// });