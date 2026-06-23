package com.gym.route;

import com.gym.controller.StaffController;
import io.javalin.apibuilder.EndpointGroup;
import static  io.javalin.apibuilder.ApiBuilder.*;
public class StaffRoute implements EndpointGroup {
    private final StaffController staffController;
    public StaffRoute(StaffController staffController){
        this.staffController=staffController;
    }

    @Override
    public void addEndpoints() {
        get(staffController::getAll);
        post(staffController::registerStaff);
        path("{id}",()->{
            get(staffController::getByID);
            delete(staffController::deleteStaff);
        });
    }
}
