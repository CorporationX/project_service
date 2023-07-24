package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
        assertEquals("Vacancy can't have an empty name", e.getMessage());
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
    public void testCreateVacancyThrowExcForStatus() {
        VacancyDto dto = VacancyDto
                .builder()
                .name("A")
                .projectId(1L)
                .createdBy(1L)
                .build();

        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> vacancyController.createVacancy(dto));
        assertEquals("Vacancy status can't be null", e.getMessage());
    }

    @Test
    public void testDeleteVacancyThrowDateExc() {
        assertThrows(DataValidationException.class, () -> vacancyController.deleteVacancy(-1));
    }

    @Nested
    class PositiveTests {
        VacancyDto dto;

        @BeforeEach
        public void setUp() {
            dto = VacancyDto
                    .builder()
                    .name("a")
                    .projectId(1L)
                    .createdBy(1L)
                    .status(VacancyStatus.OPEN)
                    .build();
        }

        @Test
        public void testCreateVacancy() {
            vacancyController.createVacancy(dto);
            Mockito.verify(vacancyService, Mockito.times(1)).createVacancy(dto);
        }

        @Test
        public void testUpdateVacancy() {
            vacancyController.updateVacancy(dto);
            Mockito.verify(vacancyService, Mockito.times(1)).updateVacancy(dto);
        }

        @Test
        public void testDeleteVacancy() {
            vacancyController.deleteVacancy(1);
            Mockito.verify(vacancyService, Mockito.times(1)).deleteVacancy(1);
        }
    }
}