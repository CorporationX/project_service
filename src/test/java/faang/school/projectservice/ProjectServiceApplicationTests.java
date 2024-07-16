package faang.school.projectservice;

import org.assertj.core.api.Assertions;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;


class ProjectServiceApplicationTests {
    @Test
    void contextLoads() {
        Assertions.assertThat(40 + 2).isEqualTo(42);
    }

    @Test
    void testShouldFail(){
        fail("Test failed !!!!");
    }
}
