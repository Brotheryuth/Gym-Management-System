package com.gym;

import com.gym.controller.MemberController;
import io.javalin.Javalin;
import com.gym.route.*;
import static io.javalin.apibuilder.ApiBuilder.*;


public class App {
    private Javalin app;


    public  App(MemberController memberController){
        //config cors
        this.app = Javalin.create(config->{
            config.bundledPlugins.enableCors(cors->{
                cors.addRule(rule->{
                    rule.anyHost();
                });
            }) ;
            // set up route
            config.router.apiBuilder(()->{
                path("api/members", new MemberRoute(memberController));
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
