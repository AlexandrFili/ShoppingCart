package com.ecom.service;

import java.util.List;

import com.ecom.model.UserDtls;

//Интерфейс сервиса для работы с пользователями
public interface UserService {
	   // Очень важный сервис - отвечает за безопасность и аутентификацию
	
	// 1️. СОХРАНЕНИЕ/РЕГИСТРАЦИЯ ПОЛЬЗОВАТЕЛЯ
    // Метод создает нового пользователя или обновляет существующего
	public UserDtls saveUser(UserDtls user);
	
	// 2️. ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ ПО EMAIL
    // Метод ищет пользователя по email (для входа в систему)
	public UserDtls getUserByEmail(String email);
	
	// 3️. ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЕЙ ПО РОЛИ
    // Метод возвращает список пользователей с определенной ролью
	public List<UserDtls> getUsers(String role);

	// 4️. ОБНОВЛЕНИЕ СТАТУСА АККАУНТА
    // Метод активирует/деактивирует аккаунт пользователя
	public Boolean updateAccountStatus(Integer id, Boolean status);
	
	// 5️. ⭐ УВЕЛИЧЕНИЕ СЧЕТЧИКА НЕУДАЧНЫХ ПОПЫТОК ВХОДА
    // Метод увеличивает счетчик неудачных попыток входа
	public void increaseFailedAttempt(UserDtls user);
	
	// 6️. ⭐ БЛОКИРОВКА АККАУНТА ПОЛЬЗОВАТЕЛЯ
    // Метод блокирует аккаунт пользователя
	public void userAccountLock(UserDtls user);
	
	// 7️. ⭐ ПРОВЕРКА ИСТЕЧЕНИЯ ВРЕМЕНИ БЛОКИРОВКИ
    // Метод проверяет, истекло ли время блокировки аккаунта
	public boolean unlockAccountTimeExpired(UserDtls user);
	
	// 8️. ⭐ СБРОС СЧЕТЧИКА ПОПЫТОК
    // Метод сбрасывает счетчик неудачных попыток
	public void resetAttempt(int userId);

	// 9️. ОБНОВЛЕНИЕ ТОКЕНА ВОССТАНОВЛЕНИЯ ПАРОЛЯ
    // Метод устанавливает токен для сброса пароля
	public void updateUserResetToken(String email, String resetToken);
	
	// 10. ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ ПО ТОКЕНУ ВОССТАНОВЛЕНИЯ
    // Метод ищет пользователя по токену сброса пароля
	public UserDtls getUserByToken(String token);
	
	// 1️1. ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ
    // Метод обновляет данные пользователя
	public UserDtls updateUser(UserDtls user);

	// 1️2. УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ
    // Метод удаляет пользователя по ID
	public Boolean deleteUser(Integer id);

}
