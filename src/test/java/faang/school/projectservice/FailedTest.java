package faang.school.projectservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class FailedTest {

    @Test
    public void test() {
        var bad = false;
        Assertions.assertThat(bad).isTrue();
    }
}
