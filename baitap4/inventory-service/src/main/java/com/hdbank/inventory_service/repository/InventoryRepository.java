package com.hdbank.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hdbank.inventory_service.entity.Inventory;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findBySkuCodeIn(List<String> skuCodes);
}