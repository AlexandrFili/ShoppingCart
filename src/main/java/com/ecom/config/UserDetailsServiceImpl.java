package com.ecom.config;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;

//Данный класс это сервис загрузки пользователей для Spring Security, другими словами это связующее звено между БД и Spring Security

@Service
public class UserDetailsServiceImpl implements UserDetailsService { // Реализует интерфейс UserDetailsService из Spring Security

	// 1️. Внедрение зависимости - репозиторий для доступа к таблице пользователей
	@Autowired
	private UserRepository userRepository; // Spring автоматически создаст и подключит экземпляр UserRepository

	// 2️. ⭐ ГЛАВНЫЙ МЕТОД: Загрузка пользователя по email пользователя 
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// Ищем пользователя в БД по email
		// Здесь 'username' = email пользователя
		UserDtls user = userRepository.findByEmail(username);

		// 3️. Проверка: найден ли пользователь
		if (user == null) {  // Если пользователь не найден - бросаем исключение
			throw new UsernameNotFoundException("Пользователь не найден!"); // Это исключение перехватит AuthFailureHandlerImpl и покажет сообщение
		} // 4️. Если пользователь найден - оборачиваем его в CustomUser
		return new CustomUser(user); //CustomUser "переводит" модель UserDtls на язык Spring Security
	}

}
