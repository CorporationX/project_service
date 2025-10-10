package faang.school.projectservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FailingTest {

    @Test
    void comprehensiveFailureDemo() {

        String expected = "hello";
        String actual = "hllo";
        assertEquals(expected, actual, "Тест упал");
    }
}
