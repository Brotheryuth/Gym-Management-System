package com.gym.route;

import com.gym.controller.MembershipPlanController;

import  io.javalin.apibuilder.EndpointGroup;
import  static  io.javalin.apibuilder.ApiBuilder.*;

public class MembershipPlanRoute implements EndpointGroup {
    private MembershipPlanController membershipPlanController;

    public MembershipPlanRoute(MembershipPlanController membershipPlanController){
        this.membershipPlanController= membershipPlanController;
    }

    @Override
    public void addEndpoints() {
        get(membershipPlanController::getAll);
        post(membershipPlanController::createPlan);
        path("{id}",()->{
            get(membershipPlanController::getByID);
            put(membershipPlanController::updatePlan);
            delete(membershipPlanController::deletePlan);
            get("subscribers/count", membershipPlanController::getSubscriberCount);
        });
        get("duration/{duration}",membershipPlanController::getPlanByDuration);
    }
}
