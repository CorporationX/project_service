package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {

    @Mock
    VacancyService vacancyService;

    @InjectMocks
    VacancyController vacancyController;

    @Test
    public void testCreateVacancyThrowExcForName() {
        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> vacancyController.createVacancy(new VacancyDto()));
        assertEquals("Vacancy can't have create an empty name", e.getMessage());
    }

    @Test
    public void testCreateVacancyThrowExcForProjectId() {
        VacancyDto dto = VacancyDto
                .builder()
                .name("a")
                .build();

        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> vacancyController.createVacancy(dto));
        assertEquals("Vacancy should have correct project id", e.getMessage());
    }

    @Test
    public void testCreateVacancyThrowExcForCreatedBy() {
        VacancyDto dto = VacancyDto
                .builder()
                .name("a")
                .projectId(1L)
                .build();

        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> vacancyController.createVacancy(dto));
        assertEquals("Vacancy should have correct creator id", e.getMessage());
    }

    @Test
    public void testCreateVacancy() {
        VacancyDto dto = VacancyDto
                .builder()
                .name("a")
                .projectId(1L)
                .createdBy(1L)
                .build();

        vacancyController.createVacancy(dto);

        Mockito.verify(vacancyService, Mockito.times(1)).createVacancy(dto);
    }
}