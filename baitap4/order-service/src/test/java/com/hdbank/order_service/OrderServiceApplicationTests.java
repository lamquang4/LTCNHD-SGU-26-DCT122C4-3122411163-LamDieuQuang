package com.hdbank.order_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdbank.order_service.entity.Order;
import com.hdbank.order_service.repository.OrderRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderServiceApplicationTests {

	@Autowired
	OrderRepository orderRepository;

	@Test
	void testSaveOrder() {
		Order order = Order.builder()
				.orderNumber("123")
				.build();

		orderRepository.save(order);

		assertThat(orderRepository.findAll()).isNotEmpty();
	}
}
