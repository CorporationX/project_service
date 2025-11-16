package faang.school.projectservice.integration.jira.queue.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.aMqp.core.Binding;
import org.springframework.aMqp.core.BindingBuilder;
import org.springframework.aMqp.core.ExchangeBuilder;
import org.springframework.aMqp.core.Queue;
import org.springframework.aMqp.core.QueueBuilder;
import org.springframework.aMqp.core.TopicExchange;
import org.springframework.aMqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.aMqp.rabbit.connection.ConnectionFactory;
import org.springframework.aMqp.rabbit.core.RabbitAdmin;
import org.springframework.aMqp.rabbit.core.RabbitTemplate;
import org.springframework.aMqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.aMqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitMqConfig {
    
    public static final String JIRA_EXCHANGE = "jira.exchange";
    public static final String JIRA_DLX = "jira.dlx";
    
    public static final String TASK_CREATE_QUEUE = "jira.task.create.queue";
    public static final String TASK_UPDATE_QUEUE = "jira.task.update.queue";
    public static final String TASK_DELETE_QUEUE = "jira.task.delete.queue";
    public static final String TASK_SYNC_QUEUE = "jira.task.sync.queue";
    public static final String TASK_BULK_QUEUE = "jira.task.bulk.queue";
    
    public static final String TASK_CREATE_DLQ = "jira.task.create.dlq";
    public static final String TASK_UPDATE_DLQ = "jira.task.update.dlq";
    public static final String TASK_DELETE_DLQ = "jira.task.delete.dlq";
    public static final String TASK_SYNC_DLQ = "jira.task.sync.dlq";
    
    public static final String TASK_CREATE_KEY = "jira.task.create";
    public static final String TASK_UPDATE_KEY = "jira.task.update";
    public static final String TASK_DELETE_KEY = "jira.task.delete";
    public static final String TASK_SYNC_KEY = "jira.task.sync";
    public static final String TASK_BULK_KEY = "jira.task.bulk";
    
    // ==========================================
    // Message Converter
    // ==========================================
    
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
    
    // ==========================================
    // RabbitTemplate
    // ==========================================
    
    @Bean
    public RabbitTemplate rabbitTemplate(
        ConnectionFactory connectionFactory,
        MessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2);
        backOffPolicy.setMaxInterval(10000);
        
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);
        template.setRetryTemplate(retryTemplate);
        
        return template;
    }
    
    // ==========================================
    // RabbitAdmin
    // ==========================================
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }
    
    // ==========================================
    // Listener Container Factory
    // ==========================================
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
        ConnectionFactory connectionFactory,
        MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(5);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        
        return factory;
    }
    
    // ==========================================
    // Exchanges
    // ==========================================
    
    @Bean
    public TopicExchange jiraExchange() {
        return ExchangeBuilder.topicExchange(JIRA_EXCHANGE)
            .durable(true)
            .build();
    }
    
    @Bean
    public TopicExchange jiraDlx() {
        return ExchangeBuilder.topicExchange(JIRA_DLX)
            .durable(true)
            .build();
    }
    
    // ==========================================
    // Queues with DLQ
    // ==========================================
    
    @Bean
    public Queue taskCreateQueue() {
        return QueueBuilder.durable(TASK_CREATE_QUEUE)
            .withArgument("x-dead-letter-exchange", JIRA_DLX)
            .withArgument("x-dead-letter-routing-key", "dlq.task.create")
            .withArgument("x-message-ttl", 3600000)
            .withArgument("x-max-retries", 3)
            .build();
    }
    
    @Bean
    public Queue taskUpdateQueue() {
        return QueueBuilder.durable(TASK_UPDATE_QUEUE)
            .withArgument("x-dead-letter-exchange", JIRA_DLX)
            .withArgument("x-dead-letter-routing-key", "dlq.task.update")
            .withArgument("x-message-ttl", 3600000)
            .withArgument("x-max-retries", 3)
            .build();
    }
    
    @Bean
    public Queue taskDeleteQueue() {
        return QueueBuilder.durable(TASK_DELETE_QUEUE)
            .withArgument("x-dead-letter-exchange", JIRA_DLX)
            .withArgument("x-dead-letter-routing-key", "dlq.task.delete")
            .withArgument("x-message-ttl", 3600000)
            .withArgument("x-max-retries", 3)
            .build();
    }
    
    @Bean
    public Queue taskSyncQueue() {
        return QueueBuilder.durable(TASK_SYNC_QUEUE)
            .withArgument("x-dead-letter-exchange", JIRA_DLX)
            .withArgument("x-dead-letter-routing-key", "dlq.task.sync")
            .withArgument("x-message-ttl", 7200000)
            .build();
    }
    
    @Bean
    public Queue taskBulkQueue() {
        return QueueBuilder.durable(TASK_BULK_QUEUE)
            .withArgument("x-dead-letter-exchange", JIRA_DLX)
            .withArgument("x-dead-letter-routing-key", "dlq.task.bulk")
            .withArgument("x-message-ttl", 7200000)
            .withArgument("x-max-priority", 10)
            .build();
    }
    
    // ==========================================
    // Dead Letter Queues
    // ==========================================
    
    @Bean
    public Queue taskCreateDlq() {
        return QueueBuilder.durable(TASK_CREATE_DLQ).build();
    }
    
    @Bean
    public Queue taskUpdateDlq() {
        return QueueBuilder.durable(TASK_UPDATE_DLQ).build();
    }
    
    @Bean
    public Queue taskDeleteDlq() {
        return QueueBuilder.durable(TASK_DELETE_DLQ).build();
    }
    
    @Bean
    public Queue taskSyncDlq() {
        return QueueBuilder.durable(TASK_SYNC_DLQ).build();
    }
    
    // ==========================================
    // Bindings
    // ==========================================
    
    @Bean
    public Binding taskCreateBinding(Queue taskCreateQueue, TopicExchange jiraExchange) {
        return BindingBuilder.bind(taskCreateQueue)
            .to(jiraExchange)
            .with(TASK_CREATE_KEY);
    }
    
    @Bean
    public Binding taskUpdateBinding(Queue taskUpdateQueue, TopicExchange jiraExchange) {
        return BindingBuilder.bind(taskUpdateQueue)
            .to(jiraExchange)
            .with(TASK_UPDATE_KEY);
    }
    
    @Bean
    public Binding taskDeleteBinding(Queue taskDeleteQueue, TopicExchange jiraExchange) {
        return BindingBuilder.bind(taskDeleteQueue)
            .to(jiraExchange)
            .with(TASK_DELETE_KEY);
    }
    
    @Bean
    public Binding taskSyncBinding(Queue taskSyncQueue, TopicExchange jiraExchange) {
        return BindingBuilder.bind(taskSyncQueue)
            .to(jiraExchange)
            .with(TASK_SYNC_KEY);
    }
    
    @Bean
    public Binding taskBulkBinding(Queue taskBulkQueue, TopicExchange jiraExchange) {
        return BindingBuilder.bind(taskBulkQueue)
            .to(jiraExchange)
            .with(TASK_BULK_KEY);
    }
    
    // ==========================================
    // DLQ Bindings
    // ==========================================
    
    @Bean
    public Binding dlqCreateBinding(Queue taskCreateDlq, TopicExchange jiraDlx) {
        return BindingBuilder.bind(taskCreateDlq)
            .to(jiraDlx)
            .with("dlq.task.create");
    }
    
    @Bean
    public Binding dlqUpdateBinding(Queue taskUpdateDlq, TopicExchange jiraDlx) {
        return BindingBuilder.bind(taskUpdateDlq)
            .to(jiraDlx)
            .with("dlq.task.update");
    }
    
    @Bean
    public Binding dlqDeleteBinding(Queue taskDeleteDlq, TopicExchange jiraDlx) {
        return BindingBuilder.bind(taskDeleteDlq)
            .to(jiraDlx)
            .with("dlq.task.delete");
    }
    
    @Bean
    public Binding dlqSyncBinding(Queue taskSyncDlq, TopicExchange jiraDlx) {
        return BindingBuilder.bind(taskSyncDlq)
            .to(jiraDlx)
            .with("dlq.task.sync");
    }
}

