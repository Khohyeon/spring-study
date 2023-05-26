package com.example.schedulingtasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
// 스케줄 관리를 하기 위한 어노테이션 백그라운드 스레드 풀을 생성하고 관리
public class SchedulingtasksApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulingtasksApplication.class, args);
	}

}
