package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.ProductOrder;

// Репозиторий для работы с заказами

//Интерфейс репозитория для сущности ProductOrder
//Наследуется от JpaRepository<ProductOrder, Integer>:
//- ProductOrder: тип сущности (заказ)
//- Integer: тип первичного ключа (у ProductOrder поле id типа Integer)
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

	// 1️. ПОЛУЧЕНИЕ ВСЕХ ЗАКАЗОВ ПОЛЬЗОВАТЕЛЯ
    // Метод возвращает список всех заказов конкретного пользователя
	List<ProductOrder> findByUserId(Integer userId);
	// "findBy" + "UserId" (поле user.id)
}