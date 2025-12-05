package com.ecom.service;

import java.io.IOException;
import java.util.List;

import com.ecom.model.Product;
import org.springframework.web.multipart.MultipartFile;

//Интерфейс сервиса для работы с товарами
public interface ProductService {
	
	// 1️. СОЗДАНИЕ/СОХРАНЕНИЕ ТОВАРА
    // Метод создает новый товар или сохраняет существующий
	public Product saveProduct(Product product) throws IOException; 
	
	// 2️. ПОЛУЧЕНИЕ ВСЕХ ТОВАРОВ
    // Метод возвращает список ВСЕХ товаров
	public List<Product> getAllProducts();
	
	// 3️. УДАЛЕНИЕ ТОВАРА
    // Метод удаляет товар по ID
	public Boolean deleteProduct(Integer id);
	
	// 4️. ПОЛУЧЕНИЕ ТОВАРА ПО ID
    // Метод возвращает товар по его ID
	public Product getProductById(Integer id);
	
	// 5️. ОБНОВЛЕНИЕ ТОВАРА С ИЗОБРАЖЕНИЕМ
    // Метод обновляет товар с возможностью загрузки изображения
	public Product updateProduct(Product product, MultipartFile file) throws IOException;

	// 6️. ПОЛУЧЕНИЕ АКТИВНЫХ ТОВАРОВ (С ФИЛЬТРАЦИЕЙ ПО КАТЕГОРИИ)
    // Метод возвращает активные товары, опционально фильтруя по категории
	public List<Product> getAllActiveProducts(String category);
}
