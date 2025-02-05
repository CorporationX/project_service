package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.candidate.CandidateService;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    private final VacancyRepository vacancyRepository = Mockito.mock(VacancyRepository.class);
    private final ProjectService projectService = Mockito.mock(ProjectService.class);
    private final VacancyValidator vacancyValidator = Mockito.mock(VacancyValidator.class);
    private final CandidateService candidateService = Mockito.mock(CandidateService.class);
    private final List<VacancyFilter> vacancyFilters = new ArrayList<>();
    private final VacancyService vacancyService = new VacancyService(vacancyRepository, projectService, vacancyValidator,
            candidateService, vacancyFilters);

    @Test
    void createVacancy() {
        Vacancy sourceVacancy = Vacancy.builder()
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

        Vacancy excepted = Vacancy.builder()
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

        when(projectService.findProjectById(1L)).thenReturn(Project.builder().id(1L).build());
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(excepted);
        doNothing().when(vacancyValidator).validateTutorRole(1L, 1L);

        Vacancy actual = vacancyService.createVacancy(sourceVacancy, 1L);
        verify(vacancyRepository, times(1)).save(sourceVacancy);
        Assertions.assertEquals(excepted, actual);
    }


    @Test
    void closeVacancy() {
        List<Candidate> candidates = IntStream.rangeClosed(2, 6)
                .boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                })
                .toList();

        Vacancy sourceVacancy = Vacancy.builder()
                .id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(candidates)
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();

        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(sourceVacancy));
        Vacancy targetVacancy = Vacancy.builder()
                .id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(candidates)
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .updatedAt(LocalDateTime.of(2025, 1, 21, 14, 30))
                .updatedBy(1L)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();

        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(targetVacancy);
        doNothing().when(vacancyValidator).validateVacancyStatus(any(Vacancy.class));
        doNothing().when(vacancyValidator).validateTutorRole(anyLong(), anyLong());
        doNothing().when(vacancyValidator).validateCandidatesCount(any(Vacancy.class));

        Vacancy actual = vacancyService.closeVacancy(1L, 1L);
        Assertions.assertEquals(targetVacancy, actual);

        verify(vacancyRepository, times(1)).save(any(Vacancy.class));
        verify(vacancyValidator, times(1)).validateTutorRole(anyLong(), anyLong());
        verify(vacancyValidator, times(1)).validateCandidatesCount(any(Vacancy.class));
        verify(vacancyValidator, times(1)).validateVacancyStatus(any(Vacancy.class));
        verify(vacancyRepository, times(1)).findById(anyLong());
    }

    @Test
    void closeVacancyWithVacancyIdIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.closeVacancy(null, 1L),
                "vacancyId or tutorId is null");
    }

    @Test
    void closeVacancyWithTutorIdIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.closeVacancy(1L, null),
                "vacancyId or tutorId is null");
    }

    @Test
    void closeVacancyVacancyNotFound() {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.closeVacancy(2L, 1L),
                "vacancy 2 not found");
    }

    @Test
    void updateVacancy() {
        List<Candidate> candidates = IntStream.rangeClosed(2, 6)
                .boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                })
                .toList();

        Vacancy sourceVacancy = Vacancy.builder()
                .id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(candidates)
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();

        Vacancy newVacancy = Vacancy.builder()
                .name("newVacancy")
                .description("newDescription")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(candidates)
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(4000.0)
                .workSchedule(WorkSchedule.FLEXIBLE)
                .count(4)
                .requiredSkillIds(List.of(1L, 2L, 3L, 4L))
                .build();

        Vacancy targetVacancy = Vacancy.builder()
                .id(1L)
                .name("newVacancy")
                .description("newDescription")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(candidates)
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .updatedAt(LocalDateTime.of(2025, 1, 21, 14, 30))
                .updatedBy(2L)
                .status(VacancyStatus.OPEN)
                .salary(4000.0)
                .workSchedule(WorkSchedule.FLEXIBLE)
                .count(4)
                .requiredSkillIds(List.of(1L, 2L, 3L, 4L))
                .build();

        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(sourceVacancy));
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(targetVacancy);
        doNothing().when(vacancyValidator).validateTutorRole(anyLong(), anyLong());
        doNothing().when(vacancyValidator).validateVacancyStatus(any(Vacancy.class));

        Vacancy actual = vacancyService.updateVacancy(1L, newVacancy, 2L);

        Assertions.assertEquals(targetVacancy, actual);
        verify(vacancyRepository, times(1)).save(any(Vacancy.class));
        verify(vacancyValidator, times(1)).validateTutorRole(anyLong(), anyLong());
        verify(vacancyValidator, times(1)).validateVacancyStatus(any(Vacancy.class));
        verify(vacancyRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateVacancyWithNullUpdates() {
        List<Candidate> candidates = IntStream.rangeClosed(2, 6)
                .boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                })
                .toList();

        Vacancy sourceVacancy = Vacancy.builder()
                .id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(candidates)
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();

        Vacancy newVacancy = Vacancy.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(candidates)
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .coverImageKey("coverImageKey")
                .build();

        Vacancy targetVacancy = Vacancy.builder()
                .id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(candidates)
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .updatedAt(LocalDateTime.of(2025, 1, 21, 14, 30))
                .updatedBy(2L)
                .status(VacancyStatus.OPEN)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .coverImageKey("coverImageKey")
                .build();


        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(sourceVacancy));
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(targetVacancy);
        doNothing().when(vacancyValidator).validateTutorRole(anyLong(), anyLong());
        doNothing().when(vacancyValidator).validateVacancyStatus(any(Vacancy.class));

        Vacancy actual = vacancyService.updateVacancy(1L, newVacancy, 2L);

        Assertions.assertEquals(targetVacancy, actual);
        verify(vacancyRepository, times(1)).save(any(Vacancy.class));
        verify(vacancyValidator, times(1)).validateTutorRole(anyLong(), anyLong());
        verify(vacancyValidator, times(1)).validateVacancyStatus(any(Vacancy.class));
        verify(vacancyRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateVacancyWithNullVacancyId() {
        Vacancy newVacancy = Vacancy.builder().build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.updateVacancy(null,
                newVacancy, 1L), "vacancyId, newVacancy or tutorId is null");
    }

    @Test
    void updateVacancyWithNullNewVacancy() {
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.updateVacancy(2L,
                null, 1L), "vacancyId, newVacancy or tutorId is null");
    }

    @Test
    void updateVacancyWithNullTutorId() {
        Vacancy newVacancy = Vacancy.builder().build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.updateVacancy(2L,
                newVacancy, null), "vacancyId, newVacancy or tutorId is null");
    }

    @Test
    void updateVacancyWithNotFoundVacancy() {
        Vacancy newVacancy = Vacancy.builder().build();
        when(vacancyRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.updateVacancy(1L,
                newVacancy, 2L), "vacancy 1 not found");
    }

    @Test
    void deleteVacancy() {
        Vacancy vacancy = Vacancy.builder()
                .id(1L)
                .project(Project.builder()
                        .id(1L)
                        .build())
                .build();

        doNothing().when(candidateService).deleteCandidatesByVacancyId(anyLong());
        doNothing().when(vacancyRepository).deleteById(anyLong());
        doNothing().when(vacancyValidator).validateTutorRole(anyLong(), anyLong());
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(vacancy));

        Assertions.assertDoesNotThrow(() -> vacancyService.deleteVacancy(1L, 1L));
        verify(vacancyRepository, times(1)).deleteById(anyLong());
        verify(candidateService, times(1)).deleteCandidatesByVacancyId(anyLong());
    }

    @Test
    void deleteVacancyNotFound() {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.deleteVacancy(1L, 1L),
                "vacancy 1 not found");
    }

    @Test
    void deleteVacancyIdIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.deleteVacancy(null, 1L),
                "vacancyId or tutorId is null");
    }

    @Test
    void deleteVacancyTutorIdIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.deleteVacancy(1L, null),
                "vacancyId or tutorId is null");
    }

    @Test
    void getVacancy() {
        Vacancy vacancy = Vacancy.builder()
                .id(1L)
                .build();
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
        Assertions.assertEquals(1, vacancyService.getVacancy(1L).getId());
        verify(vacancyRepository, times(1)).findById(1L);
    }

    @Test
    void getVacancyIdIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.getVacancy(null),
                "vacancyId is null");
    }

    @Test
    void getVacancyNotFound() {
        when(vacancyRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.getVacancy(1L),
                "vacancy 1 not found");
    }

    @Test
    void getVacancies() {
        VacancyFilter nameFilter = Mockito.mock(VacancyFilter.class);
        VacancyFilter positionFilter = Mockito.mock(VacancyFilter.class);
        vacancyFilters.add(positionFilter);
        vacancyFilters.add(nameFilter);

        VacancyFilterDto vacancyFilterDto = VacancyFilterDto.builder()
                .name("test")
                .position(TeamRole.DEVELOPER)
                .build();

        List<Vacancy> vacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("test")
                        .position(TeamRole.DEVELOPER)
                        .build(),
                Vacancy.builder()
                        .id(2L)
                        .name("vacancy")
                        .position(TeamRole.DEVELOPER)
                        .build(),
                Vacancy.builder()
                        .id(3L)
                        .name("vacancy-test_2")
                        .position(TeamRole.INTERN)
                        .build(),
                Vacancy.builder()
                        .id(5L)
                        .build()
        );

        List<Vacancy> vacanciesAfterPositionFilter = List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("test")
                        .position(TeamRole.DEVELOPER)
                        .build(),
                Vacancy.builder()
                        .id(2L)
                        .name("vacancy")
                        .position(TeamRole.DEVELOPER)
                        .build()
        );

        List<Vacancy> vacanciesAfterNameFilter = List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("test")
                        .position(TeamRole.DEVELOPER)
                        .build()
        );

        when(vacancyRepository.findAll()).thenReturn(vacancies);
        when(nameFilter.isApplicable(vacancyFilterDto)).thenReturn(true);
        when(positionFilter.isApplicable(vacancyFilterDto)).thenReturn(true);
        when(nameFilter.apply(any(), any())).thenReturn(vacanciesAfterNameFilter.stream());
        when(positionFilter.apply(any(), any())).thenReturn(vacanciesAfterPositionFilter.stream());

        List<Vacancy> actual = vacancyService.getVacancies(vacancyFilterDto);
        List<Vacancy> expectedVacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("test")
                        .position(TeamRole.DEVELOPER)
                        .build()
        );

        Assertions.assertEquals(expectedVacancies, actual);
        verify(vacancyRepository, times(1)).findAll();
        verify(nameFilter, times(1)).isApplicable(vacancyFilterDto);
        verify(positionFilter, times(1)).isApplicable(vacancyFilterDto);
        verify(nameFilter, times(1)).apply(any(), any());
        verify(positionFilter, times(1)).apply(any(), any());
    }

    @Test
    void getVacanciesIsFilterNull() {
        List<Vacancy> vacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("test")
                        .position(TeamRole.DEVELOPER)
                        .build(),
                Vacancy.builder()
                        .id(2L)
                        .name("vacancy")
                        .position(TeamRole.DEVELOPER)
                        .build(),
                Vacancy.builder()
                        .id(3L)
                        .name("vacancy-test_2")
                        .position(TeamRole.INTERN)
                        .build(),
                Vacancy.builder()
                        .id(5L)
                        .build()
        );

        when(vacancyRepository.findAll()).thenReturn(vacancies);
        List<Vacancy> actual = vacancyService.getVacancies(null);
        Assertions.assertEquals(vacancies, actual);
        verify(vacancyRepository, times(1)).findAll();
    }

    @Test
    void addCandidates() {
        List<Candidate> candidates = IntStream.rangeClosed(11, 15).boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                }).toList();

        Vacancy vacancy = Vacancy.builder()
                .id(1L)
                .name("test")
                .position(TeamRole.DEVELOPER)
                .candidates(List.of())
                .project(Project.builder()
                        .id(3L)
                        .build())
                .build();

        Vacancy savedVacancy = Vacancy.builder()
                .id(1L)
                .name("test")
                .position(TeamRole.DEVELOPER)
                .project(Project.builder()
                        .id(3L)
                        .build())
                .candidates(candidates)
                .updatedAt(LocalDateTime.now())
                .updatedBy(4L)
                .build();

        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
        doNothing().when(vacancyValidator).validateTutorRole(4L, 3L);
        doNothing().when(vacancyValidator).validateCandidates(any(Vacancy.class), any());
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(savedVacancy);

        Assertions.assertEquals(savedVacancy, vacancyService.addCandidates(candidates, 1L, 4L));
        verify(vacancyRepository, times(1)).findById(1L);
        verify(vacancyRepository, times(1)).save(any(Vacancy.class));
        verify(vacancyValidator, times(1)).validateCandidates(any(Vacancy.class), anyList());
        verify(vacancyValidator, times(1)).validateTutorRole(4L, 3L);
    }

    @Test
    void addCandidatesWithNullCandidates() {
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.addCandidates(null,
                1L, 4L), "candidates, tutorId or vacancyId is null");
    }

    @Test
    void addCandidatesWithEmptyCandidates() {
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.addCandidates(List.of(),
                1L, 4L), "candidates, tutorId or vacancyId is null");
    }

    @Test
    void addCandidatesWithNullVacancyId() {
        List<Candidate> candidates = IntStream.rangeClosed(11, 15).boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                }).toList();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.addCandidates(candidates,
                null, 4L), "candidates, tutorId or vacancyId is null");
    }

    @Test
    void addCandidatesWithNullTutorId() {
        List<Candidate> candidates = IntStream.rangeClosed(11, 15).boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                }).toList();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.addCandidates(candidates,
                1L, null), "candidates, tutorId or vacancyId is null");
    }

    @Test
    void addCandidatesWithNotFoundVacancy() {
        List<Candidate> candidates = IntStream.rangeClosed(11, 15).boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                }).toList();

        when(vacancyRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.addCandidates(candidates,
                1L, 4L), "vacancy 1 not found");
    }
}