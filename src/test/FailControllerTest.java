import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class FailControllerTest {

    @Test
    void shouldFail() {
        // Этот тест гарантированно провалится
        fail("Этот тест был создан для проверки, что пайплайн обрабатывает упавшие тесты.");
    }
}