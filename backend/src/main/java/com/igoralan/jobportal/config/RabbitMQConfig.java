package com.igoralan.jobportal.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "job-exchange";
    public static final String SYNC_QUEUE_NAME = "job.sync.queue";
    public static final String DELETE_QUEUE_NAME = "job.delete.queue";

    @Bean
    public TopicExchange jobExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue jobSyncQueue() {
        return new Queue(SYNC_QUEUE_NAME);
    }

    @Bean
    public Queue jobDeleteQueue() {
        return new Queue(DELETE_QUEUE_NAME);
    }

    @Bean
    public Binding jobSyncBinding(TopicExchange exchange, Queue jobSyncQueue) {
        return BindingBuilder.bind(jobSyncQueue).to(exchange).with("job.created.#");
    }

    @Bean
    public Binding jobUpdateBinding(TopicExchange exchange, Queue jobSyncQueue) {
        return BindingBuilder.bind(jobSyncQueue).to(exchange).with("job.updated.#");
    }

    @Bean
    public Binding jobDeleteBinding(TopicExchange exchange, Queue jobDeleteQueue) {
        return BindingBuilder.bind(jobDeleteQueue).to(exchange).with("job.deleted.#");
    }
}
