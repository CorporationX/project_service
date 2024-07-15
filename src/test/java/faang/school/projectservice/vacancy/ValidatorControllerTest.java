package faang.school.projectservice.vacancy;

import faang.school.projectservice.validator.VacancyControllerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ValidatorControllerTest {

    @InjectMocks
    private VacancyControllerValidator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidatorIdNegative() {
        long id = 0;
        assertThrows(IllegalArgumentException.class, () -> validator.validatorId(id));
    }

    @Test
    public void testValidatorIdWhenValid() {
        long id = 1L;
        assertDoesNotThrow(() -> validator.validatorId(id));
    }
}