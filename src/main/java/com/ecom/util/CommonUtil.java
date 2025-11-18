package com.ecom.util;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public Boolean sendMail(String url, String reciepentEmail) throws UnsupportedEncodingException, MessagingException {
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message); 
		
		helper.setFrom("shopperfili@gmail.com", "Онлайн-магазин товаров");
		helper.setTo(reciepentEmail);
		
		String content = "<p>Приветствую!</p>" + "<p>Вы запросили сброс пароля</p>" 
		+ "<p>Кликните по ссылке, чтобы изменить свой пароль: </p>" + "<p><a href=\"" + url
		+"\">Изменить мой пароль</a></p>";
		helper.setSubject("Сброс пароля");
		helper.setText(content, true);
		mailSender.send(message);
		
		return true;
	}

	public static String generateUrl(HttpServletRequest request) {
		
		// http://localhost:8080/forgot-password
		String siteUrl = request.getRequestURL().toString();
		
		return siteUrl.replace(request.getServletPath(), "");  
			
	}
}
