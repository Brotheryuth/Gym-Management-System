package com.gym.controller;

import com.gym.enums.PaymentMethod;
import com.gym.model.Payment;
import com.gym.service.PaymentService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;


public class PaymentController  {
    private final PaymentService paymentService;

    public  PaymentController(PaymentService paymentService){
        this.paymentService=paymentService;
    }

    public  void getAll(Context ctx){
        try {

            List<PaymentTemp> temp = paymentService.findAll().stream().map(PaymentTemp::new).toList();
            ctx.status(HttpStatus.OK).json(temp);
        }catch (Exception e){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
    }}

    /**
     * get payment by id
     * @param ctx (req,res)
     */
    public void getByID(Context ctx){
        try{
        String ID  = ctx.pathParam("id");
        if(ID.isBlank()) throw new IllegalArgumentException("ID is null");
        Payment payment = paymentService.findById(ID);
        if(payment ==null){
            throw  new IllegalStateException("Payment not found");
        }
        ctx.status(HttpStatus.OK).json(new PaymentTemp(payment));
        }catch (IllegalArgumentException e){
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        }catch (IllegalStateException e ){
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }catch (Exception e ){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }
    // DTO for capturing the payment processor confirmation (e.g. scanning QR)
    public static class ProcessPaymentRequest {
        public String paymentMethod;
    }
    public void processPayment(Context ctx) {
        String id = ctx.pathParam("id");
        try {
            ProcessPaymentRequest req = ctx.bodyAsClass(ProcessPaymentRequest.class);
            PaymentMethod method = PaymentMethod.BYCASH;
            if (req.paymentMethod != null && !req.paymentMethod.isBlank()) {
                String upper = req.paymentMethod.trim().toUpperCase();
                if (upper.equals("CASH")) upper = "BYCASH";
                if (upper.equals("CREDIT_CARD") || upper.equals("CARD")) upper = "CREDITCARD";
                try {
                    method = PaymentMethod.valueOf(upper);
                } catch (IllegalArgumentException e) {
                    method = PaymentMethod.BYCASH;
                }
            }
            boolean success = paymentService.processPayment(id, method);
            if (!success) throw new IllegalStateException("Failed to process payment.");
            ctx.status(HttpStatus.OK).result("Payment processed successfully. Membership activated.");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }



    //DTO
    public static class PaymentTemp{
        private final String id;
        private final String membershipID;
        private final double baseAmount;
        private final double finalAmount;
        private final int discount;
        private final String method;
        private final String status;
        private final String createAt;
        private final String paymentDate;
        public PaymentTemp(Payment p) {
            this.id = p.getId();
            this.membershipID = p.getMembership() != null ? p.getMembership().getId() : null;
            this.baseAmount = p.getBaseAmount();
            this.finalAmount = p.getFinalAmount();
            this.discount = p.getDiscount();
            this.method = p.getMethod() != null ? p.getMethod().name() : null;
            this.status = p.getStatus() != null ? p.getStatus().name() : null;
            this.createAt = p.getCreateAt() != null ? p.getCreateAt().toString() : null;
            this.paymentDate = p.getPaymentDate() != null ? p.getPaymentDate().toString() : null;
        }
        public String getId() { return id; }
        public String getMembershipID() { return membershipID; }
        public double getBaseAmount() { return baseAmount; }
        public double getFinalAmount() { return finalAmount; }
        public int getDiscount() { return discount; }
        public String getMethod() { return method; }
        public String getStatus() { return status; }
        public String getCreateAt() { return createAt; }
        public String getPaymentDate() { return paymentDate; }
    }

}


