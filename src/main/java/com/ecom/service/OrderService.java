package com.ecom.service;

import java.util.List;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

//Интерфейс сервиса для работы с заказами
public interface OrderService {

	// 1️. СОЗДАНИЕ НОВОГО ЗАКАЗА
    // Метод создает заказ на основе данных пользователя и OrderRequest
	public void saveOrder(Integer userid, OrderRequest orderRequest);
	
	// 2️. ПОЛУЧЕНИЕ ЗАКАЗОВ ПОЛЬЗОВАТЕЛЯ
    // Метод возвращает все заказы конкретного пользователя
	public List<ProductOrder> getOrdersByUser(Integer userId);
	
	// 3️. ОБНОВЛЕНИЕ СТАТУСА ЗАКАЗА
    // Метод изменяет статус заказа
	public Boolean updateOrderStatus(Integer id,String status);

	 // 4️. ПОЛУЧЕНИЕ ВСЕХ ЗАКАЗОВ (для админа)
    // Метод возвращает все заказы всех пользователей
	public List<ProductOrder> getAllOrders();
	
}