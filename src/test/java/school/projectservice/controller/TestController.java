package school.projectservice.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestController {

    @Test
    void shouldFail() {
        String expected = "Fail";
        String actual = "This is a test endpoint";
        assertEquals(expected, actual, "The test is designed to fail.");
    }

}
