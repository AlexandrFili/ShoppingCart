package com.ecom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Для статических ресурсов (CSS, JS, default images)
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");
        
        // ✅ ДЛЯ ЗАГРУЖАЕМЫХ ФАЙЛОВ
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}