package com.gym.service;

import com.gym.enums.MemberStatus;
import com.gym.enums.MembershipStatus;
import com.gym.enums.PaymentMethod;
import com.gym.model.Member;
import com.gym.model.Membership;
import com.gym.model.MembershipPlan;
import com.gym.model.Payment;
import com.gym.repository.MemberRepository;
import com.gym.repository.MembershipPlanRepository;
import com.gym.repository.MembershipRepository;
import com.gym.repository.PaymentRepository;

import java.sql.Date;
import java.util.List;

public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final MembershipPlanRepository membershipPlanRepository;

    public MembershipService(MembershipRepository membershipRepository, 
                             MemberRepository memberRepository, 
                             PaymentRepository paymentRepository,
                             MembershipPlanRepository membershipPlanRepository) {
        this.membershipRepository = membershipRepository;
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
        this.membershipPlanRepository = membershipPlanRepository;
    }

    /**
     * Subscribes a member to a plan.
     * Enforces the "no double subscriptions" rule and automatically creates a pending Payment.
     *
     * @param member the member subscribing
     * @param plan the subscription plan
     * @param startDate the start date of the subscription
     * @param discount the discount percentage (0 to 100)
     * @param method the payment method
     * @return the created Membership
     */
    public Membership subscribeMember(Member member, MembershipPlan plan, Date startDate, int discount, PaymentMethod method) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null.");
        }
        if (plan == null) {
            throw new IllegalArgumentException("Plan cannot be null.");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null.");
        }

        // 1. Check if member already has an active or pending membership
        Membership existing = membershipRepository.findMemberByID(member.getId());
        if (existing != null && (existing.getStatus() == MembershipStatus.ACTIVE || existing.getStatus() == MembershipStatus.PENDING)) {
            throw new IllegalStateException("Member already has an active or pending membership subscription.");
        }

        // 2. Create the Membership
        Membership membership = new Membership(member, plan, startDate);
        boolean membershipInserted = membershipRepository.insert(membership);
        if (!membershipInserted) {
            return null;
        }

        // 3. Create the corresponding Payment record automatically
        Payment payment = new Payment(membership, discount, method);
        paymentRepository.insert(payment);
        membership.setPayment(payment);

        return membership;
    }

    /**
     * Cancels a membership and deactivates the member status.
     *
     * @param membershipId the membership ID to cancel
     * @return true if successfully cancelled
     */
    public boolean cancelMembership(String membershipId) {
        Membership membership = membershipRepository.findById(membershipId);
        if (membership == null) {
            throw new IllegalArgumentException("Membership record not found.");
        }

        if (membership.getStatus() == MembershipStatus.CANCELLED || membership.getStatus() == MembershipStatus.EXPIRED) {
            throw new IllegalStateException("Membership is already inactive.");
        }

        // 1. Update Membership status
        membership.setStatus(MembershipStatus.CANCELLED);
        boolean updated = membershipRepository.update(membership);
        if (!updated) {
            return false;
        }

        // 2. Deactivate the member
        Member member = membership.getMember();
        if (member != null) {
            member.setMemberStatus(MemberStatus.INACTIVE);
            memberRepository.update(member);
        }

        return true;
    }

    /**
     * Checks all memberships and expires those whose end dates are in the past.
     * Reverts associated members to INACTIVE.
     * 
     * @return the number of memberships expired during this run
     */
    public int checkAndExpireMemberships() {
        int expiredCount = 0;
        Date today = new Date(System.currentTimeMillis());

        for (Membership membership : membershipRepository.findAll()) {
            if (membership.getStatus() == MembershipStatus.ACTIVE && membership.getEndDate() != null) {
                if (membership.getEndDate().before(today)) {
                    // Expire the membership
                    membership.setStatus(MembershipStatus.EXPIRED);
                    membershipRepository.update(membership);

                    // Deactivate member
                    Member member = membership.getMember();
                    if (member != null) {
                        member.setMemberStatus(MemberStatus.INACTIVE);
                        memberRepository.update(member);
                    }
                    expiredCount++;
                }
            }
        }
        return expiredCount;
    }

    public Membership findById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return membershipRepository.findById(id.trim());
    }

    public List<Membership> findAll() {
        return membershipRepository.findAll();
    }
}
