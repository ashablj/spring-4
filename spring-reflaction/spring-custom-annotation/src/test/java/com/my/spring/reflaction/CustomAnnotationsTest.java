package com.my.spring.reflaction;

import com.my.spring.reflaction.AnnotatedBeanLocator;
import com.my.spring.reflaction.annotation.Foos;
import com.my.spring.reflaction.entity.Named;
import org.junit.Test;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardMethodMetadata;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CustomAnnotationsTest {

    @Test
    public void testFindByAnnotation() throws Exception {
        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext(CustomAnnotationsSpringCfg.class);

        Method m = CustomAnnotationsSpringCfg.class.getMethod("a");
        assertNotNull(m);
        assertNotNull(m.getAnnotation(Foos.class));

        ConfigurableListableBeanFactory factory = appContext.getBeanFactory();
        BeanDefinition bd = factory.getBeanDefinition("a");
        System.out.println(Arrays.toString(bd.attributeNames()));

        String type = Foos.class.getName();
        if (bd instanceof AnnotatedBeanDefinition) {
            AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) bd;

            AnnotationMetadata metadata = abd.getMetadata();
            System.out.println(metadata.getAnnotationTypes());
            Map<String, Object> attributes = metadata.getAnnotationAttributes(type);
            System.out.println(attributes);
        }

        if (bd instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition abd = (AbstractBeanDefinition) bd;

            System.out.println(abd.getQualifiers());
        }

        Object source = ((BeanMetadataElement) bd).getSource();
        if (source instanceof StandardMethodMetadata) {
            StandardMethodMetadata metadata = (StandardMethodMetadata) source;

            System.out.println(metadata.getAnnotationAttributes(type));
        }

        // TODO this should work
//        Map<String, Object> beans = appContext.getBeansWithAnnotation( Foo.class );
//        assertEquals( "[a]", beans.keySet().toString() );

        // Workaround
        AnnotatedBeanLocator locator = new AnnotatedBeanLocator(appContext);
        assertEquals("[a]", locator.getBeansWithAnnotation(Foos.class).toString());
    }

    @Lazy
    @Configuration
    public static class CustomAnnotationsSpringCfg {
        @Foos("x")
        @Bean
        public Named a() {
            return new Named("a");
        }

        @Bean
        public Named b() {
            return new Named("b");
        }
    }
}