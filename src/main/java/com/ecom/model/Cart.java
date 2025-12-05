package com.ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Данный класс представляет собой модель корзины покупок

//Аннотации Lombok для автоматической генерации кода:
@Entity  				// Помечает класс как JPA сущность (таблица в БД)
@AllArgsConstructor     // Генерирует конструктор со всеми полями
@NoArgsConstructor      // Генерирует конструктор без аргументов (обязателен для JPA)
@Getter                 // Генерирует геттеры для всех полей
@Setter                 // Генерирует сеттеры для всех полей
public class Cart { // Класс представляет товар в корзине пользователя
	
	// 1️. ПЕРВИЧНЫЙ КЛЮЧ
	@Id // Помечает поле как первичный ключ
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Стратегия генерации ID: IDENTITY = автоинкремент в БД
	private Integer id;
	
	// 2️. СВЯЗЬ С ПОЛЬЗОВАТЕЛЕМ (Many-to-One)
	@ManyToOne // Многие товары в корзине → одному пользователю
	private UserDtls user; // Каждая запись в корзине принадлежит конкретному пользователю
	
	// 3️. СВЯЗЬ С ТОВАРОМ (Many-to-One)  
	@ManyToOne // Многие записи корзины → одному товару
	private Product product; // Указывает, какой товар добавлен в корзину
	
	// 4️. КОЛИЧЕСТВО ТОВАРА 
	private Integer quantity;// Сколько единиц этого товара пользователь хочет купить
    // Пример: товар "Ноутбук", quantity = 1
	
	// 5️. ⭐ ВЫЧИСЛЯЕМОЕ ПОЛЕ: Общая цена для этого товара
	@Transient // НЕ сохраняется в БД! Вычисляется на лету
	private Double totalPrice;
	
	// 6️. ⭐ ВЫЧИСЛЯЕМОЕ ПОЛЕ: Общая цена всего заказа
	@Transient
	private Double totalOrderPrice; // Сумма всех totalPrice для всех товаров в корзине пользователя
	

}
