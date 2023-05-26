package com.example.schedulingtasks;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    @Scheduled(fixedRate = 5000)  // 1분(5000밀리초)마다 실행 -> 5초마다 실행
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));  // 터미널 로고에 띄우는 멘트
    }

}
