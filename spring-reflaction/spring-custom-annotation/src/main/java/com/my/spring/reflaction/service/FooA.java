package com.my.spring.reflaction.service;

import com.my.spring.reflaction.annotation.Fooish;
import org.springframework.stereotype.Component;

@Component
@Fooish(cool = false, tags = {"sixfoo"})
public class FooA implements Foo {

    @Override
    public void bar() {
        System.out.println("I am number " + 1);
    }
}
