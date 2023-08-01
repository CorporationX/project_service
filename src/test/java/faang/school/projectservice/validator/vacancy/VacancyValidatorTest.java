package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VacancyValidatorTest {
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private VacancyValidator validator;

    @Test
    public void createVacancyControllerValidation_VacancyDtoIsNull_Test() {
        VacancyValidationException exception = Assertions.assertThrows(VacancyValidationException.class,
                () -> validator.createVacancyControllerValidation(null));

        Assertions.assertEquals(exception.getMessage(), "Your vacancy is null!");
    }

    @Test
    public void createVacancyControllerValidation_NullProjectId_Test() {
        VacancyValidationException exception = Assertions.assertThrows(VacancyValidationException.class,
                () -> validator.createVacancyControllerValidation(VacancyDto.builder().projectId(null).build()));

        Assertions.assertEquals(exception.getMessage(), "Illegal project!");
    }
}
