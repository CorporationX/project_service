package faang.school.projectservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {
    @Test
    void testFormatWithOneParams() {
        Utils utils = new Utils();
        String expected = "format one word";
        assertEquals(expected, utils.format("format {} word", "one"));
    }

    @Test
    void testFormatWithTwoParams() {
        Utils utils = new Utils();
        String expected = "format one word, two word";
        assertEquals(expected, utils.format("format {} word, {} word", "one", "two"));
    }

    @Test
    void testFormatWithThreeParams() {
        Utils utils = new Utils();
        String expected = "format one word, two word, three word";
        assertEquals(expected, utils.format("format {} word, {} word, {} word",
                "one", "two", "three"));
    }
}