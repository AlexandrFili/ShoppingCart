package com.ecom.config;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecom.model.UserDtls;

//Реализует интерфейс UserDetails из Spring Security
//Это ОБЯЗАТЕЛЬНЫЙ класс для интеграции собственной модели (UserDtls) пользователя со Spring Security
public class CustomUser implements UserDetails{
	
	private UserDtls user; // Ссылка на доменный объект пользователя

	
	// Конструктор - принимает объект UserDtls
	public CustomUser(UserDtls user) {
		super();
		this.user = user;
	}

	// 1️-ый МЕТОД: Получение прав (ролей) пользователя
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() { 
		// Преобразуем строковую роль из UserDtls в объект Spring Security
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
		return Arrays.asList(authority); // Возвращаем список с одной ролью 
	}

	// 2️-ой МЕТОД: Получение пароля для проверки
	@Override
	public String getPassword() {
		return user.getPassword(); // Возвращает зашифрованный пароль из БД
	}

	// 3️-ий МЕТОД: Получение Email пользователя 
	@Override
	public String getUsername() {
		return user.getEmail(); // Используется как login для аутентификации
	}
	
	// 4️-ый МЕТОД: Аккаунт не просрочен?
	@Override
	public boolean isAccountNonExpired() {
		return true; // Всегда true, если не реализована функциональность срока действия аккаунта
	}
	
	// 5️-ый МЕТОД: Аккаунт не заблокирован?
	// ⚠️ КРИТИЧЕСКИ ВАЖНО: связан с AuthFailureHandlerImpl!
	@Override
	public boolean isAccountNonLocked() { // Возвращает статус блокировки из БД
		return user.getAccountNonLocked();
		// Если false → Spring Security НЕ пропустит пользователя
        // Это используется в механизме блокировки после 3 неудачных попыток
	}
	
	// 6️-ой МЕТОД: Учетные данные не просрочены?
	@Override
	public boolean isCredentialsNonExpired() {
		return true; // Всегда true, если не реализована смена пароля по сроку 
	}
	
	// 7️-ой МЕТОД: Аккаунт включен/активирован?
	@Override
	public boolean isEnabled() {
		return user.getIsEnable(); // Проверка активации email
		// Если false → пользователь не подтвердил email, доступ запрещен
	}

}
