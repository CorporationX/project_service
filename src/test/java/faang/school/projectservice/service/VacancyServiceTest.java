package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.Vacancy.*;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.filter.vacancy.VacancyDescriptionFilter;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.filter.vacancy.VacancyNameFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @Spy
    private VacancyMapper vacancyMapper = VacancyMapper.INSTANCE;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private VacancyService vacancyService;

    @Test
    void testCreate_CreatorIsNotProjectOwner() {
        Project vacancyProject = Project
                .builder()
                .id(1L)
                .ownerId(2L)
                .build();
        CreateVacancyDto vacancyDto = CreateVacancyDto
                .builder()
                .id(5L)
                .projectId(1L)
                .build();

        when(projectRepository.existsById(anyLong())).thenReturn(true);
        when(projectRepository.getProjectById(anyLong())).thenReturn(vacancyProject);
        when(userContext.getUserId()).thenReturn(3L);

        Assertions.assertThrows(DataValidException.class, () -> vacancyService.create(vacancyDto));
        Mockito.verify(vacancyRepository, Mockito.times(0)).save(any());
    }

    @Test
    void testCreate_saveEntityFromValidDto() {
        Project vacancyProject = Project
                .builder()
                .id(1L)
                .ownerId(2L)
                .build();
        CreateVacancyDto vacancyDto = CreateVacancyDto
                .builder()
                .id(5L)
                .projectId(1L)
                .build();

        when(projectRepository.existsById(anyLong())).thenReturn(true);
        when(projectRepository.getProjectById(anyLong())).thenReturn(vacancyProject);
        when(userContext.getUserId()).thenReturn(2L);

        Vacancy expected = Vacancy
                .builder()
                .id(5L)
                .createdBy(2L)
                .status(VacancyStatus.OPEN)
                .candidates(new ArrayList<>())
                .build();

        vacancyService.create(vacancyDto);

        Mockito.verify(vacancyRepository, Mockito.times(1)).save(expected);
    }

    @Test
    void testUpdate_NotAvailableClosedStatus() {
        UpdateVacancyDto vacancyDto = UpdateVacancyDto
                .builder()
                .id(1L)
                .candidateIds(new ArrayList<>(List.of(1L, 2L)))
                .count(3)
                .status(VacancyStatus.CLOSED)
                .build();

        when(candidateRepository.countAllByVacancyIdAndStatusAndId(anyLong(), any(CandidateStatus.class), anyList()))
                .thenReturn(2);

        Assertions.assertThrows(DataValidException.class, () -> vacancyService.update(vacancyDto));
        Mockito.verify(vacancyRepository, Mockito.times(0)).save(any());
    }

    @Test
    void testUpdate_ClosedStatusCanBeSet() {
        Candidate acceptedCandidate = Candidate
                .builder()
                .id(1L)
                .candidateStatus(CandidateStatus.ACCEPTED)
                .build();
        Candidate waitingResponseCandidate = Candidate
                .builder()
                .id(2L)
                .candidateStatus(CandidateStatus.WAITING_RESPONSE)
                .build();
        UpdateVacancyDto vacancyDto = UpdateVacancyDto
                .builder()
                .id(1L)
                .candidateIds(new ArrayList<>(List.of(1L, 2L)))
                .count(2)
                .status(VacancyStatus.CLOSED)
                .build();
        Vacancy vacancy = Vacancy
                .builder()
                .id(1L)
                .candidates(new ArrayList<>(List.of(acceptedCandidate, acceptedCandidate, waitingResponseCandidate)))
                .count(2)
                .status(VacancyStatus.OPEN)
                .build();

        when(candidateRepository.countAllByVacancyIdAndStatusAndId(
                anyLong(), any(CandidateStatus.class), anyList()
                )
        ).thenReturn(2);
        when(vacancyRepository.findById(any())).thenReturn(Optional.of(vacancy));
        when(userContext.getUserId()).thenReturn(3L);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);

        vacancyService.update(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(any());
    }

    @Test
    void testChangeCandidateStatus_CandidateIsNotFoundInVacancy() {
        Candidate inVacancy = Candidate
                .builder()
                .id(1L)
                .build();
        Candidate inVacancyToo = Candidate
                .builder()
                .id(2L)
                .build();
        Candidate notInVacancy = Candidate
                .builder()
                .id(3L)
                .build();

        UpdateCandidateRequestDto updateCandidate = UpdateCandidateRequestDto
                .builder()
                .candidateId(3L)
                .vacancyId(1L)
                .candidateStatus(CandidateStatus.ACCEPTED)
                .build();

        Vacancy vacancy = Vacancy
                .builder()
                .id(1L)
                .candidates(new ArrayList<>(List.of(inVacancy, inVacancyToo)))
                .count(2)
                .status(VacancyStatus.OPEN)
                .build();

        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(vacancy));
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.of(notInVacancy));

        Assertions.assertThrows(DataValidException.class, () -> vacancyService.changeCandidateStatus(updateCandidate));
    }

    @Test
    void testChangeCandidateStatus_closeVacancy() {
        Project project = Project
                .builder()
                .id(3L)
                .build();

        Team actualTeam = Team
                .builder()
                .id(1L)
                .project(project)
                .build();
        Team anotherTeam = Team
                .builder()
                .id(2L)
                .project(project)
                .build();

        project.setTeams(List.of(actualTeam, anotherTeam));
        Vacancy vacancy = Vacancy
                .builder()
                .id(1L)
                .count(2)
                .status(VacancyStatus.OPEN)
                .project(project)
                .build();

        project.setVacancies(List.of(vacancy));
        Candidate inVacancy = Candidate
                .builder()
                .id(1L)
                .candidateStatus(CandidateStatus.ACCEPTED)
                .build();
        Candidate inVacancyToo = Candidate
                .builder()
                .id(2L)
                .userId(3L)
                .candidateStatus(CandidateStatus.WAITING_RESPONSE)
                .build();

        vacancy.setCandidates(List.of(inVacancy, inVacancyToo));
        UpdateCandidateRequestDto updateCandidate = UpdateCandidateRequestDto
                .builder()
                .candidateId(2L)
                .vacancyId(1L)
                .teamId(actualTeam.getId())
                .role(TeamRole.DEVELOPER)
                .candidateStatus(CandidateStatus.ACCEPTED)
                .build();

        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(vacancy));
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.of(inVacancyToo));
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(actualTeam));
        when(candidateRepository.countAllByVacancyIdAndStatusAndId(
                anyLong(), any(CandidateStatus.class), anyList()
                )
        ).thenReturn(2);

        vacancyService.changeCandidateStatus(updateCandidate);
        verify(teamMemberJpaRepository).save(any());
        verify(vacancyRepository).save(any());
        Assert.assertEquals(VacancyStatus.CLOSED, vacancy.getStatus());
    }

    @Test
    void testChangeCandidateStatus_targetTeamNotFoundInProject() {
        Project project = Project
                .builder()
                .id(3L)
                .build();
        Team actualTeam = Team
                .builder()
                .id(1L)
                .project(project)
                .build();
        Team anotherTeam = Team
                .builder()
                .id(2L)
                .project(project)
                .build();

        project.setTeams(List.of(anotherTeam));
        Vacancy vacancy = Vacancy
                .builder()
                .id(1L)
                .count(2)
                .status(VacancyStatus.OPEN)
                .project(project)
                .build();

        project.setVacancies(List.of(vacancy));
        Candidate inVacancy = Candidate
                .builder()
                .id(1L)
                .candidateStatus(CandidateStatus.ACCEPTED)
                .build();
        Candidate inVacancyToo = Candidate
                .builder()
                .id(2L)
                .userId(3L)
                .candidateStatus(CandidateStatus.WAITING_RESPONSE)
                .build();

        vacancy.setCandidates(List.of(inVacancy, inVacancyToo));
        UpdateCandidateRequestDto updateCandidate = UpdateCandidateRequestDto
                .builder()
                .candidateId(2L)
                .vacancyId(1L)
                .teamId(actualTeam.getId())
                .role(TeamRole.DEVELOPER)
                .candidateStatus(CandidateStatus.ACCEPTED)
                .build();

        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(vacancy));
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.of(inVacancyToo));
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(actualTeam));

        Assertions.assertThrows(DataValidException.class, () -> vacancyService.changeCandidateStatus(updateCandidate));
    }

    static Stream<VacancyFilter> argsProvider1() {
        return Stream.of(
                new VacancyNameFilter(),
                new VacancyDescriptionFilter()
        );
    }

    static Stream<Arguments> argsProvider2() {
        return Stream.of(
                Arguments.of(new VacancyNameFilter(), VacancyFilterDto.builder().namePattern("d").build()),
                Arguments.of(new VacancyDescriptionFilter(), VacancyFilterDto.builder().descriptionPattern("google").build())
        );
    }

    @ParameterizedTest
    @MethodSource("argsProvider1")
    public void testIsApplicable(VacancyFilter userFilter) {
        VacancyFilterDto filter = new VacancyFilterDto("f", "apple");
        VacancyFilterDto filter2 = new VacancyFilterDto();

        boolean result1 = userFilter.isApplicable(filter);
        boolean result2 = userFilter.isApplicable(filter2);

        assertTrue(result1);
        assertFalse(result2);
    }

    @ParameterizedTest
    @MethodSource("argsProvider2")
    public void testApply(VacancyFilter vacancyFilter, VacancyFilterDto filter) {
        Vacancy vacancy1 = Vacancy.builder().name("developer").description("google").build();
        Vacancy vacancy2 = Vacancy.builder().name("no").description("any").build();

        List<Vacancy> result = vacancyFilter.apply(Stream.of(vacancy1, vacancy2), filter).collect(Collectors.toList());

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(vacancy1, result.get(0))
        );
    }

    @Test
    public void testFindAll() {
        List<Candidate> candidates = new ArrayList<>();

        Vacancy vacancy1 = Vacancy
                .builder()
                .id(1L)
                .candidates(candidates)
                .build();
        Vacancy vacancy2 = Vacancy
                .builder()
                .id(2L)
                .candidates(candidates)
                .build();
        List<Vacancy> vacancies = Arrays.asList(vacancy1, vacancy2);

        when(vacancyRepository.findAll()).thenReturn(vacancies);

        List<ExtendedVacancyDto> results = vacancyService.findAll();

        assertEquals(2, results.size());
    }

    @Test
    public void testFindById() {
        List<Candidate> candidates = new ArrayList<>();

        Vacancy vacancy = Vacancy
                .builder()
                .id(1L)
                .candidates(candidates)
                .build();
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

        ExtendedVacancyDto result = vacancyService.findById(1L);

        assertNotNull(result);
    }

    @Test
    public void testDelete() {
        Long vacancyId = 1L;
        doNothing().when(vacancyRepository).deleteById(vacancyId);
        vacancyService.delete(vacancyId);
        verify(vacancyRepository, times(1)).deleteById(vacancyId);
    }
}