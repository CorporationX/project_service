package faang.school.projectservice.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты TestService тестовые")
public class TestServiceTest {

    @InjectMocks
    private TestService testService;

    @Test
    @DisplayName("Тест TestService")
    public void exampleTest() {
        assertThat(testService.testMethod(1,2)).isEqualTo(2);
    }

}