package faang.school.projectservice.testConfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestBeansConfig {
    @Bean
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("test")
                .withUsername("test")
                .withPassword("test");
    }

    @Bean
    public GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:latest"))
                .withExposedPorts(6379);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
