package faang.school.projectservice;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {"spring.liquibase.enabled=false"})
@ActiveProfiles("test")
class ProjectServiceApplicationTest {

    @MockBean
    private FeignClient feignClient;

    @MockBean
    private AmazonS3 amazonS3;

    @Test
    void contextLoads() {
    }
}