package com.beaconfire.coreservice.dao;

import com.beaconfire.coreservice.exception.NotEnoughInventoryException;
import com.beaconfire.coreservice.exception.NotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class InventoryDao {
    private final SessionFactory sessionFactory;
    public InventoryDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /** check the number of updated rows to validate purchase */
    public void deductInventory(Long productId, Integer quantity) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();
        int updatedCount = 0; // To store the number of updated rows

        try {
            updatedCount = session
                    .createQuery("update Inventory set quantity = quantity - :quantity where productId = :productId")
                    .setParameter("quantity", quantity)
                    .setParameter("productId", productId)
                    .executeUpdate();

             if (updatedCount == 0) {
                 throw new NotFoundException("Product with ID " + productId + " not found in inventory.");
             }

        } catch (Exception e) {
            throw new NotEnoughInventoryException("Not enough inventory or other update error with Product ID: " + productId);
        }
    }

    public void addInventory(Long productId, Integer quantity){
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("update Inventory set quantity = quantity + :quantity where productId = :productId")
                .setParameter("quantity", quantity)
                .setParameter("productId", productId)
                .executeUpdate();
    }
}
