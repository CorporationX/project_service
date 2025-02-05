package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.mapper.CandidateMapper;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.service.vacancy.VacancyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {
    VacancyService vacancyService = mock(VacancyService.class);
    VacancyMapper vacancyMapper = mock(VacancyMapper.class);
    CandidateMapper candidateMapper = mock(CandidateMapper.class);
    UserContext userContext = mock(UserContext.class);
    VacancyController vacancyController = new VacancyController(vacancyService, vacancyMapper, userContext,
            candidateMapper);
    VacancyUpdateDto vacancyUpdateDto;
    Vacancy vacancy;

    @Test
    void createVacancy() {
        Vacancy exceptedVacancy = Vacancy.builder()
                .id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(List.of())
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();

        VacancyResponseDto excepted = VacancyResponseDto.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .candidatesId(List.of())
                .status(VacancyStatus.OPEN)
                .build();

        when(userContext.getUserId()).thenReturn(1L);
        when(vacancyMapper.toEntity(vacancyUpdateDto)).thenReturn(vacancy);
        when(vacancyService.createVacancy(vacancy, 1L)).thenReturn(exceptedVacancy);
        when(vacancyMapper.toDto(exceptedVacancy)).thenReturn(excepted);

        ResponseEntity<VacancyResponseDto> actual = vacancyController.createVacancy(vacancyUpdateDto);
        Assertions.assertEquals(ResponseEntity.ok(excepted), actual);

        verify(vacancyService, times(1)).createVacancy(vacancy, 1L);
        verify(userContext, times(1)).getUserId();
        verify(vacancyMapper, times(1)).toDto(exceptedVacancy);
        verify(vacancyMapper, times(1)).toEntity(vacancyUpdateDto);
    }

    @Test
    void closeVacancy() {
        when(userContext.getUserId()).thenReturn(11L);
        VacancyResponseDto expectedDto = VacancyResponseDto.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .status(VacancyStatus.CLOSED)
                .candidatesId(List.of(1L, 2L, 3L))
                .build();

        Vacancy vacancy = Vacancy.builder().id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .build())
                .candidates(List.of())
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(11L)
                .updatedAt(LocalDateTime.of(2025, 1, 23, 15, 20))
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();

        when(vacancyService.closeVacancy(1L, 11L)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(expectedDto);

        ResponseEntity<VacancyResponseDto> actual = vacancyController.closeVacancy(1L);
        ResponseEntity<VacancyResponseDto> expected = ResponseEntity.ok(expectedDto);

        Assertions.assertEquals(expected, actual);
        verify(userContext, times(1)).getUserId();
        verify(vacancyService, times(1)).closeVacancy(1L, 11L);
    }

    @Test
    void updateVacancy() {
        when(userContext.getUserId()).thenReturn(1L);

        Vacancy exceptedVacancy = Vacancy.builder()
                .id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(List.of())
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(11L)
                .updatedAt(LocalDateTime.of(2025, 1, 23, 15, 20))
                .updatedBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();

        VacancyResponseDto expectedDto = VacancyResponseDto.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .candidatesId(List.of())
                .status(VacancyStatus.OPEN)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();

        when(vacancyMapper.toEntity(vacancyUpdateDto)).thenReturn(vacancy);
        when(userContext.getUserId()).thenReturn(1L);
        when(vacancyService.updateVacancy(1L, vacancy, 1L)).thenReturn(exceptedVacancy);
        when(vacancyMapper.toDto(exceptedVacancy)).thenReturn(expectedDto);

        ResponseEntity<VacancyResponseDto> actual = vacancyController.updateVacancy(1L, vacancyUpdateDto);
        ResponseEntity<VacancyResponseDto> expected = ResponseEntity.ok(expectedDto);
        Assertions.assertEquals(expected, actual);

        verify(userContext, times(1)).getUserId();
        verify(vacancyService, times(1)).updateVacancy(1L, vacancy, 1L);
        verify(vacancyMapper, times(1)).toDto(exceptedVacancy);
        verify(vacancyMapper, times(1)).toEntity(vacancyUpdateDto);
    }

    @Test
    void deleteVacancy() {
        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(vacancyService).deleteVacancy(1L, 1L);

        Assertions.assertDoesNotThrow(() -> vacancyController.deleteVacancy(1L));
        verify(vacancyService, times(1)).deleteVacancy(1L, 1L);
        verify(userContext, times(1)).getUserId();
    }

    @Test
    void getVacancy() {
        Vacancy vacancy = Vacancy.builder()
                .id(1L)
                .name("vacancy")
                .build();
        when(vacancyService.getVacancy(1L)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(VacancyResponseDto.builder()
                .name("vacancy")
                .build());

        ResponseEntity<VacancyResponseDto> expected = ResponseEntity.ok(VacancyResponseDto.builder()
                .name("vacancy")
                .build());
        ResponseEntity<VacancyResponseDto> actual = vacancyController.getVacancy(1L);

        Assertions.assertEquals(expected, actual);
        verify(vacancyService, times(1)).getVacancy(1L);
        verify(vacancyMapper, times(1)).toDto(vacancy);
    }

    @Test
    void getVacanciesByFilters() {
        VacancyFilterDto vacancyFilterDto = VacancyFilterDto.builder()
                .position(TeamRole.ANALYST)
                .name("test")
                .build();

        List<Vacancy> vacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .position(TeamRole.ANALYST)
                        .name("test")
                        .build(),
                Vacancy.builder()
                        .id(2L)
                        .position(TeamRole.ANALYST)
                        .name("test-vacancy")
                        .build()
        );

        List<VacancyResponseDto> vacanciesDto = List.of(
                VacancyResponseDto.builder()
                        .position(TeamRole.ANALYST)
                        .name("test")
                        .build(),
                VacancyResponseDto.builder()
                        .position(TeamRole.ANALYST)
                        .name("test-vacancy")
                        .build()
        );

        when(vacancyService.getVacancies(vacancyFilterDto)).thenReturn(vacancies);
        when(vacancyMapper.toDtoList(vacancies)).thenReturn(vacanciesDto);

        ResponseEntity<List<VacancyResponseDto>> actual = vacancyController.getVacanciesByFilters(vacancyFilterDto);
        ResponseEntity<List<VacancyResponseDto>> expected = ResponseEntity.ok(vacanciesDto);

        Assertions.assertEquals(expected, actual);
        verify(vacancyService, times(1)).getVacancies(vacancyFilterDto);
        verify(vacancyMapper, times(1)).toDtoList(vacancies);
    }

    @Test
    void addCandidates() {
        VacancyResponseDto vacancyResponseDto = VacancyResponseDto.builder()
                .name("vacancy")
                .build();
        when(userContext.getUserId()).thenReturn(5L);

        List<CandidateDto> candidatesDtos = IntStream.rangeClosed(1, 3).boxed()
                .map(i ->
                        CandidateDto.builder()
                                .userId(Long.valueOf(i))
                                .build()
                ).toList();

        List<Candidate> candidates = IntStream.rangeClosed(1, 3).boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                }).toList();

        when(candidateMapper.toEntityList(candidatesDtos)).thenReturn(candidates);
        when(vacancyMapper.toDto(any(Vacancy.class))).thenReturn(vacancyResponseDto);
        when(vacancyService.addCandidates(candidates, 2L, 5L)).thenReturn(Vacancy.builder()
                .id(2L)
                .build());

        ResponseEntity<VacancyResponseDto> expected = ResponseEntity.ok(vacancyResponseDto);
        ResponseEntity<VacancyResponseDto> actual = vacancyController.addCandidates(2L, candidatesDtos);

        Assertions.assertEquals(expected, actual);
        verify(vacancyService, times(1)).addCandidates(candidates, 2L, 5L);
        verify(vacancyMapper, times(1)).toDto(any(Vacancy.class));
        verify(candidateMapper, times(1)).toEntityList(candidatesDtos);
    }

    @BeforeEach
    void setVacancyUpdateDto() {
        vacancyUpdateDto = VacancyUpdateDto.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
    }

    @BeforeEach
    void setVacancy() {
        vacancy = Vacancy.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .build())
                .candidates(List.of())
                .createdBy(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
    }
}