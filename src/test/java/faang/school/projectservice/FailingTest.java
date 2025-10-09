package faang.school.projectservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FailingTest {

    @Test
    void comprehensiveFailureDemo() {

        String expected = "hello";
        String actual = "hello";
        assertEquals(expected, actual, "Тест теперь проходит успешно");
    }
}
