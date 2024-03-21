package faang.school.projectservice.springContext;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class SpringContextTest {
    @MockBean
    private AmazonS3 clientAmazonS3;

    @Test
    public void contextLoads() {
    }
}
