package com.example.pharmastock.notification;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Notifications run on their own small thread pool so that sending an email/SMS
 * never blocks the HTTP thread handling a reservation/payment request. This is
 * the concurrency piece: multiple notifications can be in flight at once,
 * independent of and in parallel with request handling.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("notify-");
        executor.initialize();
        return executor;
    }
}
