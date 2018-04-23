package com.my.spring.reflaction.service;

import com.my.spring.reflaction.annotation.Fooish;
import org.springframework.stereotype.Component;

@Component
@Fooish(cool = true, tags = {"sixfoo"})
public class FooB implements Foo {
    @Override
    public void bar() {
        System.out.println("I am two number " + 2);
    }
}
