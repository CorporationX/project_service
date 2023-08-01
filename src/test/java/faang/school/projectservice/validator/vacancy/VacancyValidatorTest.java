package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VacancyValidatorTest {
    @InjectMocks
    private VacancyValidator vacancyValidator;

    @Test
    public void updateVacancyControllerValidation_NullVacancyDto_Test() {
        VacancyValidationException exception = Assertions.assertThrows(VacancyValidationException.class,
                () -> vacancyValidator.updateVacancyControllerValidation(null, 1));

        Assertions.assertEquals(exception.getMessage(), "Vacancy not found!");
    }

    @Test
    public void deleteVacancyControllerValidation_WrongDeleter_Test() {
        VacancyValidationException exception = Assertions.assertThrows(VacancyValidationException.class,
                () -> vacancyValidator.updateVacancyControllerValidation(VacancyDto.builder().build(), 0));

        Assertions.assertEquals(exception.getMessage(), "Permission error!");
    }

    @Test
    public void deleteVacancyServiceValidation_WrongDeleter_Test() {
        VacancyValidationException exception = Assertions.assertThrows(VacancyValidationException.class,
                () -> vacancyValidator.updateVacancyServiceValidation(VacancyDto.builder().count(0).createdBy(1L).build(), 2));

        Assertions.assertEquals(exception.getMessage(), "Permission error!");
    }
}
