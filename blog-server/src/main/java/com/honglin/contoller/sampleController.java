package com.honglin.contoller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class sampleController {

    @GetMapping("/test")
    public String hello(@AuthenticationPrincipal String username) {
        //System.out.println("User: "+username +" logging");
        return "User: " + username + " logging";
    }

    @GetMapping("/test2")
    public String hello2(Principal principal) {
        //System.out.println("User: "+username +" logging");
        String output = principal.getName();
        return "User: " + output;
    }
}
