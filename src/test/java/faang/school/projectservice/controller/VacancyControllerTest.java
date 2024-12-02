package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateDeleteVacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {
    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

    @BeforeEach
    public void setUp() {
        vacancyController = new VacancyController(vacancyService);
    }

    @Test
    public void testIfCreatesVacancyWithValidDto() {
        // arrange
        CreateVacancyDto createVacancyDto = CreateVacancyDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .projectId(1L)
                .count(1)
                .createdBy(1L)
                .build();

        // act
        vacancyController.createVacancy(createVacancyDto);

        // assert
        verify(vacancyService).createVacancy(createVacancyDto);
    }

    @Test
    public void testIfThrowsExceptionWhenDeletingVacancyWithNegativeId() {
        UpdateDeleteVacancyDto vacancyDto = UpdateDeleteVacancyDto.builder()
                .id(-1L)
                .build();


        assertThrows(DataValidationException.class,
                () -> vacancyController.deleteVacancy(vacancyDto));
    }

    @Test
    public void testIfDeletesVacancyWithValidDto() {
        // arrange
        UpdateDeleteVacancyDto vacancyDto = UpdateDeleteVacancyDto.builder()
                .id(1L)
                .build();

        // act
        vacancyController.deleteVacancy(vacancyDto);

        // assert
        verify(vacancyService).deleteVacancy(vacancyDto);
    }

    @Test
    public void testIfGetsVacancyWithValidId() {
        // arrange
        Long id = 1L;

        // act
        vacancyController.getVacancy(id);

        // assert
        verify(vacancyService).getVacancy(id);
    }

    @Test
    public void testIfGetsFilteredVacanciesWithValidNameAndPosition() {
        // arrange
        String name = "name";
        String position = "position";

        // act
        vacancyController.getFilteredVacancies(name, position);

        // assert
        verify(vacancyService).getFilteredVacancies(name, position);
    }

    @Test
    public void testIfUpdatesVacancyWithValidDto() {
        // arrange
        UpdateDeleteVacancyDto vacancyDto = UpdateDeleteVacancyDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .projectId(1L)
                .count(1)
                .createdBy(1L)
                .build();

        // act
        vacancyController.updateVacancy(vacancyDto);

        // assert
        verify(vacancyService).updateVacancy(vacancyDto);
    }
}