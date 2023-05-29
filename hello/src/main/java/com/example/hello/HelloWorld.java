package com.example.hello;

import java.time.LocalTime;

public class HelloWorld {

    public static void main(String[] args) {
//        LocalTime currentTime = new LocalTime(12,15,15,10);
//        System.out.println("The current local time is: " + currentTime);

        Greeter greeter = new Greeter();
        System.out.println(greeter.sayHello());
    }
}
