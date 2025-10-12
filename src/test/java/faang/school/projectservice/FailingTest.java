package faang.school.projectservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FailingTest {
    @Test
    void shouldFailIntentionally() {
        Assertions.fail("Тест CI");
    }
}
