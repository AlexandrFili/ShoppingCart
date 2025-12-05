package com.ecom.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;

//Реализация сервиса пользователей, т.е. реализация интерфейса UserService

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository; // Репозиторий пользователей
	
	@Autowired
	private PasswordEncoder passwordEncoder; // BCrypt кодировщик паролей
	
	// 1️. РЕАЛИЗАЦИЯ: СОХРАНЕНИЕ/РЕГИСТРАЦИЯ ПОЛЬЗОВАТЕЛЯ
	@Override
	public UserDtls saveUser(UserDtls user) {
		// Установка дефолтных значений
		user.setRole("ROLE_USER"); // По умолчанию обычный пользователь
		user.setIsEnable(true);  // Активен
		user.setAccountNonLocked(true); // Не заблокирован
		user.setFailedAttempt(0);  // Счетчик ошибок = 0
		
		// Хеширование пароля
	 	String encodePassword = passwordEncoder.encode(user.getPassword());
	 	user.setPassword(encodePassword);
	 	 // Сохранение в БД
		UserDtls saveUser = userRepository.save(user);
		return saveUser;
	}

	// 2️. РЕАЛИЗАЦИЯ: ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ ПО EMAIL
	@Override
	public UserDtls getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	// 3️. РЕАЛИЗАЦИЯ: ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЕЙ ПО РОЛИ
	@Override
	public List<UserDtls> getUsers(String role) {
		return userRepository.findByRole(role);
	}

	// 4️. РЕАЛИЗАЦИЯ: ОБНОВЛЕНИЕ СТАТУСА АККАУНТА
	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {
		
		// Используем Optional для безопасной работы
		Optional<UserDtls> findByUser = userRepository.findById(id);
		
		if(findByUser.isPresent()) {
			UserDtls userDtls = findByUser.get();		
			userDtls.setIsEnable(status); // Активируем/деактивируем аккаунт
			userRepository.save(userDtls);
			return true;
		}
		
		return false;
	}

	// 5️. ⭐ РЕАЛИЗАЦИЯ: УВЕЛИЧЕНИЕ СЧЕТЧИКА НЕУДАЧНЫХ ПОПЫТОК
	@Override
	public void increaseFailedAttempt(UserDtls user) {
		int attempt = user.getFailedAttempt() + 1;
		user.setFailedAttempt(attempt);
		userRepository.save(user);
		
	}

	// 6️. ⭐ РЕАЛИЗАЦИЯ: БЛОКИРОВКА АККАУНТА ПОЛЬЗОВАТЕЛЯ
	@Override
	public void userAccountLock(UserDtls user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepository.save(user);
		
	}

	// 7️. ⭐ РЕАЛИЗАЦИЯ: ПРОВЕРКА ИСТЕЧЕНИЯ ВРЕМЕНИ БЛОКИРОВКИ
	@Override
	public boolean unlockAccountTimeExpired(UserDtls user) {
		
		long lockTime = user.getLockTime().getTime();
		long unLockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
		
		long currentTime = System.currentTimeMillis();
		
		// Если время разблокировки наступило
		if (unLockTime < currentTime) {
			// Разблокируем аккаунт
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepository.save(user);
			return true; // Аккаунт разблокирован
			
		}
		
		return false; // Время блокировки еще не истекло
	}

	// 8️. ⭐ РЕАЛИЗАЦИЯ: СБРОС СЧЕТЧИКА ПОПЫТОК
	@Override
	public void resetAttempt(int userId) {
		// TODO Auto-generated method stub
		
	}

	// 9️. РЕАЛИЗАЦИЯ: ОБНОВЛЕНИЕ ТОКЕНА ВОССТАНОВЛЕНИЯ ПАРОЛЯ
	@Override
	public void updateUserResetToken(String email, String resetToken) {
		UserDtls findByEmail = userRepository.findByEmail(email);
		findByEmail.setResetToken(resetToken);
		userRepository.save(findByEmail);
		
	}

	// 10. РЕАЛИЗАЦИЯ: ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ ПО ТОКЕНУ ВОССТАНОВЛЕНИЯ
	@Override
	public UserDtls getUserByToken(String token) {
		return userRepository.findByResetToken(token);
	}

	// 1️1. РЕАЛИЗАЦИЯ: ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ
	@Override
	public UserDtls updateUser(UserDtls user) {
		return userRepository.save(user);
	}

	// 1️2. РЕАЛИЗАЦИЯ: УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ
	@Override
	public Boolean deleteUser(Integer id) {
		UserDtls user = userRepository.findById(id).orElse(null);

		if (!ObjectUtils.isEmpty(user)) {
			userRepository.delete(user);
			return true;
		}

		return false;
	}
	
}
