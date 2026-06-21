package com.gym.model;

import java.util.UUID;

public class MembershipPlan {
    private final String planID;
    private double planPrice;
    private int duration;

    public MembershipPlan() {
        this.planID = java.util.UUID.randomUUID().toString();
    }

    /**
     * Constructor to create a new membership plan (ID is auto-generated).
     *
     * @param planPrice The price of the plan
     * @param duration The duration of the plan in months
     */
    public MembershipPlan(double planPrice, int duration) {
        this.planID = UUID.randomUUID().toString();
        setPlanPrice(planPrice);
        setDuration(duration);
    }

    /**
     * Constructor to load an existing membership plan from database.
     *
     * @param planID The existing plan ID
     * @param planPrice The price of the plan
     * @param duration The duration of the plan in months
     */
    public MembershipPlan(String planID, double planPrice, int duration) {
        this.planID = planID;
        setPlanPrice(planPrice);
        setDuration(duration);
    }

    public String getPlanID() {
        return planID;
    }

    public double getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(double planPrice) {
        if (planPrice < 0.0) {
            throw new IllegalArgumentException("Plan price cannot be negative.");
        }
        this.planPrice = planPrice;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Plan duration must be at least 1 month.");
        }
        this.duration = duration;
    }

    @Override
    public String toString() {
        return String.format(
                """
                ----------------------------------
                        MEMBERSHIP PLAN
                ----------------------------------
                ID              : %s
                Price           : $%.2f
                Duration        : %d month(s)
                ----------------------------------
                """,
                this.planID,
                this.planPrice,
                this.duration
        );
    }
}
