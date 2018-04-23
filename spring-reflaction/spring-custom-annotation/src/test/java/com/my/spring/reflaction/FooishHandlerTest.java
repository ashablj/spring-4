package com.my.spring.reflaction;

import com.my.spring.reflaction.annotation.Fooish;
import com.my.spring.reflaction.service.*;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Map;

/**
 */
public class FooishHandlerTest {

    @Test
    public void testFindByAnnotation() throws Exception {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
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

    @Configuration
    public static class Config {
        @Bean
        public Foo a() {
            return new FooA();
        }

        @Bean
        public Foo b() {
            return new FooB();
        }

        @Bean
        public Foo c() {
            return new FooC();
        }

        @Bean
        public Foo d() {
            return new FooD();
        }
    }
}
