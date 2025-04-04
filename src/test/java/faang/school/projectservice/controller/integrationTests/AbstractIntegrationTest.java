package faang.school.projectservice.controller.integrationTests;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("it")
@Sql(scripts = {"/clear.sql"}, executionPhase = BEFORE_TEST_METHOD)
public abstract class AbstractIntegrationTest {
    public static final String MINIO_BUCKET = "projectbucket";

    private static final String MINIO_DOCKER_IMAGE = "minio/minio:latest";
    private static final int MINIO_EXPOSED_PORT = 9000;
    private static final String MINIO_USER = "user";
    private static final String MINIO_PASSWORD = "password";

    @Autowired
    protected MockMvc mockMvc;

    protected final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    public MinioClient minioClient;

    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER
            = new PostgreSQLContainer<>("postgres:13.6").withReuse(true);

    public static final GenericContainer<?> MINIO_CONTAINER
            = new GenericContainer<>(DockerImageName.parse(MINIO_DOCKER_IMAGE)).withReuse(true)
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

    @BeforeAll
    static void init() {
        POSTGRESQL_CONTAINER.start();
        MINIO_CONTAINER.start();
    }
}
