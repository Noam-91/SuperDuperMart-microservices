package com.beaconfire.coreservice.dao;

import com.beaconfire.coreservice.domain.Order;
import com.beaconfire.coreservice.domain.OrderProduct;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class OrderDao {
    private final SessionFactory sessionFactory;
    public OrderDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Long addOrder(Order order){
        Session session = sessionFactory.getCurrentSession();
        session.persist(order);
        return order.getOrderId();
    }

    public void updateStatusByOrderId(Long orderId, String status, Long updatedBy){
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("update Order set status = :status, updatedBy=:updatedBy where orderId = :orderId")
                .setParameter("status", status)
                .setParameter("updatedBy", updatedBy)
                .setParameter("orderId", orderId)
                .executeUpdate();
    }

    public Optional<Order> findById(Long orderId){
        Session session = sessionFactory.getCurrentSession();
        return Optional.ofNullable(session.get(Order.class, orderId));
    }

    public List<Order> getOrdersByUserId(Long orderId){
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Order where userId = :userId order by createdAt desc ", Order.class)
                .setParameter("userId", orderId)
                .list();
    }

    public List<Order> findAll(){
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Order order by createdAt desc", Order.class)
                .list();
    }

    public List<Long> getMostFrequentPurchasedProducts(Integer num, Long userId){
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT op.productId FROM Order o JOIN o.orderProducts op WHERE o.userId = :userId AND o.status <> 'CANCELLED' GROUP BY op.productId ORDER BY count(op.productId) DESC", Long.class)
                .setParameter("userId", userId)
                .setMaxResults(num)
                .list();
    }

    public List<Long> getMostRecentPurchasedProducts(Integer num, Long userId){
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT op.productId FROM Order o JOIN o.orderProducts op WHERE o.userId = :userId AND o.status <> 'CANCELLED' ORDER BY o.createdAt DESC", Long.class)
                .setParameter("userId", userId)
                .setMaxResults(num)
                .list();
    }

    public List<Long> getMostPopularProducts(Integer num){
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT op.productId FROM Order o JOIN o.orderProducts op WHERE o.status = 'COMPLETED' ORDER BY sum(op.productId) DESC", Long.class)
                .setMaxResults(num)
                .list();
    }

    public List<Order> getAllCompleted(){
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Order where status = 'COMPLETED'", Order.class)
                .list();
    }
}
