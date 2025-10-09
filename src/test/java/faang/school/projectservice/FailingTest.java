package faang.school.projectservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FailingTest {

    @Test
    void comprehensiveFailureDemo() {

        String expected = "hello";
        String actual = "helo";
        assertEquals(expected, actual, "Демонстрация падения теста из-за опечатки");
    }
}
