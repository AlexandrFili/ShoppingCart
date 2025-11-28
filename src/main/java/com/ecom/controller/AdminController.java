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

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;
	
	private final String UPLOAD_DIR = "uploads/";
	
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

	@ModelAttribute
	public void getUsersDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}
		
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}

	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		return "admin/add_product";
	}

	@GetMapping("/category")
	public String category(Model m) {
		m.addAttribute("categorys", categoryService.getAllCategory());
		return "admin/category";
	}

	@PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {

        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);

        Boolean existsCategory = categoryService.existCategory(category.getName());

        if (existsCategory) {
            session.setAttribute("errorMsg", "Такое название категории уже существует!");
        } else {
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

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {

		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
	}
	
	 @PostMapping("/updateCategory")
	    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
	            HttpSession session) throws IOException {

	        Category oldCategory = categoryService.getCategoryById(category.getId());
	        String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

	        if (!ObjectUtils.isEmpty(category)) {
	            oldCategory.setName(category.getName());
	            oldCategory.setIsActive(category.getIsActive());
	            oldCategory.setImageName(imageName);
	        }

	        Category updateCategory = categoryService.saveCategory(oldCategory);

	        if (!ObjectUtils.isEmpty(updateCategory)) {
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

	 @PostMapping("/saveProduct")
	    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
	            HttpSession session) throws IOException {

	        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

	        product.setImage(imageName);
	        product.setDiscount(0);
	        product.setDiscountPrice(product.getPrice());
	        Product saveProduct = productService.saveProduct(product);

	        if (!ObjectUtils.isEmpty(saveProduct)) {
	            // ✅ СОХРАНЯЕМ В UPLOADS
	            saveFile(image, "product_img", image.getOriginalFilename());
	            session.setAttribute("succMsg", "Товар успешно добавлен!");
	        } else {
	            session.setAttribute("errorMsg", "Что-то не так на сервере.");
	        }
	        return "redirect:/admin/loadAddProduct";
	    }


	@GetMapping("/products")
	public String loadViewProduct(Model m) {
		m.addAttribute("products", productService.getAllProducts());
		return "admin/products";

	}

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

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/edit_product";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session, Model m) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "Значения скидки должны быть от 0 до 100!");
		} else {

			Product updateProduct = productService.updateProduct(product, image);

			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "Товар успешно изменен!");
			} else {
				session.setAttribute("errorMsg", "Что-то не так на сервере.");
			}
		}
		return "redirect:/admin/editProduct/" + product.getId();
	}
	
	@GetMapping("/users")
	public String getAllUsers(Model m) {
		List<UserDtls> users = userService.getUsers("ROLE_USER");
		m.addAttribute("users", users);
		return "/admin/users";
	}
	
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

	@GetMapping("/orders")
	public String getAllOrders(Model m) {

		List<ProductOrder> allOrders = orderService.getAllOrders();
		m.addAttribute("orders", allOrders);
		
		return "/admin/orders";
	}
	
	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		Boolean updateOrder = orderService.updateOrderStatus(id, status);

		if (updateOrder) {
			session.setAttribute("succMsg", "Статус обновился!");
		} else {
			session.setAttribute("errorMsg", "Статус не обновился!");
		}
		return "redirect:/admin/orders";
	}
	
}
