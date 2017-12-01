package com.redhat.labs.demo.spring;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserResource {

    @RequestMapping("/rest/v1/hello")
    public String hello(String name) {
        return String.format("Hello %s!", name);
    }
}