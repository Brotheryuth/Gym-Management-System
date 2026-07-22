package com.gym;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.controller.*;
import com.gym.route.*;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import io.javalin.plugin.bundled.CorsPluginConfig;

import static io.javalin.apibuilder.ApiBuilder.*;


public class App {
    private final  Javalin app;



    public  App(MemberController memberController, 
               MembershipPlanController membershipPlanController,
               MembershipController membershipController,
               PaymentController paymentController,
               StaffController staffController,
               AuthController authController){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        //config cors
        this.app = Javalin.create(config->{
            config.jsonMapper(new JavalinJackson(objectMapper, false));
            config.bundledPlugins.enableCors(cors->{
                cors.addRule(CorsPluginConfig.CorsRule::anyHost);
            }) ;
            // set up route
            config.router.apiBuilder(()->{
                get("/", ctx -> ctx.result("Welcome to the Gym Management System API!"));
                path("api/members", new MemberRoute(memberController));
                path("api/plans", new MembershipPlanRoute(membershipPlanController));
                path("api/memberships", new MembershipRoute(membershipController));
                path("api/payments", new PaymentRoute(paymentController));
                path("api/staff", new StaffRoute(staffController));
                path("api/auth", new AuthRoute(authController));
            });
        });
    }

    public  void start ( int port ){
        this.app.start(port);
        System.out.println("Server running on port : "+port);
    }
    public  void stop(){
        this.app.stop();
    }



}

