package com.ecom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//Данный класс - есть ни что иное, как Конфигуратор обработки статических файлов, то есть он настраивает
//как Spring MVC будет обслуживать статические файлы (изображения, CSS, JS) и загруженные файлы.

@Configuration
public class MvcConfig implements WebMvcConfigurer { // Реализует интерфейс WebMvcConfigurer для настройки Spring MVC
    
	 // Переопределение метода для настройки обработчиков ресурсов
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { // ResourceHandlerRegistry - реестр для регистрации обработчиков статических ресурсов
        // Для статических ресурсов (CSS, JS, default images)
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");
        
        // ✅ ДЛЯ ЗАГРУЖАЕМЫХ ФАЙЛОВ (динамические картинки - товары/категори и т.п.)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}