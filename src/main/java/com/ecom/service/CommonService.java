package com.ecom.service;

import org.springframework.stereotype.Service;

//Общий сервис для утилитных/вспомогательных операций

@Service
public interface CommonService {
	
	// 1️. УДАЛЕНИЕ СООБЩЕНИЙ ИЗ СЕССИИ
    // Метод удаляет flash-сообщения из HTTP сессии
	public void removeSessionMessage();

}
