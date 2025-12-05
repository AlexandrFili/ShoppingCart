package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Cart;

// Репозиторий для работы с корзиной 

//Интерфейс репозитория для сущности Cart
//Наследуется от JpaRepository<Cart, Integer>:
//- Cart: тип сущности, с которой работает репозиторий
//- Integer: тип первичного ключа сущности Cart
public interface CartRepository extends JpaRepository<Cart, Integer> { 
	// Spring Data JPA автоматически реализует этот интерфейс!
    // Не нужно писать реализации - Spring создаст её во время выполнения.
	
	// 1️. ПОИСК ТОВАРА В КОРЗИНЕ ПОЛЬЗОВАТЕЛЯ
	// Метод ищет запись в корзине по ID товара и ID пользователя
	public Cart findByProductIdAndUserId(Integer productId, Integer userId);
	// "findBy" + "ProductId" (поле product.id) + "And" + "UserId" (поле user.id)
	
	// 2️. ПОДСЧЕТ КОЛИЧЕСТВА ТОВАРОВ В КОРЗИНЕ
	// Метод возвращает количество записей в корзине для пользователя
	public Integer countByUserId(Integer userId);
	 // "countBy" + "UserId" (поле user.id)

	// 3️. ПОЛУЧЕНИЕ ВСЕХ ТОВАРОВ В КОРЗИНЕ ПОЛЬЗОВАТЕЛЯ
	public List<Cart> findByUserId(Integer userId);
	// "findBy" + "UserId" (поле user.id)
}
