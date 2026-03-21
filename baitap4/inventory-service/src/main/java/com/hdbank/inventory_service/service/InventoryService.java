package com.hdbank.inventory_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.hdbank.inventory_service.dto.InventoryResponse;
import com.hdbank.inventory_service.repository.InventoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repository;

    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        return repository.findBySkuCodeIn(skuCodes)
                .stream()
                .map(inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity() > 0)
                        .build())
                .toList();
    }
}
