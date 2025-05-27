package com.beaconfire.coreservice.service;

import com.beaconfire.coreservice.dao.PaymentDao;
import com.beaconfire.coreservice.domain.Payment;
import com.beaconfire.coreservice.exception.NotFoundException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentService {
    @Value("${stripe.api.key}")
    private String stripeSecretKey;
    @Value("${stripe.urls.success}")
    private String successUrl;
    @Value("${stripe.urls.fail}")
    private String failUrl;
    private final PaymentDao paymentDao;

    public PaymentService(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    public String createPayment(Long orderId, BigDecimal total){
        String type = "card";
        Payment payment = Payment.builder()
                .orderId(orderId)
                .total(total)
                .type(type)
                .build();
        Stripe.apiKey = stripeSecretKey;
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failUrl)
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setName("TEST")
                            .setAmount(payment.getTotal().longValue() * 100)
                            .setQuantity(1L)
                            .setCurrency("usd")
                            .build())
                    .build();
            Session session = Session.create(params);
            payment.setPaymentIntentId(session.getPaymentIntent());
            paymentDao.addPayment(payment);
            return session.getUrl();
        }catch (StripeException e){
            throw new RuntimeException("STRIPE: " + e.getMessage());
        }
    }

    public Payment getPaymentByPaymentIntentId (String paymentIntentId) {
        return paymentDao.findByPaymentIntentId(paymentIntentId);
    }

    @Transactional
    public void refund(Long paymentId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        Payment payment = paymentDao.findById(paymentId);
        if (payment == null) {
            throw new NotFoundException("Payment Not Found.");
        }
        String paymentIntentId = payment.getPaymentIntentId();

        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .build();

        // Create a refund
        try{
            Refund.create(params);
            payment.setStatus("REFUNDED");
            paymentDao.save(payment);
        }catch (StripeException e){
            throw new RuntimeException("STRIPE: " + e.getMessage());
        }

    }

    /** webhook is responsible to update */
    @Transactional
    public void updatePaymentStatus(Long orderId, boolean paymentSuccess){
        paymentDao.updateStatusByOrderId(orderId, paymentSuccess? "SUCCESS":"DECLINED");
}

}
