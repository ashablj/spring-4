package com.my.spring.reflaction;

import com.my.spring.reflaction.annotation.Fooish;
import com.my.spring.reflaction.config.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.Map;

public class App {

    private static final ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

    public static void main(String[] args) throws Exception {
        final Map<String, Object> myFoos = applicationContext.getBeansWithAnnotation(Fooish.class);

        for (final Object myFoo : myFoos.values()) {
            final Class<?> fooClass = myFoo.getClass();
            final Fooish annotation = fooClass.getAnnotation(Fooish.class);

            System.out.println(
                    "Found foo class: " + fooClass
                            + ", with tags: " + Arrays.toString(annotation.tags())
                            + ", cool: " + annotation.cool()
            );
        }
    }
}