package com.ecom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Category;
import com.ecom.repository.CategoryRepository;
import com.ecom.service.CategoryService;

//Реализация сервиса категорий, т.е. реализация интерфейса CategoryService

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository; // Репозиторий категорий

	// 1️. РЕАЛИЗАЦИЯ: СОХРАНЕНИЕ КАТЕГОРИИ
	@Override
	public Category saveCategory(Category category) {
		return categoryRepository.save(category);
	}

	// 2️. РЕАЛИЗАЦИЯ: ПОЛУЧЕНИЕ ВСЕХ КАТЕГОРИЙ
	@Override
	public List<Category> getAllCategory() {
		return categoryRepository.findAll();
	}

	// 3️. РЕАЛИЗАЦИЯ: ПРОВЕРКА СУЩЕСТВОВАНИЯ КАТЕГОРИИ ПО ИМЕНИ
	@Override
	public Boolean existCategory(String name) {
		return categoryRepository.existsByName(name);
	}

	// 4️. РЕАЛИЗАЦИЯ: УДАЛЕНИЕ КАТЕГОРИИ
	@Override
	public Boolean deleteCategory(int id) {
		// Шаг 1: Ищем категорию по ID
		Category category = categoryRepository.findById(id).orElse(null);
		
		if(!ObjectUtils.isEmpty(category)) { // Шаг 2: Если категория найдена - удаляем
			
			categoryRepository.delete(category);
			return true;
		}
		
		return false; // Шаг 3: Если не найдена - возвращаем false
	}

	// 5️. РЕАЛИЗАЦИЯ: ПОЛУЧЕНИЕ КАТЕГОРИИ ПО ID
	@Override
	public Category getCategoryById(int id) {
		Category category = categoryRepository.findById(id).orElse(null);
		
		return category;
	}

	// 6️. РЕАЛИЗАЦИЯ: ПОЛУЧЕНИЕ ВСЕХ АКТИВНЫХ КАТЕГОРИЙ
	@Override
	public List<Category> getAllActiveCategory() {
		List<Category> categories = categoryRepository.findByIsActiveTrue();
		return categories;
	}
}
