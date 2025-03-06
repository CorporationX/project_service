package faang.school.projectservice.controller.integrationTests;

import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("it")
public class CampaignControllerIT {

    private static final String MINIO_IMAGE = "minio/minio:RELEASE.2023-09-04T19-57-37Z";
    private static final String ACCESS_KEY = "minioadmin";
    private static final String SECRET_KEY = "minioadmin";

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest");

    @Container
    public GenericContainer<?> minioContainer = new GenericContainer<>(MINIO_IMAGE)
            .withExposedPorts(9000)
            .withEnv("MINIO_ROOT_USER", ACCESS_KEY)
            .withEnv("MINIO_ROOT_PASSWORD", SECRET_KEY)
            .withCommand("server", "/data")
            .waitingFor(Wait.forHttp("/minio/health/ready").forPort(9000));

    @BeforeEach
    public void init() throws Exception {
        String endpoint = "http://" + minioContainer.getHost() + ":" +
                minioContainer.getMappedPort(9000);

        // Создаем клиент Minio
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build();

        // Создаем тестовый бакет
        String bucketName = "test-bucket";
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
    }


    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @Test
    void contextLoad() {
    }
}
