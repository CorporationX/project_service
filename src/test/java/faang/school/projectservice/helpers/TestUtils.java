package faang.school.projectservice.helpers;

import org.junit.jupiter.api.function.Executable;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestUtils {

    public static <T extends RuntimeException> void assertThrowsWithMessage(
            Class<T> expectedType,
            String expectedMessage,
            Executable executable) {

        T exception = assertThrows(expectedType, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
