package com.my.spring.reflaction.service;

import org.springframework.stereotype.Component;

@Component
public class FooD implements Foo {
    @Override
    public void bar() {
        System.out.println("I am not a number, I am a free man!");
    }
}
