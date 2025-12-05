package com.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.server.ui.LoginPageGeneratingWebFilter;

//Класс конфигурации безопасности Spring Security

@Configuration
public class SecurityConfig { // Главный класс, который настраивает ВСЮ систему безопасности
	
	// 1️. Внедрение обработчика УСПЕШНОЙ аутентификации
	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	// Это наш AuthSuccessHandlerImpl - определяет, куда перенаправлять после входа
	
	// 2️. Внедрение обработчика НЕУДАЧНОЙ аутентификации
	@Autowired
	@Lazy // @Lazy - создание отложено до первого использования (решает циклические зависимости)
	private AuthFailureHandlerImpl authenticationFailureHandler;
	 // Это наш AuthFailureHandlerImpl - обрабатывает неправильные логины/пароли
	
	// 3️. БИН: Кодировщик паролей (BCrypt)
	@Bean // Spring создаст этот бин и будет внедрять его где нужно
	public PasswordEncoder passwordEncoder() {
		// BCrypt - самый безопасный алгоритм хеширования паролей
		return new BCryptPasswordEncoder();
		// Автоматически добавляет "соль" (salt), делает хеши уникальными
        // Пример хеша: "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
	}
	
	// 4️. БИН: Сервис для загрузки пользовательских данных
	@Bean
	public UserDetailsService userDetailsService() {
		// Создаем экземпляр нашего UserDetailsServiceImpl
        // Этот класс загружает пользователей из БД по email
		return new UserDetailsServiceImpl();
	}
	
	// 5️. БИН: Провайдер аутентификации
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		// Настраиваем провайдер:
		authenticationProvider.setUserDetailsService(userDetailsService());  // Указываем, КАК загружать пользователей
		authenticationProvider.setPasswordEncoder(passwordEncoder());  // Указываем, КАК проверять пароли (сравнивать хеши)
		return authenticationProvider; 
		// Этот провайдер будет использоваться Spring Security
        // для проверки логина/пароля
	} 
	
	// 6️. ⚡ САМЫЙ ВАЖНЫЙ БИН: Цепочка фильтров безопасности
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {  // HttpSecurity - главный объект для настройки безопасности HTTP
		http.csrf(crsf->crsf.disable()).cors(cors->cors.disable()) // Отключаем CSRF защиту и CORS (для упрощения)
		
			// ⭐ НАСТРОЙКА ДОСТУПА ПО URL (AUTHORIZATION) ⭐
			.authorizeHttpRequests(req->req.requestMatchers("/user/**").hasRole("USER") // Все URL, начинающиеся с /user/ требуют роли USER
			.requestMatchers("/admin/**").hasRole("ADMIN") // Все URL, начинающиеся с /admin/ требуют роли ADMIN
			.requestMatchers("/**").permitAll()) // Все остальные URL доступны всем (даже неавторизованным)
			
			// ⭐ НАСТРОЙКА ФОРМЫ ВХОДА ⭐
			.formLogin(form->form.loginPage("/signin") // Кастомная страница входа (не стандартная Spring)
			.loginProcessingUrl("/login") // URL для отправки формы (POST)
//			.defaultSuccessUrl("/"))
			
			 // Подключаем наши кастомные обработчики:
			.failureHandler(authenticationFailureHandler) // При ошибке входа
			.successHandler(authenticationSuccessHandler)) // При успешном входе
			
			// ⭐ НАСТРОЙКА ВЫХОДА ⭐
			.logout(logout->logout.permitAll()); // Разрешаем всем доступ к logout
		
		return http.build(); // Собираем и возвращаем цепочку фильтров
	}

}
