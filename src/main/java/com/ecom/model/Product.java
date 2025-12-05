package com.ecom.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Данный класс представляет собой основную сущность - модель товара

//Lombok аннотации:
@AllArgsConstructor  // Генерирует конструктор со всеми полями
@NoArgsConstructor   // Генерирует конструктор без аргументов (обязателен для JPA)
@Getter              // Генерирует геттеры для всех полей
@Setter              // Генерирует сеттеры для всех полей
@Entity              // Помечает класс как JPA сущность (таблица в БД)
public class Product {
	
	// 1️. ПЕРВИЧНЫЙ КЛЮЧ
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)  // Стратегия генерации ID: IDENTITY = автоинкремент в БД
	private int id;
	
	// 2️. ОСНОВНАЯ ИНФОРМАЦИЯ О ТОВАРЕ
	
	@Column(length = 500) // Указываем максимальную длину VARCHAR(500)
	private String title; // Название товара
	
	@Column(length = 5000) // Указываем максимальную длину VARCHAR(5000)
	private String description; // Описание товара
	
	private String category; // Категория товара (как строка)
	
	 // 3️. ЦЕНА И НАЛИЧИЕ
	
	private Double price; // Базовая цена товара
	
	private int stock; // Количество товара на складе
	
	// 4️. ИЗОБРАЖЕНИЕ И ВИЗУАЛЬНОЕ ПРЕДСТАВЛЕНИЕ
	
	private String image; // Имя файла изображения или URL
	
	// 5️. СКИДКИ И АКЦИИ
	
	private int discount; // Размер скидки в процентах
	
	private Double discountPrice; // Цена со скидкой (вычисляется)
	// Формула: discountPrice = price * (100 - discount) / 100
    // Пример: price=1000, discount=15 → discountPrice=850
	
	private Boolean isActive; // Активен ли товар для продажи
	// true = товар отображается в каталоге
    // false = товар скрыт (но остается в БД)
 
}
