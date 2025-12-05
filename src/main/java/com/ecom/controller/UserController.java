package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

//КОНТРОЛЛЕР ПОЛЬЗОВАТЕЛЯ (USER CONTROLLER)
//Обрабатывает все запросы, связанные с личным кабинетом пользователя
//Маршрут: все методы доступны по пути /user/...
//Доступен только аутентифицированным пользователям с ролью ROLE_USER

@Controller
@RequestMapping("/user")
public class UserController {

	// ВНЕДРЕНИЕ ЗАВИСИМОСТЕЙ (СЕРВИСОВ)
	// Используем @Autowired для автоматического подключения сервисов
	
	@Autowired
	private UserService userService; // Сервис для работы с пользователями

	@Autowired
	private CategoryService categoryService; // Сервис для работы с категориями

	@Autowired
	private CartService cartService; // Сервис для работы с корзиной
	 
	@Autowired
	private OrderService orderService; // Сервис для работы с заказами
	
	// 1️. ГЛАВНАЯ СТРАНИЦА ЛИЧНОГО КАБИНЕТА
	// Отображает домашнюю страницу личного кабинета пользователя
	@GetMapping({ "", "/" })
	public String home() {
		return "user/home";
	}

	// МЕТОД @ModelAttribute: ОБЩИЕ ДАННЫЕ ДЛЯ ВСЕХ СТРАНИЦ
	// Выполняется перед каждым запросом к контроллеру
	// Добавляет в модель информацию о текущем пользователе и категориях
	@ModelAttribute
	public void getUsersDetails(Principal p, Model m) {
		if (p != null) {
			// ШАГ 1: ПОЛУЧЕНИЕ ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ
			String email = p.getName(); // Получаем email из Spring Security
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			
			// ШАГ 2: КОЛИЧЕСТВО ТОВАРОВ В КОРЗИНЕ
			// Отображается в шапке сайта (иконка корзины с цифрой)
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}

		// ШАГ 3: СПИСОК ВСЕХ АКТИВНЫХ КАТЕГОРИЙ
		// Добавляется на все страницы для отображения в меню навигации
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}

	// 2️. ДОБАВЛЕНИЕ ТОВАРА В КОРЗИНУ
	// Добавляет товар в корзину текущего пользователя
	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {

		// ШАГ 1: СОХРАНЕНИЕ ТОВАРА В КОРЗИНУ
		// CartService сам обрабатывает логику:
		// - Если товара нет в корзине - создает новую запись с количеством 1
		// - Если товар уже есть - увеличивает количество на 1
		Cart saveCart = cartService.saveCart(pid, uid);

		if (ObjectUtils.isEmpty(saveCart)) {

			session.setAttribute("errorMsg", "Товар не был добавлен в корзину!");
		} else {
			session.setAttribute("succMsg", "Товар добавлен в корзину!");
		}
		
		// ШАГ 2: ПЕРЕНАПРАВЛЕНИЕ НА СТРАНИЦУ ТОВАРА
		// Пользователь остается на странице товара, видит сообщение
		return "redirect:/product/" + pid;
	}

	// 3️. СТРАНИЦА КОРЗИНЫ
	// Отображает все товары в корзине текущего пользователя
	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {

		// ШАГ 1: ПОЛУЧЕНИЕ ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ
		UserDtls user = getLoggedInUserDetails(p);
		
		// ШАГ 2: ПОЛУЧЕНИЕ ТОВАРОВ В КОРЗИНЕ
		// CartService возвращает список товаров с рассчитанными ценами
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		
		// ШАГ 3: РАСЧЕТ ОБЩЕЙ СУММЫ ЗАКАЗА
		// Общая сумма хранится в последнем элементе списка
		if (carts.size() > 0) {

			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/cart";
	}

	// 4️. ИЗМЕНЕНИЕ КОЛИЧЕСТВА ТОВАРА В КОРЗИНЕ
	// Увеличивает или уменьшает количество конкретного товара в корзине
	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {

		// sy: операция ("de" - decrease/уменьшить, иначе - increase/увеличить)
        // cid: ID записи в корзине (Cart.id)
		cartService.updateQuantity(sy, cid);
		
		// После изменения возвращаем пользователя на страницу корзины
		return "redirect:/user/cart";

	}

	// ВСПОМОГАТЕЛЬНЫЙ МЕТОД: ПОЛУЧЕНИЕ ДАННЫХ ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ
	// Используется в нескольких методах контроллера
	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}
	
	// 5️. СТРАНИЦА ОФОРМЛЕНИЯ ЗАКАЗА
	// Отображает страницу с подтверждением заказа и формой ввода данных доставки
	@GetMapping("/orders")
	public String orderPage(Principal p, Model m) {
		
		// ШАГ 1: ПОЛУЧЕНИЕ ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ И ТОВАРОВ В КОРЗИНЕ
		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		
		// ШАГ 2: РАСЧЕТ СТОИМОСТИ ЗАКАЗА
		if (carts.size() > 0) {

			// Стоимость товаров (без доставки)
			Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			
			// Итоговая стоимость (с доставкой 500 руб.)
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 500;
			
			m.addAttribute("orderPrice", orderPrice);
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/order";

	}
	
	// 6️. СОХРАНЕНИЕ ЗАКАЗА
	// Обрабатывает форму оформления заказа
	@PostMapping("/save-order")
	public String savePage(@ModelAttribute OrderRequest request, Principal p) {

		// ШАГ 1: ПОЛУЧЕНИЕ ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ
		UserDtls user = getLoggedInUserDetails(p);
		
		// ШАГ 2: СОХРАНЕНИЕ ЗАКАЗА В БАЗЕ ДАННЫХ
		orderService.saveOrder(user.getId(), request);
		
		return "redirect:/user/success";

	}
	
	// 7️. СТРАНИЦА УСПЕШНОГО ОФОРМЛЕНИЯ ЗАКАЗА
	// Отображается после успешного создания заказа
	@GetMapping("/success")
	public String loadSuccess() {
		
		return "/user/success";

	}
	
	// 8️. СТРАНИЦА МОИХ ЗАКАЗОВ
	// Отображает историю заказов текущего пользователя
	@GetMapping("/user-orders")
	public String myOrder(Model m, Principal p) {
		
		// ШАГ 1: ПОЛУЧЕНИЕ ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ
		UserDtls loginUser = getLoggedInUserDetails(p);
		
		// ШАГ 2: ПОЛУЧЕНИЕ ВСЕХ ЗАКАЗОВ ПОЛЬЗОВАТЕЛЯ
		List <ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
		m.addAttribute("orders", orders);
		
		return "/user/my_orders";

	}
	
	// 9️. ОБНОВЛЕНИЕ СТАТУСА ЗАКАЗА (для пользователя)
	// Позволяет пользователю отменить заказ (изменить статус на "Отменен")
	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		// ШАГ 1: КОНВЕРТАЦИЯ ID СТАТУСА В ТЕКСТОВОЕ НАЗВАНИЕ
		// Используем enum OrderStatus для получения названия по ID
		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		// ШАГ 2: ОБНОВЛЕНИЕ СТАТУСА В БАЗЕ ДАННЫХ
		Boolean updateOrder = orderService.updateOrderStatus(id, status);

		if (updateOrder) {
			session.setAttribute("succMsg", "Статус обновился!");
		} else {
			session.setAttribute("errorMsg", "Статус не обновился!");
		}
		
		// ШАГ 3: ПЕРЕНАПРАВЛЕНИЕ НА СТРАНИЦУ ЗАКАЗОВ
		return "redirect:/user/user-orders";
	}
	
}
