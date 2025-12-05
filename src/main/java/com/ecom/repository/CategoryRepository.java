package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Category;

//Репозиторий категорий 

public interface CategoryRepository extends JpaRepository<Category, Integer>{
	
	// 1️. ПРОВЕРКА СУЩЕСТВОВАНИЯ КАТЕГОРИИ ПО ИМЕНИ
	// Метод проверяет, существует ли категория с таким именем
	public Boolean existsByName(String name);
	// "existsBy" + "Name" (поле name)
	
	// 2️. ПОЛУЧЕНИЕ ВСЕХ АКТИВНЫХ КАТЕГОРИЙ
	// Метод возвращает список только активных категорий
	public List<Category> findByIsActiveTrue();
	// "findBy" + "IsActive" (поле isActive) + "True" (значение true)

}
