package faang.school.projectservice.integration.jira.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Конфигурация WebSocket для real-time обновлений Jira интеграции
 * 
 * Endpoints:
 * - /ws/jira - STOMP endpoint для подключения
 * - /topic/jira/tasks - Топик для обновлений задач
 * - /topic/jira/sync - Топик для обновлений синхронизации
 * - /topic/jira/status - Топик для обновлений статусов
 */
@Configuration
@EnableWebSocketMessageBroker
public class JiraWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Включаем простой in-memory message broker для отправки сообщений клиентам
        config.enableSimpleBroker("/topic", "/queue");
        
        // Префикс для сообщений от клиента к серверу
        config.setApplicationDestinationPrefixes("/app");
        
        // Префикс для пользовательских сообщений
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Регистрируем STOMP endpoint
        registry.addEndpoint("/ws/jira")
            .setAllowedOriginPatterns("*")  // В продакшене указать конкретные домены
            .withSockJS();  // Fallback для браузеров без WebSocket поддержки
        
        // Альтернативный endpoint без SockJS
        registry.addEndpoint("/ws/jira")
            .setAllowedOriginPatterns("*");
    }
}

