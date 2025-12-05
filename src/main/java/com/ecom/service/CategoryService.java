package com.ecom.service;

import java.util.List;
import com.ecom.model.Category;

//Интерфейс сервиса для работы с категориями товаров
public interface CategoryService {
	
	// 1️. СОЗДАНИЕ/СОХРАНЕНИЕ КАТЕГОРИИ
    // Метод создает новую категорию или обновляет существующую
	public Category saveCategory(Category category);
	
	// 2️. ПРОВЕРКА СУЩЕСТВОВАНИЯ КАТЕГОРИИ ПО ИМЕНИ
    // Метод проверяет, существует ли категория с указанным именем
	public Boolean existCategory(String name);
	
	// 3️. ПОЛУЧЕНИЕ ВСЕХ КАТЕГОРИЙ
    // Метод возвращает список ВСЕХ категорий
	public List<Category> getAllCategory();
	
	// 4️. УДАЛЕНИЕ КАТЕГОРИИ
    // Метод удаляет категорию по ID
	public Boolean deleteCategory(int id);
	
	// 5️. ПОЛУЧЕНИЕ КАТЕГОРИИ ПО ID
    // Метод возвращает категорию по её ID
	public Category getCategoryById(int id);
	
	// 6️. ПОЛУЧЕНИЕ ВСЕХ АКТИВНЫХ КАТЕГОРИЙ
    // Метод возвращает список только АКТИВНЫХ категорий
	public List<Category> getAllActiveCategory();
}
