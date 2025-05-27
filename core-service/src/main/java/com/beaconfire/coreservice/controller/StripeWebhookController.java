package com.beaconfire.coreservice.controller;

import com.beaconfire.coreservice.domain.Payment;
import com.beaconfire.coreservice.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripeWebhookController {
    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);
    private final PaymentService paymentService;

    public StripeWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    private static final String endpointSecret = "";

    @PostMapping("/webhook")
    public ResponseEntity<String> handle(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        log.info("Webhook received.");
        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, endpointSecret
            );

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
        }
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
        }
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                assert paymentIntent != null;
                Payment payment = paymentService.getPaymentByPaymentIntentId(paymentIntent.getId());
                paymentService.updatePaymentStatus(payment.getOrderId(), true);
                return ResponseEntity.ok("Payment succeeded: " + paymentIntent.getId());
            case "payment_method.attached":
                // ...
                return ResponseEntity.ok("Payment method attached");
            // ... handle other event types
            default:
                // Unexpected event type
                return ResponseEntity.badRequest().build();
        }
    }
}
