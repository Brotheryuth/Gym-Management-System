package com.gym.route;

import static io.javalin.apibuilder.ApiBuilder.*;

import com.gym.controller.MembershipController;
import io.javalin.apibuilder.EndpointGroup;

public class MembershipRoute implements EndpointGroup {

    private final MembershipController membershipController;

    public MembershipRoute(MembershipController membershipController) {
        this.membershipController = membershipController;
    }

    @Override
    public void addEndpoints() {
        get(membershipController::findAll);
        path("{id}", () -> {
            get(membershipController::findById);
        });
        post(membershipController::subscribeMember);
    }
}
