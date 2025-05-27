package com.beaconfire.coreservice.dao;

import com.beaconfire.coreservice.domain.Payment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional
@Repository
public class PaymentDao {
    private final SessionFactory sessionFactory;
    public PaymentDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addPayment(Payment payment){
        Session session = sessionFactory.getCurrentSession();
        session.persist(payment);
    }

    public void updateStatusByOrderId(Long orderId, String status){
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("update Payment set status = :status where orderId = :orderId")
                .setParameter("status", status)
                .setParameter("orderId", orderId)
                .executeUpdate();
    }

    public Payment findByPaymentIntentId(String paymentIntentId){
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Payment where paymentIntentId = :paymentIntentId", Payment.class)
                .setParameter("paymentIntentId", paymentIntentId)
                .uniqueResult();
    }

    public Payment findById(Long paymentId){
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Payment where paymentId = :paymentId", Payment.class)
                .setParameter("paymentId", paymentId)
                .uniqueResult();
    }

    public void save(Payment payment){
        Session session = sessionFactory.getCurrentSession();
        session.merge(payment);
    }
}
