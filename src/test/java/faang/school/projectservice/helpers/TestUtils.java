package faang.school.projectservice.helpers;

import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestUtils {

    public static <T extends RuntimeException> void assertThrowsAny(
            Class<T> expectedType,
            Executable executable) {

        assertThrows(expectedType, executable);
    }
}
