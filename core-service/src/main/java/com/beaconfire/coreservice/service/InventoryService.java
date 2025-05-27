package com.beaconfire.coreservice.service;

import com.beaconfire.coreservice.dao.InventoryDao;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private final InventoryDao inventoryDao;
    public InventoryService(InventoryDao inventoryDao){
        this.inventoryDao = inventoryDao;
    }

    public void deductInventory(Long productId, Integer quantity) throws RuntimeException{
        inventoryDao.deductInventory(productId, quantity);
    }

    public void addInventory(Long productId, Integer quantity){
        inventoryDao.addInventory(productId, quantity);
    }
}
