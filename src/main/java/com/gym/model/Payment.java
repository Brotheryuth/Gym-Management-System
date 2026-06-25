package com.gym.model;

import com.gym.enums.PaymentMethod;
import com.gym.enums.PaymentStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Payment {
    private final String id;
    @JsonIgnoreProperties("payment")
    private Membership membership;
    private double baseAmount;
    private int discount; // discount as integer percentage (e.g., 20 for 20%)
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime createAt;
    private LocalDateTime paymentDate;

    private static final DateTimeFormatter cleanDate = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");

    /**
     * Helper to format LocalDateTime to a clean string.
     *
     * @param dateTime The LocalDateTime to format
     * @return Formatted string, or "N/A" if null
     */
    public String cleanDateFormat(LocalDateTime dateTime) {
        if (dateTime != null) {
            return dateTime.format(cleanDate);
        }
        return "N/A";
    }

    public Payment() {
        this.id = java.util.UUID.randomUUID().toString();
    }

    /**
     * Constructor for creating a new payment (ID and timestamps auto-generated).
     *
     * @param membership The membership this payment belongs to
     * @param discount Discount percentage (0-100)
     * @param method Payment method used
     */
    public Payment(Membership membership, int discount, PaymentMethod method) {
        this.id = UUID.randomUUID().toString();
        setMembership(membership);
        setDiscount(discount);
        setMethod(method);
        this.baseAmount = (membership != null && membership.getPlan() != null) ? membership.getPlan().getPlanPrice() : 0.0;
        this.status = PaymentStatus.PENDING;
        this.createAt = LocalDateTime.now();
        this.paymentDate = null;
    }

    /**
     * Constructor for loading an existing payment from the database.
     *
     * @param id The existing payment ID
     * @param membership The membership
     * @param baseAmount The base amount
     * @param discount Discount percentage
     * @param method Payment method
     * @param status Payment status
     * @param createAt Record creation timestamp
     * @param paymentDate Payment execution timestamp
     */
    public Payment(String id, Membership membership, double baseAmount, int discount, PaymentMethod method, PaymentStatus status, LocalDateTime createAt, LocalDateTime paymentDate) {
        this.id = id;
        setMembership(membership);
        setDiscount(discount);
        setMethod(method);
        setBaseAmount(baseAmount);
        setStatus(status);
        this.createAt = createAt;
        this.paymentDate = paymentDate;
    }

    /**
     * Calculates the final amount after applying discount.
     *
     * @return Final cost of the transaction
     */
    public double getFinalAmount() {
        double discountBase = baseAmount * (1 - (double) discount / 100.0);

        if (method == PaymentMethod.CREDITCARD) {
            return discountBase * 1.05; // 5% card fee
        }
        return discountBase;
    }

    public String getId() {
        return id;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        if (membership == null) {
            throw new IllegalArgumentException("Membership cannot be null.");
        }
        this.membership = membership;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(double baseAmount) {
        if (baseAmount < 0.0) {
            throw new IllegalArgumentException("Base amount cannot be negative.");
        }
        this.baseAmount = baseAmount;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100.");
        }
        this.discount = discount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        if (method == null) {
            throw new IllegalArgumentException("Payment method cannot be null.");
        }
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Payment status cannot be null.");
        }
        this.status = status;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString() {
        return String.format(
                """
                ----------------------------------
                        PAYMENT RECEIPT
                ----------------------------------
                Payment ID      : %s
                Membership ID   : %s
                Member Name     : %s
                Base Amount     : $%.2f
                Discount        : %d%%
                Method          : %s
                Final Amount    : $%.2f
                Status          : %s
                Created At      : %s
                Paid Date       : %s
                ----------------------------------
                """,
                this.id,
                this.membership != null ? this.membership.getId() : "N/A",
                (this.membership != null && this.membership.getMember() != null) ? this.membership.getMember().getFullName() : "N/A",
                this.baseAmount,
                this.discount,
                this.method,
                getFinalAmount(),
                this.status,
                cleanDateFormat(this.createAt),
                cleanDateFormat(this.paymentDate)
        );
    }
}
