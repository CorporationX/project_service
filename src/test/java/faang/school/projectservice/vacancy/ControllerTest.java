package faang.school.projectservice.vacancy;

import faang.school.projectservice.controller.VacancyController;
import faang.school.projectservice.dto.vacancy.VacancyDtoFilter;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.VacancyService;
import faang.school.projectservice.validator.VacancyControllerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {

    @InjectMocks
    private VacancyController vacancyController;

    @Mock
    private VacancyService vacancyService;
    @Mock
    private VacancyControllerValidator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Метод создания новой вакансии отработал")
    @Test
    public void testCreateVacancyWhenValid() {
        Vacancy vacancy = new Vacancy();
        Integer count = 1;
        vacancyController.createVacancy(vacancy, count);

        verify(vacancyService, times(1)).createVacancy(vacancy, count);
    }

    @DisplayName("В метод обновления  вакансии передан id 0 или меньше")
    @Test
    public void testUpdateVacancyIdNegative() {
        long vacancyId = 0;
        TeamRole teamRole = TeamRole.OWNER;

        vacancyController.updateVacancy(vacancyId, teamRole);
        verify(vacancyService, times(0)).updateVacancy(vacancyId, teamRole);
    }

    @DisplayName("Метод обновления вакансии отработал")
    @Test
    public void testUpdateVacancyWhenValid() {
        long vacancyId = 1L;
        TeamRole teamRole = TeamRole.OWNER;

        vacancyController.updateVacancy(vacancyId, teamRole);
        verify(vacancyService, times(1)).updateVacancy(vacancyId, teamRole);
    }

    @DisplayName("В метод удаления вакансии передан id 0 или меньше")
    @Test
    public void testDeleteVacancyIdNegative() {
        long vacancyId = 0;
        validator.validatorId(vacancyId);
        vacancyController.deleteVacancy(vacancyId);

        verify(vacancyService, times(0)).deleteVacancy(vacancyId);
    }

    @DisplayName("Метод удаления вакансии отработал")
    @Test
    public void testDeleteVacancyWhenValid() {
        long vacancyId = 1L;
        validator.validatorId(vacancyId);
        vacancyController.deleteVacancy(vacancyId);

        verify(vacancyService, times(1)).deleteVacancy(vacancyId);
    }

    @DisplayName("Метод получения вакансии по фильтрам отработал")
    @Test
    public void testGetVacancyPositionAndNameWhenWalid() {
        VacancyDtoFilter vacancyDtoFilter = new VacancyDtoFilter();
        vacancyController.getVacancyPositionAndName(vacancyDtoFilter);

        verify(vacancyService, times(1)).getVacancyPositionAndName(vacancyDtoFilter);
    }

    @DisplayName("В метод получения вакансии передан id 0 или меньше")
    @Test
    public void testGetVacancyByIdNegative() {
        long vacancyId = 0;
        validator.validatorId(vacancyId);
        vacancyController.getVacancyById(vacancyId);

        verify(vacancyService, times(0)).getVacancy(vacancyId);
    }

    @DisplayName("Метод получения вакансии отработал")
    @Test
    public void testGetVacancyByIdWhenValid() {
        long vacancyId = 1L;
        validator.validatorId(vacancyId);
        vacancyController.getVacancyById(vacancyId);

        verify(vacancyService, times(1)).getVacancy(vacancyId);
    }
}