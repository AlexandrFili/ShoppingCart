package com.ecom.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//Данный класс определяет, куда перенаправить пользователя после успешного входа в зависимости от его роли 

@Service // @Service - аннотация Spring, отмечающая класс как сервисный компонент
public class AuthSuccessHandlerImpl implements AuthenticationSuccessHandler{   // Реализует интерфейс AuthenticationSuccessHandler из Spring Security

	// Переопределение метода, который вызывается при УСПЕШНОЙ аутентификации
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		// 1️. Получаем коллекцию прав (ролей) аутентифицированного пользователя
        // GrantedAuthority - интерфейс, представляющий право/роль пользователя
		Collection <? extends GrantedAuthority> authorities = authentication.getAuthorities();
		
		// 2️. Конвертируем коллекцию прав в Set строк для удобной работы
        // AuthorityUtils - утилитный класс Spring Security для работы с правами
		Set<String> roles = AuthorityUtils.authorityListToSet(authorities);
		
		// 3️. Проверяем, содержит ли пользователь роль ADMIN
        // Это КЛЮЧЕВОЙ момент для РОЛЕВОЙ МОДЕЛИ ДОСТУПА
		if (roles.contains("ROLE_ADMIN")) { // Если пользователь АДМИНИСТРАТОР - перенаправляем в админ-панель
			response.sendRedirect("/admin/");
		}else {
			response.sendRedirect("/"); // Если пользователь ОБЫЧНЫЙ (или любая другая роль) - перенаправляем на главную
		}
		
		// Метод завершается, Spring Security продолжает свою работу
        // Пользователь теперь аутентифицирован и будет перенаправлен в нужное место
		
	}

}
