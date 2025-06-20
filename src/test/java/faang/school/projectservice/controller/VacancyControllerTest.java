package faang.school.projectservice.controller;

import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.dto.vacancy.CloseVacancyDto;
import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.service.vacancy.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {
    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

    private CreateVacancyDto createDto;
    private UpdateVacancyDto updateDto;
    private CloseVacancyDto closeDto;
    private VacancyResponseDto responseDto;
    private VacancyFilterDto filterDto;

    @BeforeEach
    void setUp() {
        createDto = CreateVacancyDto.builder()
                .projectId(1L)
                .position(TeamRole.DEVELOPER)
                .count(3)
                .name("Java Developer")
                .description("Backend developer")
                .build();

        updateDto = UpdateVacancyDto.builder()
                .id(1L)
                .count(5)
                .name("Senior Java Developer")
                .description("Updated description")
                .candidatesToAdd(List.of())
                .build();

        closeDto = new CloseVacancyDto(List.of(101L));
        filterDto = new VacancyFilterDto("Java", "DEVELOPER");

        responseDto = VacancyResponseDto.builder()
                .id(1L)
                .projectId(1L)
                .name("Java Developer")
                .description("Backend developer")
                .position(TeamRole.DEVELOPER)
                .count(3)
                .status(VacancyStatus.OPEN)
                .candidates(List.of(
                        CandidateDto.builder()
                                .id(101L)
                                .candidateStatus(CandidateStatus.WAITING_RESPONSE)
                                .build()
                ))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Создание вакансии - успешный сценарий")
    void createVacancy_shouldReturnCreatedVacancy() {
        when(vacancyService.createVacancy(any())).thenReturn(responseDto);

        VacancyResponseDto result = vacancyController.createVacancy(createDto);

        assertEquals(responseDto, result);
        verify(vacancyService).createVacancy(createDto);
    }

    @Test
    @DisplayName("Обновление вакансии - успешный сценарий")
    void updateVacancy_shouldReturnUpdatedVacancy() {
        when(vacancyService.updateVacancy(any())).thenReturn(responseDto);

        VacancyResponseDto result = vacancyController.updateVacancy(1L, updateDto);

        assertEquals(responseDto, result);
        verify(vacancyService).updateVacancy(updateDto);
    }

    @Test
    @DisplayName("Закрытие вакансии - успешный сценарий")
    void closeVacancy_shouldReturnVacancyDto() {
        when(vacancyService.closeVacancy(anyLong(), any())).thenReturn(responseDto);

        VacancyResponseDto result = vacancyController.closeVacancy(1L, closeDto);

        assertEquals(responseDto, result);
        verify(vacancyService).closeVacancy(1L, closeDto);
    }

    @Test
    @DisplayName("Получение отфильтрованных вакансий - успешный сценарий")
    void getFilteredVacancies_shouldReturnFilteredList() {
        when(vacancyService.getFilteredVacancies(any())).thenReturn(List.of(responseDto));

        List<VacancyResponseDto> result = vacancyController.getFilteredVacancies(filterDto);

        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));
        verify(vacancyService).getFilteredVacancies(filterDto);
    }

    @Test
    @DisplayName("Получение вакансии по ID - успешный сценарий")
    void getVacancyById_shouldReturnVacancy() {
        when(vacancyService.getVacancyById(1L)).thenReturn(responseDto);

        VacancyResponseDto result = vacancyController.getVacancyById(1L);

        assertEquals(responseDto, result);
        verify(vacancyService).getVacancyById(1L);
    }
}