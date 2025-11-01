package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.service.CategoryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

	@GetMapping("/loadAddProduct")
	public String loadAddProduct() {
		return "admin/add_product";
	}

	@GetMapping("/category")
	public String category() {
		return "admin/category";
	}

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

		String imageName = file != null ? file.getOriginalFilename(): "default.jpg";	
		category.setImageName(imageName);
		
		Boolean existsCategory = categoryService.existCategory(category.getName());

		if (existsCategory) {
			session.setAttribute("errorMsg", "Такое название категории уже существует!");
		} else {

			Category saveCategory = categoryService.saveCategory(category);
			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Не сохранилось! Внутренняя ошибка сервера");
			} else {
				
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+file.getOriginalFilename());
				
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				session.setAttribute("succMsg", "Категории успешно сохранились");
			}
		}

		return "redirect:/admin/category";
	}

}
