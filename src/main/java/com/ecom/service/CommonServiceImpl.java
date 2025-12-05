package com.ecom.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

// Реализация общего сервиса

@Service // Аннотация @Service - Spring автоматически создаст бин этого класса
//и будет внедрять его там, где это нужно (через @Autowired)
public class CommonServiceImpl implements CommonService {
	// Реализация интерфейса CommonService
    // Ключевое слово 'implements' означает, что класс реализует все методы интерфейса
	
	// 1️. РЕАЛИЗАЦИЯ МЕТОДА УДАЛЕНИЯ СООБЩЕНИЙ ИЗ СЕССИИ
	@Override // Аннотация указывает, что метод переопределяет метод интерфейса
	public void removeSessionMessage() {
		
		// Шаг 1: Получение текущего HTTP запроса
        // RequestContextHolder - утилитный класс Spring, который хранит контекст текущего запроса
        // ServletRequestAttributes - содержит атрибуты Servlet запроса
		HttpServletRequest request = ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())) // Получаем атрибуты запроса
				.getRequest();  // Извлекаем сам HTTP запрос
		
		// Шаг 2: Получение сессии из запроса
        // Сессия хранит данные между запросами одного пользователя
		HttpSession session = request.getSession(); // getSession() возвращает существующую сессию или создает новую
		
		// Шаг 3: Удаление flash-сообщений из сессии
        // Flash-сообщения - это сообщения, которые показываются один раз
		session.removeAttribute("succMsg"); // Удаляем сообщение об успехе
		session.removeAttribute("errorMsg"); // Удаляем сообщение об ошибке

	}

	
	
}
