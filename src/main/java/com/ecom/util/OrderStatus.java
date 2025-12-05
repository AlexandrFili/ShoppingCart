package com.ecom.util;

//ПЕРЕЧИСЛЕНИЕ СТАТУСОВ ЗАКАЗА (ENUM)
//Enum (перечисление) - специальный тип данных для представления фиксированного набора констант
//В данном случае используется для хранения возможных статусов заказа в системе

public enum OrderStatus {

	IN_PROGRESS(1, "В обработке"), 
	ORDER_RECEIVED(2, "Зазаз получен"), 
	PRODUCT_PACKED(3, "Товар упакован"),
	OUT_FOR_DELIVERY(4, "Отправлен в доставку"), 
	DELIVERED(5, "Доставлен"),
	CANCEL(6,"Отменен");

	
	// ПОЛЯ КЛАССА
	// Каждая константа enum имеет два поля:
	private Integer id; 					// Числовой идентификатор статуса (используется в БД)

	private String name;					// Текстовое название статуса на русском языке

	
	// КОНСТРУКТОР ENUM
	// Конструктор enum всегда private (не может быть public или protected)
	// Вызывается автоматически при создании констант выше
	private OrderStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	// ГЕТТЕРЫ И СЕТТЕРЫ
	// Позволяют получать и изменять значения полей констант
	
	public Integer getId() {
		return id; // Возвращает числовой ID статуса
	}

	public void setId(Integer id) {
		this.id = id; // Устанавливает новый ID статуса
	}

	public String getName() {
		return name; // Возвращает текстовое название статуса
	}

	public void setName(String name) {
		this.name = name; // Устанавливает новое название статуса
	}

}