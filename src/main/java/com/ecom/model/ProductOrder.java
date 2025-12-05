package com.ecom.model;

import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Данный класс представляет собой самую важную бизнес сущность - модель заказа товара

//Lombok аннотации:
@AllArgsConstructor  // Генерирует конструктор со всеми полями
@NoArgsConstructor   // Генерирует конструктор без аргументов (обязателен для JPA)
@Getter              // Генерирует геттеры для всех полей
@Setter              // Генерирует сеттеры для всех полей
@Entity              // Помечает класс как JPA сущность (таблица в БД)
public class ProductOrder {

	// 1️. ПЕРВИЧНЫЙ КЛЮЧ
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Стратегия генерации ID: IDENTITY = автоинкремент в БД
	private Integer id;

	// 2️. БИЗНЕС-ИДЕНТИФИКАТОР ЗАКАЗА
	private String orderId; // Публичный номер заказа (показывается пользователю)

	// 3️. ДАТА ЗАКАЗА
	private LocalDate orderDate; // Дата создания заказа

	// 4️. ИНФОРМАЦИЯ О ТОВАРЕ
	@ManyToOne // Многие заказы → одному товару
	private Product product; // Какой товар заказан
	// Важно: сохраняется ССЫЛКА на товар, чтобы при изменении товара
    // в будущем, данные заказа остались исторически корректными

	private Double price; // Цена товара на момент заказа

	private Integer quantity; // Количество товара

	// 5️. ИНФОРМАЦИЯ О ПОКУПАТЕЛЕ
	@ManyToOne // Многие заказы → одному пользователю
	private UserDtls user; // Кто сделал заказ

	// 6️. СТАТУС ЗАКАЗА
	private String status;  // Текущий статус заказа (можно увидеть в OrderStatus.java в com.ecom.util)

	// 7️. СПОСОБ ОПЛАТЫ
	private String paymentType;
	
	// 8️. ⭐ АДРЕС ДОСТАВКИ (важная связь!)
	@OneToOne(cascade = CascadeType.ALL) // Один заказ → один адрес
	private OrderAddress orderAddress; // Адрес доставки

	// cascade = CascadeType.ALL означает:
    // При сохранении заказа автоматически сохраняется адрес
    // При удалении заказа удаляется адрес
    // При обновлении заказа обновляется адрес

}
