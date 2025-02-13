package faang.school.projectservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class ApplicationContextTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.2")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @Container
    static final MinIOContainer minio = new MinIOContainer("minio/mc:latest")
            .withUserName("user")
            .withPassword("password");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // Настройка PostgreSQL
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Настройка MinIO. Метод getS3URL() возвращает URL, по которому можно подключиться к сервису S3.
        registry.add("services.minio.endpoint", minio::getS3URL);
        registry.add("services.minio.accessKey", minio::getUserName);
        registry.add("services.minio.secretKey", minio::getPassword);
        registry.add("services.minio.bucketName", () -> "project-files");
        registry.add("services.minio.defaultMaxSize", () -> "2147483648");
    }

    @Test
    void contextLoads() {

    }
}
