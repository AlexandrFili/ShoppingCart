package com.ecom.service;

import java.util.List;

import com.ecom.model.Cart;

//Интерфейс сервиса для работы с корзиной
//Определяет контракт (методы), которые должен реализовать сервис
public interface CartService {

	// 1️. СОХРАНЕНИЕ ТОВАРА В КОРЗИНЕ
    // Метод добавляет товар в корзину пользователя
	public Cart saveCart(Integer productId, Integer userId);

	// 2️. ПОЛУЧЕНИЕ ВСЕХ ТОВАРОВ В КОРЗИНЕ ПОЛЬЗОВАТЕЛЯ
    // Метод возвращает список всех товаров в корзине пользователя
	public List<Cart> getCartsByUser(Integer userId);
	
	// 3️. ПОЛУЧЕНИЕ КОЛИЧЕСТВА ТОВАРОВ В КОРЗИНЕ
    // Метод возвращает общее количество позиций в корзине
	public Integer getCountCart(Integer userId);

	// 4️. ИЗМЕНЕНИЕ КОЛИЧЕСТВА ТОВАРА В КОРЗИНЕ
    // Метод изменяет количество конкретного товара в корзине
	public void updateQuantity(String sy, Integer cid);
	// Параметры:
    // - sy: операция ("+" увеличить, "-" уменьшить)
    // - cid: ID записи в корзине (Cart.id)
	

}
