package com.antikryptonite.outsourcing.telegram;

import org.springframework.context.annotation.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Конфигуратор бина TaskScheduler
 */
@Configuration
public class TaskScheduleConfiguration {

    /**
     * @return taskScheduler
     */
    @Bean
    public TaskScheduler taskScheduler(){
        return new ThreadPoolTaskScheduler();
    }

}
