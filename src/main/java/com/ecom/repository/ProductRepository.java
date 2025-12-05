package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Product;

// Репозиторий для работы с товарами 

//Интерфейс репозитория для сущности Product
//Наследуется от JpaRepository<Product, Integer>:
//- Product: тип сущности (товар)
//- Integer: тип первичного ключа (у Product поле id типа int)
public interface ProductRepository extends JpaRepository<Product, Integer>{

	// 1️. ПОЛУЧЕНИЕ ВСЕХ АКТИВНЫХ ТОВАРОВ
    // Метод возвращает список только активных товаров
	List <Product> findByIsActiveTrue();
	// "findBy" + "IsActive" (поле isActive) + "True" (значение true)

	// 2️. ПОИСК ТОВАРОВ ПО КАТЕГОРИИ
    // Метод возвращает товары определенной категории
	List<Product> findByCategory(String category);
	// "findBy" + "Category" (поле category)
}
