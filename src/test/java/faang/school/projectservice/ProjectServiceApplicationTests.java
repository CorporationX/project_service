package faang.school.projectservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;


class ProjectServiceApplicationTests {
    @Test
    void contextLoads() {
        Assertions.assertThat(40 + 2).isEqualTo(42);
    }

    @Test
    void failedTest(){
        fail();
    }
}
