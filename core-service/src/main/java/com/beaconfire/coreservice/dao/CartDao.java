package com.beaconfire.coreservice.dao;

import com.beaconfire.coreservice.domain.CartProduct;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CartDao {
    SessionFactory sessionFactory;
    public CartDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<CartProduct> getAllCartProductByUserId(Long userId){
        return sessionFactory.getCurrentSession().createQuery("from CartProduct where userId = :userId", CartProduct.class)
                .setParameter("userId", userId).list();
    }

    public void deleteCartProduct(Long userId, Long productId){
        int updatedCount = sessionFactory.getCurrentSession()
                .createQuery("delete from CartProduct where userId = :userId AND productId = :productId")
                .setParameter("userId", userId)
                .setParameter("productId", productId)
                .executeUpdate();
        if(updatedCount == 0){
            throw new RuntimeException("Delete product failed. Product ID " + productId + " not found in cart.");
        }
    }

    public Optional<CartProduct> getCartProductByUserIdAndProductId(Long userId, Long productId){
        return sessionFactory.getCurrentSession().createQuery("from CartProduct where userId = :userId AND productId = :productId", CartProduct.class)
                .setParameter("userId", userId)
                .setParameter("productId", productId)
                .uniqueResultOptional();
    }

    public void addCartProduct(CartProduct cartProduct){
        sessionFactory.getCurrentSession().persist(cartProduct);
    }

    public void update(Long userId, Long productId, Integer quantity){
        int updatedCount = sessionFactory.getCurrentSession()
                .createQuery("update CartProduct set quantity = :quantity where userId = :userId AND productId = :productId")
                .setParameter("quantity", quantity)
                .setParameter("userId", userId)
                .setParameter("productId", productId)
                .executeUpdate();
        if(updatedCount == 0){
            throw new RuntimeException("Update product failed. Product ID " + productId + " not found in cart.");
        }
    }
}
