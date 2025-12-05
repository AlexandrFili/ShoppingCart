package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.UserDtls;

//Репозиторий пользователей

//Интерфейс репозитория для сущности UserDtls
//Наследуется от JpaRepository<UserDtls, Integer>:
//- UserDtls: тип сущности (пользователь)
//- Integer: тип первичного ключа (у UserDtls поле id типа Integer)
public interface UserRepository extends JpaRepository<UserDtls, Integer> {

	// 1️. ПОИСК ПОЛЬЗОВАТЕЛЯ ПО EMAIL (основной метод для входа)
    // Метод ищет пользователя по email
	public UserDtls findByEmail(String email);
	// "findBy" + "Email" (поле email)

	// 2️. ПОИСК ПОЛЬЗОВАТЕЛЕЙ ПО РОЛИ
    // Метод возвращает список пользователей с определенной ролью
	public List<UserDtls> findByRole(String role);
	// "findBy" + "Role" (поле role)
	
	// 3️. ПОИСК ПО ТОКЕНУ ВОССТАНОВЛЕНИЯ ПАРОЛЯ
    // Метод ищет пользователя по токену сброса пароля
	public UserDtls findByResetToken(String token);
	// "findBy" + "ResetToken" (поле resetToken)
}
