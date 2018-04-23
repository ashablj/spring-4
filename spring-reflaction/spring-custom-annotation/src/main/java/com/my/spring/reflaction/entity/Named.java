package com.my.spring.reflaction.entity;

public class Named {

    private final String name;

    public Named(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
