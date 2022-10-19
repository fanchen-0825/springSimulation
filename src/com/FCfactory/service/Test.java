package com.FCfactory.service;

import com.FCfactory.spring.FCApplicationContext;

public class Test {


    public static void main(String[] args) {
        FCApplicationContext context=new FCApplicationContext(AppConfig.class);

        UserService userService = (UserService) context.getBean("UserService");

    }
}
