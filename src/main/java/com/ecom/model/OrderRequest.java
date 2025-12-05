package com.ecom.model;

import lombok.Data;
import lombok.ToString;

//Данный класс представляет собой модель Data Transfer Object (DTO) для оформления заказа 

//Аннотации Lombok:
@ToString   // Генерирует toString() метод для логгирования/отладки
@Data       // Генерирует геттеры, сеттеры, equals, hashCode
public class OrderRequest { // Это Data Transfer Object (DTO) - объект для передачи данных между слоями, также это НЕ сущность JPA! НЕ сохраняется в БД напрямую.

	// 1️. ДАННЫЕ АДРЕСА ДОСТАВКИ (такие же как в OrderAddress)
	private String firstName;  // Имя получателя

	private String lastName;   // Фамилия получателя

	private String email;      // Email 

	private String mobileNo;   // Мобильный телефон

	private String address;    // Улица, дом, квартира

	private String city;       // Населенный пункт

	private String region;     // Регион/область/край

	private String pincode;    // Почтовый индекс
	
	// 2️. ⭐ НОВОЕ ПОЛЕ: СПОСОБ ОПЛАТЫ
	private String paymentType; 

}
