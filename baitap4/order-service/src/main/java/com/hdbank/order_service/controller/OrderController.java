package com.hdbank.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.hdbank.order_service.dto.OrderRequest;
import com.hdbank.order_service.service.OrderService;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public String placeOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }
}
