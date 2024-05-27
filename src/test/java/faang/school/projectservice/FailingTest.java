package faang.school.projectservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class FailingTest {
    @Test
    public void testThatFails() {
        fail("This test is designed to fail");
    }
}
