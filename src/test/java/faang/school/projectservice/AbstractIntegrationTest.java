package faang.school.projectservice;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@ActiveProfiles("it")
public abstract class AbstractIntegrationTest {
    public static final String MINIO_BUCKET = "projectbucket";

    private static final String MINIO_DOCKER_IMAGE = "minio/minio:latest";
    private static final int MINIO_EXPOSED_PORT = 9000;
    private static final String MINIO_USER = "user";
    private static final String MINIO_PASSWORD = "password";

    @Autowired
    public MinioClient minioClient;

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER
            = new PostgreSQLContainer<>("postgres:13.6");

    @Container
    public static final GenericContainer<?> MINIO_CONTAINER
            = new GenericContainer<>(DockerImageName.parse(MINIO_DOCKER_IMAGE))
            .withExposedPorts(MINIO_EXPOSED_PORT)
            .withEnv("MINIO_ROOT_USER", MINIO_USER)
            .withEnv("MINIO_ROOT_PASSWORD", MINIO_PASSWORD)
            .withCommand("server", "/data")
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(MINIO_EXPOSED_PORT),
                            new ExposedPort(MINIO_EXPOSED_PORT)))));

    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("MINIO_PORT", MINIO_CONTAINER::getFirstMappedPort);
        registry.add("MINIO_ACCESS_KEY", () -> MINIO_USER);
        registry.add("MINIO_SECRET_KEY", () -> MINIO_PASSWORD);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
