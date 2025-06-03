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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    @DisplayName("testCreateVacancy - успешное создание вакансии")
    void testCreateVacancy_shouldReturnCreatedVacancy() {
        when(vacancyService.createVacancy(any())).thenReturn(responseDto);

        ResponseEntity<VacancyResponseDto> response = vacancyController.createVacancy(createDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(vacancyService).createVacancy(createDto);
    }

    @Test
    @DisplayName("testUpdateVacancy - успешное обновление")
    void testUpdateVacancy_shouldReturnUpdatedVacancy() {
        when(vacancyService.updateVacancy(any())).thenReturn(responseDto);

        ResponseEntity<VacancyResponseDto> response = vacancyController.updateVacancy(1L, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(vacancyService).updateVacancy(updateDto);
    }

    @Test
    @DisplayName("testCloseVacancy - успешное закрытие")
    void testCloseVacancy_shouldReturnDto() {
        when(vacancyService.closeVacancy(anyLong(), any()))
                .thenReturn(responseDto);

        ResponseEntity<VacancyResponseDto> response = vacancyController.closeVacancy(1L, closeDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(vacancyService).closeVacancy(1L, closeDto);
    }

    @Test
    @DisplayName("testGetFilteredVacancies - успешная фильтрация")
    void testGetFilteredVacancies_shouldReturnFiltered() {
        when(vacancyService.getFilteredVacancies(any())).thenReturn(List.of(responseDto));

        ResponseEntity<List<VacancyResponseDto>> response = vacancyController.getFilteredVacancies(filterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(responseDto, response.getBody().get(0));
        verify(vacancyService).getFilteredVacancies(filterDto);
    }

    @Test
    @DisplayName("testGetVacancyById - успешное получение")
    void testGetVacancyById_shouldReturnVacancy() {
        when(vacancyService.getVacancyById(1L)).thenReturn(responseDto);

        ResponseEntity<VacancyResponseDto> response = vacancyController.getVacancyById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(vacancyService).getVacancyById(1L);
    }
}