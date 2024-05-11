package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VacancyValidatorTest {
    @Mock
    private VacancyValidator vacancyValidator;

    private VacancyDto vacancy;

    @BeforeEach
    public void init() {
        vacancy = new VacancyDto();
        vacancy.setName("Yandex");
        vacancy.setCount(3);
    }

    @Test
    public void testIfVacancyNameIsNull() {
        vacancy.setName(null);
        doThrow(new DataValidationException("name of vacancy doesn't exist")).when(vacancyValidator).validateVacancyName(vacancy);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> vacancyValidator.validateVacancyName(vacancy));
        assertEquals("name of vacancy doesn't exist", thrownException.getMessage());
        verify(vacancyValidator, times(1)).validateVacancyName(vacancy);
    }

    @Test
    public void testIfVacancyNameIsBlank() {
        vacancy.setName(" ");
        doThrow(new DataValidationException("name of vacancy is blank")).when(vacancyValidator).validateVacancyName(vacancy);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> vacancyValidator.validateVacancyName(vacancy));
        assertEquals("name of vacancy is blank", thrownException.getMessage());
        verify(vacancyValidator, times(1)).validateVacancyName(vacancy);
    }

    @Test
    public void testIfVacancyLessThenZero(){
        vacancy.setCount(0);
        doThrow(new DataValidationException("count of vacancy is wrong")).when(vacancyValidator).validateCountOfVacancy(vacancy);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> vacancyValidator.validateCountOfVacancy(vacancy));
        assertEquals("count of vacancy is wrong", thrownException.getMessage());
        verify(vacancyValidator, times(1)).validateCountOfVacancy(vacancy);
    }
}
