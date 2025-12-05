package com.ecom.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.ecom.model.UserDtls; // Модель пользователя
import com.ecom.repository.UserRepository; // Репозиторий для работы с пользователями в БД 
import com.ecom.service.UserService; // Сервис с бизнес-логикой пользователей
import com.ecom.util.AppConstant; // Константы приложения

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//Данный класс - это обработчик неудачной аутентификации

@Component // @Component - аннотация Spring, отмечающая класс как компонент для автоматического обнаружения
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {
	// Наследуется от стандартного обработчика неудачной аутентификации Spring Security
	
    @Autowired // Внедрение зависимости Spring (автоматическое создание и подключение объекта)
    private UserRepository userRepository; // Для поиска пользователя в базе данных

    @Autowired
    private UserService userService; // Для вызова методов бизнес-логики (блокировка, подсчет попыток)

    // Переопределение метода, который вызывается при неудачной попытке входа
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("username"); // Получаем email из параметра запроса 
        UserDtls userDtls = userRepository.findByEmail(email);  // Ищем пользователя по email в базе данных

        // ✅ КРИТИЧЕСКИ ВАЖНАЯ ПРОВЕРКА НА NULL
        // Если пользователь не найден (неправильный email)
        if (userDtls == null) { 
            exception = new LockedException("Пользователь с таким email не найден!"); // Создаем специальное исключение с понятным сообщением
            super.setDefaultFailureUrl("/signin?error=user_not_found");  // Устанавливаем URL для перенаправления с параметром ошибки
            super.onAuthenticationFailure(request, response, exception); // Вызываем родительский метод для обработки
            return;  // Завершаем выполнение, т.к. пользователя нет
        }

        // Если пользователь найден, продолжаем проверки
        if (userDtls.getIsEnable()) { // 1️-ая проверка: активирован ли аккаунт пользователя (через email подтверждение)

            if (userDtls.getAccountNonLocked()) { // 2️-ая проверка: не заблокирован ли уже аккаунт

                if (userDtls.getFailedAttempt() < AppConstant.ATTEMPT_TIME) { // 3️-ая проверка: не превышено ли количество допустимых неудачных попыток
                    userService.increaseFailedAttempt(userDtls); // Увеличиваем счетчик неудачных попыток
                    int remainingAttempts = AppConstant.ATTEMPT_TIME - userDtls.getFailedAttempt(); // Вычисляем оставшиеся попытки
                    // Создаем сообщение об ошибке с информацией об оставшихся попытках
                    exception = new LockedException("Неверный email или пароль! Осталось попыток: " + remainingAttempts);
                    // Перенаправляем на страницу входа с параметрами ошибки и количеством попыток
                    super.setDefaultFailureUrl("/signin?error=invalid_credentials&attempts=" + remainingAttempts);
                } else { // Если попытки исчерпаны - БЛОКИРУЕМ аккаунт
                    userService.userAccountLock(userDtls);
                    exception = new LockedException("Ваш аккаунт заблокирован! Больше 3-х попыток с неверным логином или паролем!");
                    super.setDefaultFailureUrl("/signin?error=account_locked");
                }
            } else { // Аккаунт УЖЕ заблокирован ранее
                
                if (userService.unlockAccountTimeExpired(userDtls)) { // 4️-ая: Проверяем, истекло ли время блокировки
                	 // Время блокировки истекло - аккаунт разблокирован автоматически
                    exception = new LockedException("Ваш аккаунт разблокирован! Пожалуйста, введите данные еще раз!");
                    super.setDefaultFailureUrl("/signin?error=account_unlocked");
                } else {  // Аккаунт все еще заблокирован
                    exception = new LockedException("Ваш аккаунт заблокирован! Попробуйте зайти позже.");
                    super.setDefaultFailureUrl("/signin?error=account_locked");
                }
            }
        } else { // Аккаунт в бане 
            exception = new LockedException("Ваш аккаунт заблокирован!");
            super.setDefaultFailureUrl("/signin?error=account_disabled");
        }
        
        // Вызываем родительский метод для завершения обработки
        super.onAuthenticationFailure(request, response, exception);
    }
}
