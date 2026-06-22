package com.gym.service;

import com.gym.model.Membership;
import com.gym.model.MembershipPlan;
import com.gym.repository.MembershipPlanRepository;
import com.gym.repository.MembershipRepository;

import java.util.List;

public class MembershipPlanService {
    private final MembershipPlanRepository planRepository;
    private final MembershipRepository membershipRepository;

    public MembershipPlanService(MembershipPlanRepository planRepository, MembershipRepository membershipRepository) {
        this.planRepository = planRepository;
        this.membershipRepository = membershipRepository;
    }

    /**
     * Creates and registers a new membership plan.
     * Enforces unique duration.
     *
     * @param price the price of the plan
     * @param duration the duration in months
     * @return the created MembershipPlan
     */
    public MembershipPlan createPlan(double price, int duration) {
        if (getPlanByDuration(duration) != null) {
            throw new IllegalArgumentException("A plan with a duration of " + duration + " month(s) already exists.");
        }

        MembershipPlan plan = new MembershipPlan(price, duration);
        boolean success = planRepository.insert(plan);
        return success ? plan : null;
    }


    /**
     *
     * @param membershipPlan accept object as argument
     * @return null if not found
     */
    public MembershipPlan createPlan(MembershipPlan membershipPlan){
        if(membershipPlan.getPlanPrice() <0.0) throw new IllegalArgumentException("Price cannot be negative");
        if(getPlanByDuration(membershipPlan.getDuration()) !=null){
            throw new IllegalArgumentException("A plan with a duration of "+membershipPlan.getDuration()+" month(s) already exist ");
        }
        MembershipPlan plan = new MembershipPlan(membershipPlan.getPlanPrice(),membershipPlan.getDuration());
        boolean success = planRepository.insert(plan);
        return success? plan : null;
    }

    /**
     * Finds a plan by duration.
     *
     * @param duration the duration to search for
     * @return the matching MembershipPlan or null
     */
    public MembershipPlan getPlanByDuration(int duration) {
        for (MembershipPlan plan : planRepository.findAll()) {
            if (plan.getDuration() == duration) {
                return plan;
            }
        }
        return null;
    }



    public List<MembershipPlan> findAll() {
        return planRepository.findAll();
    }

    public MembershipPlan findById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return planRepository.findById(id.trim());
    }

    /**
     * Updates an existing membership plan.
     * Enforces negative check and unique duration on updates.
     * Prevents duration change if in use.
     *
     * @param id the plan ID to update
     * @param newPrice the new price
     * @param newDuration the new duration in months
     * @return the updated MembershipPlan or null
     */
    public MembershipPlan updatePlan(String id, double newPrice, int newDuration) {
        MembershipPlan plan = findById(id);
        if (plan == null) {
            throw new IllegalArgumentException("Plan with ID " + id + " does not exist.");
        }

        if (newPrice < 0.0) {
            throw new IllegalArgumentException("Plan price cannot be negative.");
        }
        if (newDuration <= 0) {
            throw new IllegalArgumentException("Plan duration must be at least 1 month.");
        }

        // If duration is changing, check business logic constraints
        if (plan.getDuration() != newDuration) {
            MembershipPlan existingWithDuration = getPlanByDuration(newDuration);
            if (existingWithDuration != null && !existingWithDuration.getPlanID().equals(id)) {
                throw new IllegalArgumentException("Another plan with duration " + newDuration + " month(s) already exists.");
            }

            for (Membership membership : membershipRepository.findAll()) {
                if (membership.getPlan() != null && membership.getPlan().getPlanID().equals(id)) {
                    throw new IllegalStateException("Cannot change duration of plan because it is currently in use by active memberships.");
                }
            }
        }

        plan.setPlanPrice(newPrice);
        plan.setDuration(newDuration);

        boolean success = planRepository.update(plan);
        return success ? plan : null;
    }

    /**
     * Deletes a plan by ID. Enforces that the plan cannot be in use.
     *
     * @param id the ID of the plan to delete
     * @return true if deleted successfully
     */
    public boolean deletePlan(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Plan ID cannot be null or empty.");
        }

        for (Membership membership : membershipRepository.findAll()) {
            if (membership.getPlan() != null && membership.getPlan().getPlanID().equals(id)) {
                throw new IllegalStateException("Cannot delete plan: it is currently active for some memberships.");
            }
        }

        return planRepository.delete(id);
    }

    /**
     * Gets count of active subscribers to a plan.
     *
     * @param planId the plan ID
     * @return the number of subscribers
     */
    public int getSubscriberCount(String planId) {
        if (planId == null || planId.isBlank()) {
            return 0;
        }
        int count = 0;
        for (Membership membership : membershipRepository.findAll()) {
            if (membership.getPlan() != null && membership.getPlan().getPlanID().equals(planId)) {
                count++;
            }
        }
        return count;
    }
}
