package faang.school.projectservice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ProjectServiceApplication.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Testcontainers
public class AppContextTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER;

    @Container
    public static MinIOContainer MINIO_CONTAINER;

    static  {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.3");
        MINIO_CONTAINER = new MinIOContainer("minio/minio");
    }

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();
        POSTGRESQL_CONTAINER.waitingFor(Wait.forListeningPort());
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        MINIO_CONTAINER.start();
        MINIO_CONTAINER.waitingFor(Wait.forListeningPort());
        registry.add("services.s3.endpoint", MINIO_CONTAINER::getS3URL);
        registry.add("services.s3.accessKey", MINIO_CONTAINER::getUserName);
        registry.add("services.s3.secretKey", MINIO_CONTAINER::getPassword);
    }

    @Test
    public void contextLoads() {
        assertNotNull(applicationContext);

        jdbcTemplate.execute("SELECT 1");

        assertTrue(POSTGRESQL_CONTAINER.isRunning());
        assertTrue(MINIO_CONTAINER.isRunning());

        String result = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
        assertNotNull(result);
    }

    @AfterAll
    public static void tearDown() {
        POSTGRESQL_CONTAINER.stop();
        MINIO_CONTAINER.stop();
    }
}
