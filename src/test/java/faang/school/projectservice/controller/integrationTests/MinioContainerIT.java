package faang.school.projectservice.controller.integrationTests;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class MinioContainerIT {

    static String dockerImage = "minio/minio:latest";
    static int exposedPort = 9000;
    static String accessKey = "minioaccesskey";
    static String secretKey = "miniosecretkey";

    @Container
    private static final GenericContainer<?> minio = new GenericContainer<>(DockerImageName.parse(dockerImage))
            .withExposedPorts(exposedPort)
            .withEnv("MINIO_ROOT_USER", accessKey)
            .withEnv("MINIO_ROOT_PASSWORD", secretKey)
            .withCommand("server", "/data")
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(exposedPort), new ExposedPort(exposedPort)))));

    private S3Client s3Client;

    @DynamicPropertySource
    static void configureMinio(DynamicPropertyRegistry registry) {
        minio.start();
        String endpoint = "http://" + minio.getHost() + ":" + minio.getMappedPort(9000);
        registry.add("s3.endpoint", () -> endpoint);
        registry.add("s3.access-key", () -> accessKey);
        registry.add("s3.secret-key", () -> secretKey);
    }

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @Test
    void testMinioConnection() throws InterruptedException {
        Thread.sleep(15000);
        s3Client = createS3Client();
        String bucketName = "test-bucket";
        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        assertTrue(s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build()).sdkHttpResponse().isSuccessful());
    }

    private S3Client createS3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create("http://" + minio.getHost() + ":" + minio.getMappedPort(9000)))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .forcePathStyle(true)
                .build();
    }
}