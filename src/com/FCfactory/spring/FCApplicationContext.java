package com.FCfactory.spring;

public class FCApplicationContext {
    private Class config;

    public FCApplicationContext(Class config) {
        this.config = config;
    }

    public Object getBean(String name){
        return null;
    }
}
