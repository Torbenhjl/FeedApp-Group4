package com.oblig1.oblig1.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue voteQueue() {
        //true because it wil be stored on disk, if false then it is stored in memory.
        return new Queue("voteQueue", true);
    }
}
