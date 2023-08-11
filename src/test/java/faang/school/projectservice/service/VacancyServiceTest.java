package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.Vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.Vacancy.UpdateCandidateRequestDto;
import faang.school.projectservice.dto.Vacancy.UpdateVacancyDto;
import faang.school.projectservice.exception.DataValidException;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        vacancyService.create(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(any());
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
    void TestChangeCandidateStatus_CandidateIsNotFoundInVacancy() {
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
    void TestChangeCandidateStatus_teamMemberSave_closeVacancy() {
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
    void TestChangeCandidateStatus_targetTeamNotFoundInProject() {
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
}