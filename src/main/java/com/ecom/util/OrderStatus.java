package com.ecom.util;

public enum OrderStatus {

	IN_PROGRESS(1, "В обработке"), 
	ORDER_RECIVED(2, "Зазаз получен"), 
	PRODUCT_PACKED(3, "Товар упакован"),
	OUT_FOR_DELIVERY(4, "Отправлен в доставку"), 
	DELIVERED(5, "Доставлен"),
	CANCEL(6,"Отменен");

	private Integer id;

	private String name;

	private OrderStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}