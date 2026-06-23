package com.gym.route;

import com.gym.controller.AuthController;
import   io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;
public class AuthRoute implements   EndpointGroup {
    private  final AuthController authController;
    public  AuthRoute(AuthController authController){
        this.authController=authController;
    }

    @Override
    public void addEndpoints() {
        post(authController::login);
    }
}
