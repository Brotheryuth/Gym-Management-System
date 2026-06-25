package com.gym.controller;

import com.gym.enums.PaymentMethod;
import com.gym.model.Member;
import com.gym.model.Membership;
import com.gym.model.MembershipPlan;
import com.gym.service.MemberService;
import com.gym.service.MembershipPlanService;
import com.gym.service.MembershipService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.sql.Date;
import java.time.LocalDate;

public class MembershipController {
    private final MembershipService membershipService;
    private final MemberService memberService;
    private final MembershipPlanService membershipPlanService;

    public MembershipController(MembershipService membershipService, 
                                MemberService memberService, 
                                MembershipPlanService membershipPlanService) {
        this.membershipService = membershipService;
        this.memberService = memberService;
        this.membershipPlanService = membershipPlanService;
    }

    /**
     * Find membership by its ID
     * @param ctx Context
     */
    public void findById(Context ctx) {
        String id = ctx.pathParam("id");
        try {
            Membership membership = membershipService.findById(id);
            if (membership == null) throw new IllegalStateException("Membership not found");
            ctx.status(HttpStatus.OK).json(membership);
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    public void findAll(Context ctx) {
        try {
            ctx.status(HttpStatus.OK).json(membershipService.findAll());
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    // DTO class for subscribing
    public static class SubscribeRequest {
        public String memberID;
        public String planID;
        public String startDate; // "YYYY-MM-DD"
        public int discount;
        public String paymentMethod; // e.g. "BYCASH", "KHQR", "CREDITCARD"
    }

    public void subscribeMember(Context ctx) {
        try {
            SubscribeRequest req = ctx.bodyAsClass(SubscribeRequest.class);
            if (req.memberID == null || req.memberID.isBlank()) {
                throw new IllegalArgumentException("Member ID is required.");
            }
            if (req.planID == null || req.planID.isBlank()) {
                throw new IllegalArgumentException("Plan ID is required.");
            }
            
            // Resolve member
            Member member = memberService.findById(req.memberID);
            if (member == null) {
                throw new IllegalArgumentException("Member not found with ID: " + req.memberID);
            }

            // Resolve plan
            MembershipPlan plan = membershipPlanService.findById(req.planID);
            if (plan == null) {
                throw new IllegalArgumentException("Membership plan not found with ID: " + req.planID);
            }

            Date parsedStartDate;
            if (req.startDate == null || req.startDate.isBlank()) {
                parsedStartDate = Date.valueOf(LocalDate.now());
            } else {
                parsedStartDate = Date.valueOf(req.startDate);
            }

            PaymentMethod method = PaymentMethod.BYCASH;
            if (req.paymentMethod != null && !req.paymentMethod.isBlank()) {
                method = PaymentMethod.valueOf(req.paymentMethod.toUpperCase());
            }

            Membership newMembership = membershipService.subscribeMember(
                    member,
                    plan,
                    parsedStartDate,
                    req.discount,
                    method
            );

            if (newMembership == null) {
                throw new IllegalStateException("Failed to create membership.");
            }

            ctx.status(HttpStatus.CREATED).json(newMembership);

        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Internal error: " + e.getMessage());
        }
    }
}
