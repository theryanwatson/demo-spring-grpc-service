package org.watson.demos.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.watson.demos.GreetingRequest;
import org.watson.demos.GreetingResponse;
import org.watson.demos.models.Greeting;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Supplier;

class PojoConversionTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
            .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true)
            .configure(JsonParser.Feature.IGNORE_UNDEFINED, true);

    @Test
    void testConversion() {

        GreetingRequest greetingRequest = GreetingRequest.newBuilder()
                .setContent("some content")
                .setLocale("en_US")
                .build();

        GreetingResponse greetingResponse = GreetingResponse.newBuilder()
                .setContent("some content")
                .setLocale("en_US")
                .build();

        Greeting greeting = Greeting.builder()
                .content("some content")
                .locale(Locale.US)
                .build();

        System.out.println("ex == " + greeting);

        Greeting greeting1 = copyProperties(greetingResponse, Greeting::builder).build();
        System.out.println("1s == " + greetingResponse.toString().replace("\n", ", ") + " -> " + greeting1);

        Greeting greeting2 = copyProperties(greetingRequest, Greeting::builder).build();
        System.out.println("2s == " + greetingRequest.toString().replace("\n", ", ") + " -> " + greeting2);

        Greeting2 greeting3 = copyProperties(greetingResponse, Greeting2::new);
        System.out.println("3s == " + greetingResponse.toString().replace("\n", ", ") + " -> " + greeting3);

        Greeting2 greeting4 = copyProperties(greetingRequest, Greeting2::new);
        System.out.println("4s == " + greetingRequest.toString().replace("\n", ", ") + " -> " + greeting4);

        Greeting2 greeting5 = copyProperties(greeting, Greeting2::new);
        System.out.println("5s == " + greeting + " -> " + greeting5);
        System.out.println("5j == " + greeting + " -> " + OBJECT_MAPPER.convertValue(greeting, Greeting2.class));

        Greeting2 alt = new Greeting2("some content", Locale.US);
        Greeting greeting6 = copyProperties(alt, Greeting::builder).build();
        System.out.println("6s == " + alt + " -> " + greeting6);
        System.out.println("6j == " + alt + " -> " + OBJECT_MAPPER.convertValue(alt, Greeting.class));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Greeting2 {
        private String content;
        private Locale locale;
    }

    static <T> T copyProperties(final Object source, final T target) {
        PropertyDescriptor[] sourceDescriptors = BeanUtils.getPropertyDescriptors(source.getClass());
        PropertyDescriptor[] targetDescriptors = BeanUtils.getPropertyDescriptors(target.getClass());

        if (targetDescriptors.length <= 1) {
            Method[] methods = target.getClass().getMethods();
            Method[] declaredMethods = target.getClass().getDeclaredMethods();
            System.out.println(Arrays.toString(methods));
            System.out.println(Arrays.toString(declaredMethods));
        }

        BeanUtils.copyProperties(source, target);
        return target;
    }

    static <T> T copyProperties(final Object source, final Supplier<T> targetSupplier) {
        return copyProperties(source, targetSupplier.get());
    }
}
