package com.hdbank.order_service.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

import com.hdbank.order_service.dto.InventoryResponse;
import com.hdbank.order_service.dto.OrderRequest;
import com.hdbank.order_service.entity.Order;
import com.hdbank.order_service.entity.OrderLineItems;
import com.hdbank.order_service.repository.OrderRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

        private final OrderRepository orderRepository;
        private final WebClient.Builder webClientBuilder;
        private final KafkaTemplate<String, String> kafkaTemplate;

        @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
        public String placeOrder(OrderRequest request) {

                Order order = new Order();
                order.setOrderNumber(UUID.randomUUID().toString());

                List<OrderLineItems> items = request.getOrderLineItemsDtoList()
                                .stream()
                                .map(dto -> OrderLineItems.builder()
                                                .skuCode(dto.getSkuCode())
                                                .price(dto.getPrice())
                                                .quantity(dto.getQuantity())
                                                .build())
                                .toList();

                order.setOrderLineItemsList(items);

                List<String> skuCodes = items.stream()
                                .map(OrderLineItems::getSkuCode)
                                .toList();

                // CALL INVENTORY SERVICE
                InventoryResponse[] responses = webClientBuilder.build()
                                .get()
                                .uri("http://inventory-service/api/inventory",
                                                uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                                .retrieve()
                                .bodyToMono(InventoryResponse[].class)
                                .block();

                boolean allInStock = responses != null &&
                                Arrays.stream(responses)
                                                .allMatch(InventoryResponse::isInStock);

                if (allInStock) {
                        orderRepository.save(order);

                        // SEND KAFKA
                        kafkaTemplate.send("notificationTopic", "Order Placed: " + order.getOrderNumber());

                        return "Order Placed";
                } else {
                        throw new IllegalArgumentException("Product not in stock");
                }
        }

        public String fallbackMethod(OrderRequest request, Throwable throwable) {
                return "Inventory Service is down. Try again later";
        }
}