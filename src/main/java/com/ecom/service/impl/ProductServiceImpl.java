package com.ecom.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired 
    private ProductRepository productRepository;
    
    private final String UPLOAD_DIR = "uploads/";

    // Метод без изображения (для обратной совместимости)
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    // Метод с изображением
    public Product saveProductWithImage(Product product, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            String imageName = image.getOriginalFilename();
            product.setImage(imageName);
            
            // Сохраняем файл
            Path path = Paths.get(UPLOAD_DIR + "product_img/" + imageName);
            Files.createDirectories(path.getParent());
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("✅ Изображение товара сохранено при создании: " + path.toAbsolutePath());
        }
        
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @Override
    public Boolean deleteProduct(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (!ObjectUtils.isEmpty(product)) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product updateProduct(Product product, MultipartFile image) throws IOException {
        // Шаг 1: Получаем существующий товар
        Product dbProduct = getProductById(product.getId());
        
        if (dbProduct == null) {
            return null;
        }
        
        // Шаг 2: Определяем имя изображения
        String imageName;
        if (image != null && !image.isEmpty()) {
            imageName = image.getOriginalFilename();
        } else {
            imageName = dbProduct.getImage();
        }
        
        // Шаг 3: Обновляем поля товара
        dbProduct.setTitle(product.getTitle());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setCategory(product.getCategory());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setStock(product.getStock());
        dbProduct.setImage(imageName);
        dbProduct.setIsActive(product.getIsActive());
        dbProduct.setDiscount(product.getDiscount());
        
        // Шаг 4: Рассчитываем цену со скидкой
        Double discount = product.getPrice() * (product.getDiscount() / 100.0);
        Double discountPrice = product.getPrice() - discount;
        dbProduct.setDiscountPrice(discountPrice);

        // Шаг 5: Сохраняем в БД
        Product updateProduct = productRepository.save(dbProduct);

        // Шаг 6: Сохраняем файл на диск (если есть новое изображение)
        if (updateProduct != null && image != null && !image.isEmpty()) {
            Path path = Paths.get(UPLOAD_DIR + "product_img/" + imageName);
            Files.createDirectories(path.getParent());
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("✅ Изображение товара сохранено: " + path.toAbsolutePath());
        }
        
        return updateProduct;
    }

    @Override
    public List<Product> getAllActiveProducts(String category) {
        if (ObjectUtils.isEmpty(category)) {
            return productRepository.findByIsActiveTrue();
        } else {
            return productRepository.findByCategory(category);
        }
    }
}