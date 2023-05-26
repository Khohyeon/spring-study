package com.example.restservice.controller;

import com.example.restservice.Greeting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";

    private final AtomicLong counter = new AtomicLong();

    // @RequestParam 으로 name 값을 받아 오는데 default 값으로 World 를 설정
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    // AtomicLong 은 long 값을 위한 thread-safe 한 클래스 (멀티스레드 환경에서 주로 사용)
    // AtomicLong 객체의 incrementAndGet() 메서드 사용해서 값이 들어 올 때 마다 counter를 1씩 올린다.
    // String.format() 메서드를 사용해서 name 값을 포함한 문자열을 만들고 Greeting 객체를 생성해서 반환한다.
}
