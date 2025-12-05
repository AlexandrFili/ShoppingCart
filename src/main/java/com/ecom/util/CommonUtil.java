package com.ecom.util;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

// Класс, отвечающий за отправку email и генерацию URL

@Component
public class CommonUtil {
	
	// ВНЕДРЕНИЕ ЗАВИСИМОСТИ ДЛЯ ОТПРАВКИ EMAIL
	
	// JavaMailSender - основной компонент Spring для работы с электронной почтой
	// Настраивается в файле application.properties
	@Autowired
	private JavaMailSender mailSender;
	
	// 1️. РЕАЛИЗАЦИЯ: ОТПРАВКА ПИСЬМА ДЛЯ СБРОСА ПАРОЛЯ
	// Метод отправляет HTML-письмо с ссылкой для сброса пароля
	public Boolean sendMail(String url, String reciepentEmail) throws UnsupportedEncodingException, MessagingException {
		
		// ВНЕДРЕНИЕ ЗАВИСИМОСТИ ДЛЯ ОТПРАВКИ EMAIL
		// JavaMailSender - основной компонент Spring для работы с электронной почтой
		// Настраивается в файле application.properties
		
		// ШАГ 1: СОЗДАНИЕ MIME СООБЩЕНИЯ
		// MimeMessage поддерживает HTML-контент, вложения и кодировки
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message); 
		
		// ШАГ 2: НАСТРОЙКА ОТПРАВИТЕЛЯ И ПОЛУЧАТЕЛЯ
		// Устанавливаем email отправителя и его отображаемое имя
		helper.setFrom("shopperfili@gmail.com", "Онлайн-магазин товаров");
		helper.setTo(reciepentEmail);
		
		// ШАГ 3: ФОРМИРОВАНИЕ СОДЕРЖИМОГО ПИСЬМА
		// Создаем HTML-контент письма с ссылкой для сброса пароля
		String content = "<p>Приветствую!</p>" + "<p>Вы запросили сброс пароля</p>" 
		+ "<p>Кликните по ссылке, чтобы изменить свой пароль: </p>" + "<p><a href=\"" + url
		+"\">Изменить мой пароль</a></p>";
		
		// ШАГ 4: УСТАНОВКА ТЕМЫ И ТЕЛА ПИСЬМА
		helper.setSubject("Сброс пароля");
		helper.setText(content, true);
		
		// ШАГ 5: ОТПРАВКА ПИСЬМА
		// Используем mailSender для фактической отправки email
		mailSender.send(message);
		
		return true;
	}
	
	// 2️. РЕАЛИЗАЦИЯ: ГЕНЕРАЦИЯ БАЗОВОГО URL ПРИЛОЖЕНИЯ
	// Метод извлекает базовый URL из текущего HTTP-запроса
	// Удаляет путь к текущему endpoint, оставляя только домен и порт

	public static String generateUrl(HttpServletRequest request) {
		
		// ШАГ 1: ПОЛУЧЕНИЕ ПОЛНОГО URL ЗАПРОСА
		// Пример - http://localhost:8080/forgot-password
		String siteUrl = request.getRequestURL().toString();
		
		// ШАГ 2: УДАЛЕНИЕ ПУТИ СЕРВЛЕТА
		// request.getServletPath() возвращает "/forgot-password"
		// Метод replace удаляет этот путь из полного URL
		// Результат: "http://localhost:8080" - базовый URL приложения
		return siteUrl.replace(request.getServletPath(), "");  
			
	}
}
