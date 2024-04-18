package faang.school.projectservice.service;

import faang.school.projectservice.dto.Vacancy.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.validator.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @Mock
    private  VacancyRepository vacancyRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private VacancyMapper vacancyMapper;
    @Mock
    private VacancyValidator vacancyValidator;
    @InjectMocks
    VacancyService vacancyService;

    private long projectId;
    private long vacancyId;
    private long curatorId;
    private Project project;

    @BeforeEach
    public void initialize() {
        projectId = 100L;
        List<Team> teams = List.of(
                Team.builder()
                        .teamMembers(List.of(TeamMember.builder().roles(List.of(TeamRole.ANALYST)).build()))
                        .build(),
                Team.builder()
                        .teamMembers(List.of(
                                TeamMember.builder().roles(List.of(TeamRole.DESIGNER)).build(),
                                TeamMember.builder().roles(List.of(TeamRole.ANALYST)).build()))
                        .build());
        project = Project.builder()
                .id(projectId)
                .status(ProjectStatus.IN_PROGRESS)
                .teams(teams)
                .build();
        vacancyId = 200L;
        curatorId = 300L;
    }

    @Test
    public void createVacancyTest() {
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        vacancyService.createVacancy(projectId, new VacancyDto());
        ArgumentCaptor<Vacancy> argumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        verify(vacancyRepository, times(1)).save(argumentCaptor.capture());
    }

    @Test
    public void getVacancyByIdThrowsExceptionTest() {
        Assert.assertThrows(EntityNotFoundException.class,
                () -> vacancyService.getVacancyById(projectId));
    }

    @Test
    public void getVacancyByIdTest() {
        Mockito.when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(new Vacancy()));
        vacancyService.getVacancyById(vacancyId);
        verify(vacancyRepository, times(1)).findById(vacancyId);
    }

    @Test
    public void deleteVacancyTest() {
        Mockito.when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(Vacancy.builder()
                .candidates(List.of(Candidate.builder()
                                .userId(curatorId)
                                .candidateStatus(CandidateStatus.REJECTED)
                                .build()))
                .build()));
        vacancyService.deleteVacancy(vacancyId);
        verify(teamMemberRepository, times(1)).deleteById(curatorId);

    }

    @Test
    public void updateVacancyTest() {
        Mockito.when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(Vacancy.builder()
                .position(TeamRole.ANALYST)
                .createdBy(curatorId)
                .count(2)
                .project(project)
                .build()));
        ArgumentCaptor<Vacancy> argumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        vacancyService.updateVacancy(vacancyId, new VacancyDto());
        verify(vacancyRepository, times(1)).save(argumentCaptor.capture());
        Vacancy vacancy = argumentCaptor.getValue();
        assertEquals(vacancy.getStatus(), VacancyStatus.CLOSED);
    }

}
