SELECT p.vendor_id, SUM(c.quantity * p.price) AS vendor_profit 
FROM BirdsAndBees.Carts c JOIN BirdsAndBees.Products p ON c.product_id = p.product_id
WHERE c.user_id = 1
GROUP BY p.vendor_id;

SELECT *
FROM (
	SELECT p.vendor_id, SUM(c.quantity * p.price) AS vendor_profit 
	FROM BirdsAndBees.Carts c JOIN BirdsAndBees.Products p ON c.product_id = p.product_id
	WHERE c.user_id = 1
	GROUP BY p.vendor_id
) AS vp
JOIN BirdsAndBees.Users ON Users.user_id = vp.vendor_id;

UPDATE
(
	SELECT p.vendor_id, SUM(c.quantity * p.price) AS vendor_profit 
	FROM BirdsAndBees.Carts c JOIN BirdsAndBees.Products p ON c.product_id = p.product_id
	WHERE c.user_id = 1 AND c.purchased = false
	GROUP BY p.vendor_id
) AS vp
JOIN BirdsAndBees.Users ON Users.user_id = vp.vendor_id
SET current_profit = current_profit + vendor_profit;

UPDATE BirdsAndBees.Products p
JOIN BirdsAndBees.CARTS c ON p.product_id = c.product_id
SET p.quantity_available = p.quantity_available - c.quantity
WHERE c.user_id = 1 AND c.purchased = false;




