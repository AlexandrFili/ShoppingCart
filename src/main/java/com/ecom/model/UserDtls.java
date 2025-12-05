package com.ecom.model;

import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Данный класс представляет собой модель пользователя

//Lombok аннотации:
@AllArgsConstructor  // Генерирует конструктор со всеми полями
@NoArgsConstructor   // Генерирует конструктор без аргументов (обязателен для JPA)
@Getter              // Генерирует геттеры для всех полей
@Setter              // Генерирует сеттеры для всех полей
@Entity              // Помечает класс как JPA сущность (таблица в БД)
public class UserDtls {
	
	// 1️. ПЕРВИЧНЫЙ КЛЮЧ
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Стратегия генерации ID: IDENTITY = автоинкремент в БД
	private Integer id;
	
	// 2️. ОСНОВНАЯ ИНФОРМАЦИЯ
	
	private String name; // Полное имя пользователя
	
	private String mobileNumber; // Мобильный телефон
	
	private String email; // Email (используется как логин)
	
	// 3️. АДРЕС ПОЛЬЗОВАТЕЛЯ 
	
	private String address; // Адрес проживания
	
	private String city;    // Населеленный пункт
	
	private String region;  // Область/регион/край
	
	private String pincode; // Почтовый индекс
	
	// 4️. БЕЗОПАСНОСТЬ И АУТЕНТИФИКАЦИЯ
	
	private String password; // Хешированный пароль (BCrypt) // Никогда не хранится в открытом виде!
	
	private String profileImage; // Аватарка/фото профиля
	
	private String role; // Роль пользователя в системе
	// Пример: "ROLE_USER", "ROLE_ADMIN"
    // Определяет права доступа (см. SecurityConfig)
	
	// 5️. ⭐ СТАТУСЫ АККАУНТА (очень важно!)
	
	private Boolean isEnable; // Активирован ли аккаунт
	// true = аккаунт подтвержден 
    // false = не подтвержден, доступ запрещен
    // Используется в CustomUser.isEnabled()
	
	private Boolean accountNonLocked; // Не заблокирован ли аккаунт
	// true = не заблокирован
    // false = заблокирован (после 3 неудачных попыток входа)
    // Используется в CustomUser.isAccountNonLocked()
	
	// 6️. ⭐ ЗАЩИТА ОТ BRUTE-FORCE АТАК
	
	private Integer failedAttempt; // Счетчик неудачных попыток входа
	// Пример: 0, 1, 2, 3
    // При достижении лимита 3 аккаунт блокируется
    // Используется в AuthFailureHandlerImpl
	
	private Date lockTime; // Время блокировки аккаунта (см. AppConstant.java в com.ecom.util)
	// Когда аккаунт был заблокирован
    // Используется для автоматической разблокировки через время
	
	// 7️. ВОССТАНОВЛЕНИЕ ПАРОЛЯ
	
	private String resetToken;  // Токен для сброса пароля
	// Генерируется при запросе "Забыли пароль?"
    // Используется для верификации в ссылке сброса (через письмо на электронную почту)
    // Пример: "a1b2c3d4-e5f6-7890..."

}
