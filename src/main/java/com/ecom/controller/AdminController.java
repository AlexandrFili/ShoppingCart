package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

//КОНТРОЛЛЕР АДМИНИСТРАТОРА (ADMIN CONTROLLER)
//Обрабатывает все запросы, связанные с административной панелью
//Маршрут: все методы доступны по пути /admin/...

@Controller
@RequestMapping("/admin")
public class AdminController {

	// ВНЕДРЕНИЕ ЗАВИСИМОСТЕЙ (СЕРВИСОВ)
	
	@Autowired
	private CategoryService categoryService; // Сервис для работы с категориями

	@Autowired
	private ProductService productService; // Сервис для работы с товарами
	
	@Autowired
	private UserService userService; // Сервис для работы с пользователями
	
	@Autowired
	private CartService cartService; // Сервис для работы с корзиной
	
	@Autowired
	private OrderService orderService; // Сервис для работы с заказами
	
	// КОНСТАНТА: ДИРЕКТОРИЯ ДЛЯ ЗАГРУЗКИ ФАЙЛОВ
	// Все загружаемые файлы (изображения категорий и товаров) сохраняются в папке uploads/
	private final String UPLOAD_DIR = "uploads/";
	
	// ВСПОМОГАТЕЛЬНЫЙ МЕТОД: СОХРАНЕНИЕ ФАЙЛА
	// Сохраняет загруженный файл в указанную поддиректорию с заданным именем
	private void saveFile(MultipartFile file, String subDirectory, String fileName) throws IOException {
        if (file != null && !file.isEmpty()) {
            // Создаем путь: uploads/category_img/имя_файла.jpg
            Path path = Paths.get(UPLOAD_DIR + subDirectory + "/" + fileName);
            
            // Создаем директорию если не существует
            Files.createDirectories(path.getParent());
            
            // Сохраняем файл
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("✅ Файл сохранен: " + path.toAbsolutePath());
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
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}
		
		// ШАГ 3: СПИСОК ВСЕХ АКТИВНЫХ КАТЕГОРИЙ
		// Добавляется на все страницы для отображения в меню навигации
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}

	// 1️. ГЛАВНАЯ СТРАНИЦА АДМИН-ПАНЕЛИ
	// Отображает главную страницу административной панели
	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

	// 2️. ЗАГРУЗКА ФОРМЫ ДОБАВЛЕНИЯ ТОВАРА
	// Отображает страницу с формой для добавления нового товара
	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		// Получаем все категории для выпадающего списка в форме
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories); 
		return "admin/add_product";
	}

	// 3️. СТРАНИЦА УПРАВЛЕНИЯ КАТЕГОРИЯМИ
	// Отображает список всех категорий с возможностью управления
	@GetMapping("/category")
	public String category(Model m) {
		// Получаем все категории (активные и неактивные)
		m.addAttribute("categorys", categoryService.getAllCategory());
		return "admin/category";
	}

	// 4️. СОХРАНЕНИЕ НОВОЙ КАТЕГОРИИ
	// Обрабатывает форму добавления новой категории
	@PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {

		// ШАГ 1: ОБРАБОТКА ИЗОБРАЖЕНИЯ
        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);

     // ШАГ 2: ПРОВЕРКА УНИКАЛЬНОСТИ КАТЕГОРИИ
        Boolean existsCategory = categoryService.existCategory(category.getName());

        if (existsCategory) { // Если категория с таким названием уже существует
            session.setAttribute("errorMsg", "Такое название категории уже существует!");
        } else { // ШАГ 3: СОХРАНЕНИЕ КАТЕГОРИИ В БАЗЕ ДАННЫХ
            Category saveCategory = categoryService.saveCategory(category);
            if (ObjectUtils.isEmpty(saveCategory)) {
                session.setAttribute("errorMsg", "Не сохранилось! Внутренняя ошибка сервера");
            } else {
                // ✅ СОХРАНЯЕМ В UPLOADS
                saveFile(file, "category_img", file.getOriginalFilename());
                session.setAttribute("succMsg", "Категории успешно сохранились");
            }
        }
        return "redirect:/admin/category";
    }

	// 5️. УДАЛЕНИЕ КАТЕГОРИИ
	// Удаляет категорию по её ID
	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {
		Boolean deleteCategory = categoryService.deleteCategory(id);

		if (deleteCategory) {
			session.setAttribute("succMsg", "Категория была удалена!");
		} else {
			session.setAttribute("errorMsg", "Что-то не так на сервере.");

		}

		return "redirect:/admin/category";
	}

	// 6️. ЗАГРУЗКА ФОРМЫ РЕДАКТИРОВАНИЯ КАТЕГОРИИ
	// Отображает форму редактирования существующей категории
	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {

		// Получаем категорию по ID для предзаполнения формы
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
	}
	
	// 7️. ОБНОВЛЕНИЕ КАТЕГОРИИ
	// Обрабатывает форму редактирования категории
	 @PostMapping("/updateCategory")
	    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
	            HttpSession session) throws IOException {

		 	// ШАГ 1: ПОЛУЧЕНИЕ СТАРОЙ КАТЕГОРИИ
	        Category oldCategory = categoryService.getCategoryById(category.getId());
	        
	        // ШАГ 2: ОБРАБОТКА ИЗОБРАЖЕНИЯ
	        // Если новый файл не загружен, используем старое изображение
	        String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

	        if (!ObjectUtils.isEmpty(category)) {
	        	// ШАГ 3: ОБНОВЛЕНИЕ ДАННЫХ
	            oldCategory.setName(category.getName());
	            oldCategory.setIsActive(category.getIsActive());
	            oldCategory.setImageName(imageName);
	        }

	        // ШАГ 4: СОХРАНЕНИЕ ОБНОВЛЕННОЙ КАТЕГОРИИ
	        Category updateCategory = categoryService.saveCategory(oldCategory);

	        if (!ObjectUtils.isEmpty(updateCategory)) {
	        	// ШАГ 5: СОХРАНЕНИЕ НОВОГО ИЗОБРАЖЕНИЯ (если оно было загружено)
	            if (!file.isEmpty()) {
	                // ✅ СОХРАНЯЕМ В UPLOADS
	                saveFile(file, "category_img", file.getOriginalFilename());
	            }
	            session.setAttribute("succMsg", "Категория была обновлена");
	        } else {
	            session.setAttribute("errorMsg", "Что-то не так на сервере.");
	        }
	        return "redirect:/admin/loadEditCategory/" + category.getId();
	    }

	// 8️. СОХРАНЕНИЕ НОВОГО ТОВАРА
	// Обрабатывает форму добавления нового товара
	 @PostMapping("/saveProduct")
	    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
	            HttpSession session) throws IOException {

		 	// ШАГ 1: ОБРАБОТКА ИЗОБРАЖЕНИЯ ТОВАРА
	        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

	        // ШАГ 2: УСТАНОВКА ДАННЫХ ТОВАРА
	        product.setImage(imageName);
	        product.setDiscount(0);
	        product.setDiscountPrice(product.getPrice());
	        Product saveProduct = productService.saveProduct(product);

	        // ШАГ 3: СОХРАНЕНИЕ ТОВАРА В БАЗЕ ДАННЫХ
	        if (!ObjectUtils.isEmpty(saveProduct)) {
	            // ✅ СОХРАНЯЕМ В UPLOADS
	            saveFile(image, "product_img", image.getOriginalFilename());
	            session.setAttribute("succMsg", "Товар успешно добавлен!");
	        } else {
	            session.setAttribute("errorMsg", "Что-то не так на сервере.");
	        }
	        return "redirect:/admin/loadAddProduct";
	    }

	 
	// 9️. СТРАНИЦА ВСЕХ ТОВАРОВ
	// Отображает список всех товаров для администрирования
	@GetMapping("/products")
	public String loadViewProduct(Model m) {
		m.addAttribute("products", productService.getAllProducts());
		return "admin/products";

	}

	// 10️. УДАЛЕНИЕ ТОВАРА
	// Удаляет товар по его ID
	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Товар успешно удален!");
		} else {
			session.setAttribute("errorMsg", "Что-то не так на сервере.");
		}

		return "redirect:/admin/products";

	}

	// 11️. ЗАГРУЗКА ФОРМЫ РЕДАКТИРОВАНИЯ ТОВАРА
	// Отображает форму редактирования существующего товара
	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id)); // Получаем товар по ID для предзаполнения формы
		m.addAttribute("categories", categoryService.getAllCategory()); // Получаем все категории для выпадающего списка
		return "admin/edit_product";
	}

	// 12️. ОБНОВЛЕНИЕ ТОВАРА
	// Обрабатывает форму редактирования товара
	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session, Model m) throws IOException {

		// ШАГ 1: ВАЛИДАЦИЯ СКИДКИ
		// Проверяем, что скидка в пределах от 0 до 100%
		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "Значения скидки должны быть от 0 до 100!");
		} else {

			// ШАГ 2: ОБНОВЛЕНИЕ ТОВАРА
			// Сервис сам обрабатывает логику обновления и сохранения изображения
			Product updateProduct = productService.updateProduct(product, image);

			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "Товар успешно изменен!");
			} else {
				session.setAttribute("errorMsg", "Что-то не так на сервере.");
			}
		}
		return "redirect:/admin/editProduct/" + product.getId();
	}
	
	// 13️. СТРАНИЦА ПОЛЬЗОВАТЕЛЕЙ
	// Отображает список всех пользователей с ролью ROLE_USER
	@GetMapping("/users")
	public String getAllUsers(Model m) {
		List<UserDtls> users = userService.getUsers("ROLE_USER");
		m.addAttribute("users", users);
		return "/admin/users";
	}
	
	// 14️. ОБНОВЛЕНИЕ СТАТУСА АККАУНТА ПОЛЬЗОВАТЕЛЯ
	// Активирует или деактивирует аккаунт пользователя
	@GetMapping("/updateSts")
	public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {
		
		Boolean f = userService.updateAccountStatus(id, status);
		
		if(f) {
			
			session.setAttribute("succMsg", "Статус пользователя обновлен!");
		}else {
			session.setAttribute("errorMsg", "Что-то не так с сервером!");
		}
		
		return "redirect:/admin/users";
	}
	
	// 15️. УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ
	// Полностью удаляет пользователя из системы
	@GetMapping("/deleteUser/{id}")
	public String deleteUser(@PathVariable Integer id, HttpSession session) {
	    Boolean deleteUser = userService.deleteUser(id);
	    
	    if(deleteUser) {
	        session.setAttribute("succMsg", "Пользователь успешно удален!");
	    } else {
	        session.setAttribute("errorMsg", "Что-то не так на сервере.");
	    }
	    
	    return "redirect:/admin/users";
	}

	// 16️. СТРАНИЦА ЗАКАЗОВ
	// Отображает список всех заказов в системе
	@GetMapping("/orders")
	public String getAllOrders(Model m) {

		List<ProductOrder> allOrders = orderService.getAllOrders();
		m.addAttribute("orders", allOrders);
		
		return "/admin/orders";
	}
	
	// 17️. ОБНОВЛЕНИЕ СТАТУСА ЗАКАЗА
	// Изменяет статус заказа (например, "В обработке" → "Отправлен")
	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		// ШАГ 1: ПОЛУЧЕНИЕ ТЕКСТОВОГО НАЗВАНИЯ СТАТУСА
		// Конвертируем числовой ID статуса в текстовое название
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
		return "redirect:/admin/orders";
	}
	
}
