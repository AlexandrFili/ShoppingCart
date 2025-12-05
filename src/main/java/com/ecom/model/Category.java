package com.ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Данный класс представляет собой модель категории товаров 

//Аннотации Lombok для автоматической генерации кода:
@AllArgsConstructor    // Генерирует конструктор со всеми полями
@NoArgsConstructor     // Генерирует конструктор без аргументов (обязателен для JPA)
@Getter                // Генерирует геттеры для всех полей
@Setter                // Генерирует сеттеры для всех полей
@Entity                // Помечает класс как JPA сущность (таблица в БД)
public class Category {
	
	// 1️. ПЕРВИЧНЫЙ КЛЮЧ
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Стратегия генерации ID: IDENTITY = автоинкремент в БД
	private int id;
	
	// 2️. НАЗВАНИЕ КАТЕГОРИИ
	private String name;
	
	// 3️. ИМЯ ФАЙЛА ИЗОБРАЖЕНИЯ
	private String imageName; // Файл хранится в папке uploads/categories/
	
	// 4️. ФЛАГ АКТИВНОСТИ КАТЕГОРИИ
	private Boolean isActive;
	// Управление видимостью категории:
    // - true: категория активна, товары видны в каталоге
    // - false: категория скрыта, товары не видны (но остаются в БД)

}
