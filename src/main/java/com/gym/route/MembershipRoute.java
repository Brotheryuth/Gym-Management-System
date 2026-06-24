package com.gym.route;

import com.gym.controller.MembershipController;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;
public class MembershipRoute implements EndpointGroup {
    private final MembershipController membershipController;
    public MembershipRoute(MembershipController membershipController){
        this.membershipController=membershipController;
    }
    @Override
    public void addEndpoints() {
        get(membershipController::findAll);
        path("{id}",()->{
            get(membershipController::findByID);

        });
        post(membershipController::subscribeMember);
    }
}
