package com.ecom.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Cart;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.CartService;

//Реализация сервиса корзины, т.е. реализация интерфейса CartService

@Service
public class CartServiceImpl implements CartService{

	@Autowired
	private CartRepository cartRepository; // Репозиторий корзины
	
	@Autowired
	private UserRepository userRepository; // Репозиторий пользователей
	
	@Autowired
	private ProductRepository productRepository; // Репозиторий товаров
	
	// 1️. РЕАЛИЗАЦИЯ: СОХРАНЕНИЕ ТОВАРА В КОРЗИНУ
	@Override
	public Cart saveCart(Integer productId, Integer userId) {
		
		// Шаг 1: Получаем пользователя и товар из БД
		UserDtls userDtls = userRepository.findById(userId).get();
		
		Product product = productRepository.findById(productId).get();
		
		// Шаг 2: Проверяем, есть ли уже такой товар в корзине пользователя
		Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);
		
		Cart cart = null;
		
		if(ObjectUtils.isEmpty(cartStatus)) {
			
			cart = new Cart();
			cart.setProduct(product);
			cart.setUser(userDtls);
			cart.setQuantity(1);
			// Рассчитываем общую цену для этого товара
            // Используем discountPrice (цену со скидкой), если есть
			cart.setTotalPrice(1 * product.getDiscountPrice());
		}else { // Шаг 4: Если товар уже есть - увеличиваем количество
			cart = cartStatus;
			cart.setQuantity(cart.getQuantity() + 1); // Увеличиваем на 1
			cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice()); // Пересчитываем общую цену
			
		}
		// Шаг 5: Сохраняем в БД
		Cart saveCart = cartRepository.save(cart);
		
		return saveCart; // Возвращаем сохраненную запись
	}

	// 2️. РЕАЛИЗАЦИЯ: ПОЛУЧЕНИЕ КОРЗИНЫ ПОЛЬЗОВАТЕЛЯ
	@Override
	public List<Cart> getCartsByUser(Integer userId) {
		// Шаг 1: Получаем все товары в корзине пользователя
		List<Cart> carts = cartRepository.findByUserId(userId); 
		
		Double totalOrderPrice=0.0; // Общая сумма всего заказа
		
		List<Cart> updateCarts = new ArrayList<>(); // Новый список с рассчитанными ценами
		
		// Шаг 2: Для каждого товара в корзине рассчитываем цены
		for(Cart c:carts) { // Рассчитываем общую цену для этого товара
			Double totalPrice = (c.getProduct().getDiscountPrice()*c.getQuantity());
			c.setTotalPrice(totalPrice); // Устанавливаем в объект Cart
			
			// Добавляем к общей сумме заказа
			totalOrderPrice = totalOrderPrice + totalPrice;
			c.setTotalOrderPrice(totalOrderPrice); 
			updateCarts.add(c); // Добавляем в новый список
		}

		return updateCarts; // Возвращаем список с рассчитанными ценами
	}
	
	// 3️. РЕАЛИЗАЦИЯ: ПОЛУЧЕНИЕ КОЛИЧЕСТВА ТОВАРОВ В КОРЗИНЕ
	@Override
	public Integer getCountCart(Integer userId) {
		// Используем метод репозитория для подсчета
		Integer countByUserId = cartRepository.countByUserId(userId);	
		return countByUserId; // Количество РАЗНЫХ товаров в корзине 
		// ⚠️ Это количество позиций, а не общее количество товаров!
        // Если 2 ноутбука и 3 мыши → count = 2 (позиции), а не 5 (штук)
	}

	// 4️. РЕАЛИЗАЦИЯ: ИЗМЕНЕНИЕ КОЛИЧЕСТВА ТОВАРА
	@Override
	public void updateQuantity(String sy, Integer cid) { 
		// sy: операция ("de" - decrease/уменьшить, иначе - increase/увеличить)
        // cid: ID записи в корзине (Cart.id)
		
		// Шаг 1: Получаем запись корзины из БД
		Cart cart = cartRepository.findById(cid).get();
		int updateQuantity;
		// Шаг 2: Если операция "de" - уменьшаем количество
		if(sy.equalsIgnoreCase("de")) {
			updateQuantity = cart.getQuantity() - 1;
			
			// Если количество стало 0 или меньше - удаляем товар из корзины
			if(updateQuantity <= 0) {
				cartRepository.delete(cart);
			}else { // Иначе сохраняем новое количество
				cart.setQuantity(updateQuantity);
				cartRepository.save(cart);
			}
		}else { // Шаг 3: Иначе - увеличиваем количество
			updateQuantity = cart.getQuantity() + 1;
			cart.setQuantity(updateQuantity);
			cartRepository.save(cart);
		}
	}
}
