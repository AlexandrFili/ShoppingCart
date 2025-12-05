package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.ProductRepository;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

//ГЛАВНЫЙ КОНТРОЛЛЕР (HOME CONTROLLER)
//Обрабатывает основные публичные маршруты приложения:
//- Главная страница
//- Регистрация и авторизация
//- Просмотр товаров
//- Восстановление пароля

@Controller
public class HomeController {

	// ВНЕДРЕНИЕ ЗАВИСИМОСТЕЙ (СЕРВИСОВ)
	// Используем @Autowired для автоматического подключения сервисов
	
	@Autowired
	private CategoryService categoryService; // Сервис для работы с категориями

	@Autowired
	private ProductService productService; // Сервис для работы с товарами

	@Autowired
	private UserService userService; // Сервис для работы с пользователями

	@Autowired
	private CommonUtil commonUtil; // Утилиты для отправки email и генерации URL

	@Autowired
	private BCryptPasswordEncoder passwordEncoder; //Шифровальщик паролей

	@Autowired
	private CartService cartService; // Сервис для работы с корзиной

	// КОНСТАНТА: ДИРЕКТОРИЯ ДЛЯ ЗАГРУЗКИ ФАЙЛОВ
	// Все загружаемые файлы (изображения профилей) сохраняются в папке uploads/
	private final String UPLOAD_DIR = "uploads/";

	// ВСПОМОГАТЕЛЬНЫЙ МЕТОД: СОХРАНЕНИЕ КАРТИНКИ ПРОФИЛЯ
	// Сохраняет загруженное изображение профиля пользователя
	private void saveFile(MultipartFile file, String subDirectory, String fileName) throws IOException {
		if (file != null && !file.isEmpty()) {
			
			// ШАГ 1: СОЗДАНИЕ ПУТИ
			// Формируем полный путь: uploads/profile_img/имя_файла.jpg
			Path path = Paths.get(UPLOAD_DIR + subDirectory + "/" + fileName);
			
			// ШАГ 2: СОЗДАНИЕ ДИРЕКТОРИЙ
			// Создаем все необходимые папки, если они не существуют
			Files.createDirectories(path.getParent());
			
			// ШАГ 3: КОПИРОВАНИЕ ФАЙЛА
			// Сохраняем файл на диск с заменой существующего (REPLACE_EXISTING)
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("✅ Файл профиля сохранен: " + path.toAbsolutePath());
		}
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

	// 1️. ГЛАВНАЯ СТРАНИЦА САЙТА
	// Отображает домашнюю страницу интернет-магазина
	@GetMapping("/")
	public String index() {
		return "index";
	}

	// 2️. СТРАНИЦА АВТОРИЗАЦИИ (ВХОДА)
	// Отображает форму входа в аккаунт
	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	// 3️. СТРАНИЦА РЕГИСТРАЦИИ
	// Отображает форму создания нового аккаунта
	@GetMapping("/register")
	public String register() {
		return "register";
	}

	// 4️. СТРАНИЦА ТОВАРОВ С ФИЛЬТРАЦИЕЙ
	// Отображает каталог товаров с возможностью фильтрации по категориям
	@GetMapping("/products")
	public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category) {

		// ШАГ 1: ПОЛУЧЕНИЕ ВСЕХ КАТЕГОРИЙ ДЛЯ ФИЛЬТРА
		// Используется для построения меню фильтрации
		List<Category> categories = categoryService.getAllActiveCategory();
		
		// ШАГ 2: ПОЛУЧЕНИЕ ТОВАРОВ
		// Если category не пустая - фильтруем по категории
		// Если category пустая - получаем все активные товары
		List<Product> products = productService.getAllActiveProducts(category);
		
		// ШАГ 3: ДОБАВЛЕНИЕ ДАННЫХ В МОДЕЛЬ
		m.addAttribute("categories", categories); // Все категории для фильтра
		m.addAttribute("products", products);     // Отфильтрованные товары
		m.addAttribute("paramValue", category);   // Текущая выбранная категория (для сохранения состояния фильтра)
		return "product";
	}

	// 5️. СТРАНИЦА ПРОСМОТРА КОНКРЕТНОГО ТОВАРА
	// Отображает детальную информацию о товаре
	@GetMapping("/product/{id}")
	public String product(@PathVariable int id, Model m) {
		
		// Получаем товар по его ID
		Product productById = productService.getProductById(id);

		// Добавляем товар в модель для отображения на странице
		m.addAttribute("product", productById);
		return "view_product";
	}

	// 6️. СОХРАНЕНИЕ НОВОГО ПОЛЬЗОВАТЕЛЯ (РЕГИСТРАЦИЯ)
	// Обрабатывает форму регистрации нового пользователя
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {

		// ШАГ 1: ОБРАБОТКА ИЗОБРАЖЕНИЯ ПРОФИЛЯ
		// Если файл не загружен - используем изображение по умолчанию
		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);

		// ШАГ 2: СОХРАНЕНИЕ ПОЛЬЗОВАТЕЛЯ В БАЗЕ ДАННЫХ
		// В UserService происходит шифрование пароля и установка роли ROLE_USER
		UserDtls saveUser = userService.saveUser(user);

		if (!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
				// ✅ СОХРАНЯЕМ В UPLOADS
				saveFile(file, "profile_img", file.getOriginalFilename());
				session.setAttribute("succMsg", "Профиль успешно зарегистрирован!");
			}
		} else {
			session.setAttribute("errorMsg", "Что-то не так на сервере.");
		}

		return "redirect:/register"; // Возвращаем на страницу регистрации
	}

	// Восстановление пароля - код

	
	// 7️. СТРАНИЦА ЗАПРОСА СБРОСА ПАРОЛЯ
	// Отображает форму для ввода email и запроса сброса пароля
	@GetMapping("/forgot-password")
	public String showForgotPassword() {
		return "forgot_password.html";
	}

	// 8️. ОБРАБОТКА ЗАПРОСА НА СБРОС ПАРОЛЯ
	// Отправляет письмо со ссылкой для сброса пароля на указанный email
	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {

		// ШАГ 1: ПРОВЕРКА СУЩЕСТВОВАНИЯ ПОЛЬЗОВАТЕЛЯ
		UserDtls userByEmail = userService.getUserByEmail(email);

		if (ObjectUtils.isEmpty(userByEmail)) { // Если пользователь с таким email не найден
			session.setAttribute("errorMsg", "Недействительный email");
		} else {

			// ШАГ 2: ГЕНЕРАЦИЯ УНИКАЛЬНОГО ТОКЕНА ДЛЯ СБРОСА
			// Создаем случайный токен, который будет использоваться в ссылке
			String resetToken = UUID.randomUUID().toString();

			// Сохраняем токен в базу данных для данного пользователя
			userService.updateUserResetToken(email, resetToken);

			// ШАГ 3: ГЕНЕРАЦИЯ ССЫЛКИ ДЛЯ СБРОСА ПАРОЛЯ
			// Пример: http://localhost:8080/reset-password?token=abc123def456
			String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

			// ШАГ 4: ОТПРАВКА ПИСЬМА С ССЫЛКОЙ
			Boolean sendMail = commonUtil.sendMail(url, email);

			if (sendMail) {

				session.setAttribute("succMsg",
						"Пожалуйста, проверьте свой email..Ссылка для сброса пароля отправлена.");
			} else {
				session.setAttribute("errorMsg",
						"Что-то не так с сервером! Ссылка для сброса пароля на почту не отправлена!");
			}
		}

		return "redirect:/forgot-password"; // Возвращаем на страницу запроса сброса
	}

	// 9️. СТРАНИЦА СБРОСА ПАРОЛЯ ПО ТОКЕНУ
	// Отображает форму для ввода нового пароля (доступна только по валидной ссылке)
	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {

		// ШАГ 1: ПРОВЕРКА ВАЛИДНОСТИ ТОКЕНА
		// Ищем пользователя по токену в базе данных
		UserDtls userByToken = userService.getUserByToken(token);

		if (userByToken == null) {
			// Токен недействителен или устарел
			m.addAttribute("msg", "Ваша ссылка не действительна или устарела!!");
			return "message";
		}
		// ШАГ 2: ДОБАВЛЕНИЕ ТОКЕНА В МОДЕЛЬ
		// Токен будет скрыто передан в форму для подтверждения при смене пароля
		m.addAttribute("token", token);

		return "reset_password";
	}
	
	// 10️. ОБРАБОТКА СБРОСА ПАРОЛЯ
	// Сохраняет новый пароль пользователя и удаляет использованный токен
	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,
			Model m) {

		// ШАГ 1: ПОВТОРНАЯ ПРОВЕРКА ТОКЕНА (двойная безопасность)
		UserDtls userByToken = userService.getUserByToken(token);

		if (userByToken == null) {
			// Токен недействителен (возможно, уже использован)
			m.addAttribute("errorMsg", "Ваша ссылка не действительна или устарела!!");
			return "message";
		} else {
			// ШАГ 2: ШИФРОВАНИЕ И СОХРАНЕНИЕ НОВОГО ПАРОЛЯ
			userByToken.setPassword(passwordEncoder.encode(password)); 
			// ШАГ 3: УДАЛЕНИЕ ТОКЕНА (одноразовое использование)
			userByToken.setResetToken(null);
			// ШАГ 4: СОХРАНЕНИЕ ОБНОВЛЕННОГО ПОЛЬЗОВАТЕЛЯ
			userService.updateUser(userByToken);
			// session.setAttribute("succMsg", "Пароль успешно изменен!");
			// ШАГ 5: СООБЩЕНИЕ ОБ УСПЕХЕ
			m.addAttribute("msg", "Пароль успешно изменен!");
			return "message";
		}

	}

}
