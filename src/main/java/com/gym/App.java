package com.gym;

import com.gym.controller.MemberController;
import com.gym.controller.MembershipPlanController;
import io.javalin.Javalin;
import com.gym.route.*;
import io.javalin.plugin.bundled.CorsPluginConfig;

import static io.javalin.apibuilder.ApiBuilder.*;


public class App {
    private final  Javalin app;



    public  App(MemberController memberController , MembershipPlanController membershipPlanController){
        //config cors
        this.app = Javalin.create(config->{
            config.bundledPlugins.enableCors(cors->{
                cors.addRule(CorsPluginConfig.CorsRule::anyHost);
            }) ;
            // set up route
            config.router.apiBuilder(()->{
                get("/", ctx -> ctx.result("Welcome to the Gym Management System API!"));
                path("api/members", new MemberRoute(memberController));
                path("api/plans",new MembershipPlanRoute(membershipPlanController));
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
