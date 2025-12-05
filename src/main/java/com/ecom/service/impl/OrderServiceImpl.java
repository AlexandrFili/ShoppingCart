package com.ecom.service.impl;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.model.Cart;
import com.ecom.model.OrderAddress;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.service.OrderService;
import com.ecom.util.OrderStatus;

//Реализация сервиса заказов, т.е. реализация интерфейса OrderService

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private ProductOrderRepository orderRepository; // Репозиторий заказов

	@Autowired
	private CartRepository cartRepository; // Репозиторий корзины

	// 1️. РЕАЛИЗАЦИЯ: СОЗДАНИЕ ЗАКАЗА
	@Override
	public void saveOrder(Integer userid, OrderRequest orderRequest) {

		// Шаг 1: Получаем все товары из корзины пользователя
		List<Cart> carts = cartRepository.findByUserId(userid);

		// Шаг 2: Для каждого товара в корзине создаем отдельный заказ
		for (Cart cart : carts) {

			ProductOrder order = new ProductOrder();

			order.setOrderId(UUID.randomUUID().toString()); 		// Генерация уникального ID заказа
			order.setOrderDate(LocalDate.now()); 					// Установка текущей даты

			order.setProduct(cart.getProduct());                    // Копирование данных из корзины
			order.setPrice(cart.getProduct().getDiscountPrice());

			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());

			order.setStatus(OrderStatus.IN_PROGRESS.getName());     // Установка статуса заказа
			order.setPaymentType(orderRequest.getPaymentType());	// Способ оплаты из OrderRequest

			// Создание адреса доставки из OrderRequest
			OrderAddress address = new OrderAddress();				
			address.setFirstName(orderRequest.getFirstName());
			address.setLastName(orderRequest.getLastName());
			address.setEmail(orderRequest.getEmail());
			address.setMobileNo(orderRequest.getMobileNo());
			address.setAddress(orderRequest.getAddress());
			address.setCity(orderRequest.getCity());
			address.setRegion(orderRequest.getRegion());
			address.setPincode(orderRequest.getPincode());

			order.setOrderAddress(address);							// Связываем адрес с заказом

			orderRepository.save(order);							// Сохранение заказа в БД

		}
	}

	// 2️. РЕАЛИЗАЦИЯ: ПОЛУЧЕНИЕ ЗАКАЗОВ ПОЛЬЗОВАТЕЛЯ
	@Override
	public List<ProductOrder> getOrdersByUser(Integer userId) {
		List<ProductOrder> orders = orderRepository.findByUserId(userId);
		return orders;
	}
	
	// 3️. РЕАЛИЗАЦИЯ: ОБНОВЛЕНИЕ СТАТУСА ЗАКАЗА
	@Override
	public Boolean updateOrderStatus(Integer id, String status) {
		Optional<ProductOrder> findById = orderRepository.findById(id); // Используем Optional для безопасной работы
		if (findById.isPresent()) {
			ProductOrder productOrder = findById.get(); 
			productOrder.setStatus(status);  // Обновляем статус
			orderRepository.save(productOrder);  // Сохраняем изменения
			return true;  // Успешно обновлено
		}
		return false; // Заказ не найден
	}

	// 4️. РЕАЛИЗАЦИЯ: ПОЛУЧЕНИЕ ВСЕХ ЗАКАЗОВ
	@Override
	public List<ProductOrder> getAllOrders() {
		
		return orderRepository.findAll();
	}
}