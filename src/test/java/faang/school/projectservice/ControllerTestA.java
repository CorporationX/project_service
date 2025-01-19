package faang.school.projectservice;

import org.junit.jupiter.api.Test;

public class ControllerTestA {
    @Test
    void shouldFail() {
        System.out.println("Запуск теста...");
        throw new RuntimeException("Тест провалился намеренно!");
    }
}