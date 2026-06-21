package com.gym.service;

import com.gym.enums.MemberStatus;
import com.gym.enums.MembershipStatus;
import com.gym.enums.PaymentMethod;
import com.gym.enums.PaymentStatus;
import com.gym.model.Member;
import com.gym.model.Membership;
import com.gym.model.Payment;
import com.gym.repository.MemberRepository;
import com.gym.repository.MembershipRepository;
import com.gym.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final MembershipRepository membershipRepository;
    private final MemberRepository memberRepository;

    public PaymentService(PaymentRepository paymentRepository, 
                          MembershipRepository membershipRepository, 
                          MemberRepository memberRepository) {
        this.paymentRepository = paymentRepository;
        this.membershipRepository = membershipRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * Creates a new pending payment for a membership.
     *
     * @param membership the membership to create payment for
     * @param discount the discount percentage (0 to 100)
     * @param method the payment method
     * @return the created Payment object
     */
    public Payment createPayment(Membership membership, int discount, PaymentMethod method) {
        if (membership == null) {
            throw new IllegalArgumentException("Cannot create payment for a null membership.");
        }
        Payment payment = new Payment(membership, discount, method);
        boolean success = paymentRepository.insert(payment);
        return success ? payment : null;
    }

    /**
     * Processes a payment and activates the associated membership and member.
     *
     * @param paymentId the payment ID to process
     * @param method the method of payment used
     * @return true if successful
     */
    public boolean processPayment(String paymentId, PaymentMethod method) {
        Payment payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment record not found.");
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Payment has already been processed.");
        }

        // 1. Update Payment status, method, and date
        payment.setStatus(PaymentStatus.PAID);
        payment.setMethod(method);
        payment.setPaymentDate(LocalDateTime.now());
        
        boolean paymentUpdated = paymentRepository.update(payment);
        if (!paymentUpdated) {
            return false;
        }

        // 2. Activate Membership
        Membership membership = payment.getMembership();
        if (membership != null) {
            membership.setStatus(MembershipStatus.ACTIVE);
            membershipRepository.update(membership);

            // 3. Activate Member
            Member member = membership.getMember();
            if (member != null) {
                member.setMemberStatus(MemberStatus.ACTIVE);
                memberRepository.update(member);
            }
        }

        return true;
    }

    public Payment findById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return paymentRepository.findById(id.trim());
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    /**
     * Deletes a payment record. 
     * Enforces auditing rule: PAID payments cannot be deleted.
     *
     * @param id the payment ID to delete
     * @return true if deleted successfully
     */
    public boolean deletePayment(String id) {
        Payment payment = paymentRepository.findById(id);
        if (payment == null) {
            return false;
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Cannot delete a completed payment record for accounting integrity.");
        }

        return paymentRepository.delete(id);
    }
}
