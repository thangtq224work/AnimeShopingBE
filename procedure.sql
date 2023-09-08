USE animeshop;
drop procedure if exists getTopSellProduct;
DELIMITER $$
CREATE PROCEDURE animeshop.getTopSellProduct(
IN pTop INT,
IN pFrom datetime,
IN pTo datetime	
)
BEGIN
	IF pFrom is not null and pTo is null THEN
		select animeshop.order_detail.product_id as 'pId',animeshop.product.name as 'pName',sum(animeshop.order_detail.quantity) as 'quantity' from animeshop.order_detail inner join
		animeshop.order on animeshop.order_detail.order_id = animeshop.order.id  inner join 
        animeshop.product on animeshop.order_detail.product_id = animeshop.product.id
        where animeshop.order.create_at >= pFrom
		group by animeshop.order_detail.product_id order by sum(animeshop.order_detail.quantity) desc limit pTop;
    ELSEIF pFrom is not null AND pTo is not null THEN
		select animeshop.order_detail.product_id as 'pId',animeshop.product.name as 'pName',sum(animeshop.order_detail.quantity) as 'quantity' from animeshop.order_detail inner join
		animeshop.order on animeshop.order_detail.order_id = animeshop.order.id  inner join 
        animeshop.product on animeshop.order_detail.product_id = animeshop.product.id
        where animeshop.order.create_at between pFrom AND pTo
		group by animeshop.order_detail.product_id order by sum(animeshop.order_detail.quantity) desc limit pTop;
    ELSE 
		select animeshop.order_detail.product_id as 'pId',animeshop.product.name as 'pName',sum(animeshop.order_detail.quantity) as 'quantity' from animeshop.order_detail inner join
		animeshop.order on animeshop.order_detail.order_id = animeshop.order.id  inner join 
        animeshop.product on animeshop.order_detail.product_id = animeshop.product.id
		group by animeshop.order_detail.product_id order by sum(animeshop.order_detail.quantity) desc limit pTop;
    END IF;
END $$ 
DELIMITER ;







call getTopSellProduct(3,null,null)