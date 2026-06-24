package com.gym.route;

import com.gym.controller.PaymentController;
import io.javalin.apibuilder.EndpointGroup;
import  static io.javalin.apibuilder.ApiBuilder.*;
public class PaymentRoute implements EndpointGroup {
    private final PaymentController paymentController;

    public PaymentRoute(PaymentController paymentController){
        this.paymentController=paymentController;
    }

    @Override
    public void addEndpoints() {
        get(paymentController::getAll);
        path("{id}",()->{
           get(paymentController::getByID);
           post(paymentController::processPayment);

        });
    }
}
