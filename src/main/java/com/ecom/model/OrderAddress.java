package com.ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

//Данный класс представляет собой модель адреса доставки заказа

//Аннотации Lombok:
@Data   // Комплексная аннотация: генерирует геттеры, сеттеры, toString, equals, hashCode
@Entity // Помечает класс как JPA сущность (таблица в БД)
public class OrderAddress {

	// 1️. ПЕРВИЧНЫЙ КЛЮЧ
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Стратегия генерации ID: IDENTITY = автоинкремент в БД
	private Integer id;

	// 2️. ИНФОРМАЦИЯ О ПОЛУЧАТЕЛЕ
	
	private String firstName; // Имя получателя

	private String lastName;  // Фамилия получателя

	private String email;     // Email получателя 

	private String mobileNo;  // Мобильный телефон 

	// 3️. АДРЕС ДОСТАВКИ
	
	private String address;   // Улица, дом, квартира

	private String city;      // Населенный пункт

	private String region;    // Область/край/регион

	private String pincode;   // Почтовый индекс 

}