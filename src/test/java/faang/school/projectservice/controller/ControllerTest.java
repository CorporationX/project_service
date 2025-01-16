package faang.school.projectservice.controller;

import org.junit.jupiter.api.Test;

public class ControllerTest {
    @Test
    void shouldFail() {
        System.out.println("Запуск теста...");
        throw new RuntimeException("Тест провалился намеренно!");
    }
}
