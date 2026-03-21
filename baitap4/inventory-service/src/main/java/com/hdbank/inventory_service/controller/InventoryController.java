package com.hdbank.inventory_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.hdbank.inventory_service.dto.InventoryResponse;
import com.hdbank.inventory_service.service.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public List<InventoryResponse> isInStock(
            @RequestParam List<String> skuCode) {

        return inventoryService.isInStock(skuCode);
    }
}