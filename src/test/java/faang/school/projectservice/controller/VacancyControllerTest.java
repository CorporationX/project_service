package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {

    @InjectMocks
    private VacancyController vacancyController;
    @Mock
    private VacancyService vacancyService;

    private long vacancyId;
    private VacancyDto vacancyDto;

    @BeforeEach
    public void setUp() {
        vacancyId = 1L;
        vacancyDto = VacancyDto.builder().build();
    }

    @DisplayName("Метод создания новой вакансии отработал")
    @Test
    public void testCreateVacancyWhenValid() {
        when(vacancyService.createVacancy(vacancyDto)).thenReturn(vacancyDto);

        vacancyController.createVacancy(vacancyDto);

        verify(vacancyService, times(1)).createVacancy(vacancyDto);
    }

    @DisplayName("Метод обновления вакансии отработал")
    @Test
    public void testUpdateVacancyWhenValid() {
        when(vacancyService.updateVacancy(vacancyDto, vacancyId)).thenReturn(vacancyDto);

        vacancyController.updateVacancy(vacancyDto, vacancyId);

        verify(vacancyService, times(1)).updateVacancy(vacancyDto, vacancyId);
    }

    @DisplayName("Метод закрытия вакансии отработал")
    @Test
    public void testCloseVacancyWhenValid() {
        vacancyController.closeVacancy(vacancyId);
        verify(vacancyService, times(1)).closeVacancy(vacancyId);
    }

    @DisplayName("Метод удаления вакансии отработал")
    @Test
    public void testDeleteVacancyWhenValid() {
        vacancyController.deleteVacancy(vacancyId);

        verify(vacancyService, times(1)).deleteVacancy(vacancyId);
    }

    @DisplayName("Метод получения вакансии по фильтрам отработал")
    @Test
    public void testGetVacancyPositionAndNameWhenWalid() {
        List<VacancyDto> result = List.of(vacancyDto);
        VacancyFilterDto vacancyDtoFilter = new VacancyFilterDto();
        when(vacancyController.getVacancyPositionAndName(vacancyDtoFilter)).thenReturn(result);

        vacancyController.getVacancyPositionAndName(vacancyDtoFilter);

        verify(vacancyService, times(1)).getVacancyPositionAndName(vacancyDtoFilter);
    }

    @DisplayName("Метод получения вакансии отработал")
    @Test
    public void testGetVacancyByIdWhenValid() {
        when(vacancyController.getVacancyById(vacancyId)).thenReturn(vacancyDto);

        vacancyController.getVacancyById(vacancyId);

        verify(vacancyService, times(1)).getVacancyById(vacancyId);
    }
}