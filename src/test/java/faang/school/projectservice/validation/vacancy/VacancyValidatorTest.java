package faang.school.projectservice.validation.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyValidatorTest {

    @Mock
    ProjectRepository projectRepository;
    @Mock
    VacancyRepository vacancyRepository;
    @Mock
    TeamMemberRepository teamMemberRepository;
    @InjectMocks
    VacancyValidator vacancyValidator;

    private Vacancy vacancy;
    private VacancyDto vacancyDto;
    private Project project;
    private Team team;
    private TeamMember curator;
    private TeamMember candidateTeamMember;
    private Candidate candidate;
    private TeamRole curatorRole;

    @BeforeEach
    void setUp() {
        curatorRole = TeamRole.MANAGER;
        candidateTeamMember = TeamMember.builder()
                .id(1L)
                .userId(10L)
                .build();
        candidate = Candidate.builder()
                .id(candidateTeamMember.getId())
                .userId(candidateTeamMember.getUserId())
                .candidateStatus(CandidateStatus.WAITING_RESPONSE)
                .build();
        curator = TeamMember.builder()
                .id(2L)
                .userId(11L)
                .roles(List.of(curatorRole))
                .build();
        team = Team.builder()
                .id(3L)
                .teamMembers(List.of(curator, candidateTeamMember))
                .build();
        project = Project.builder()
                .id(4L)
                .teams(List.of(team))
                .build();
        team.setProject(project);
        vacancy = Vacancy.builder()
                .id(5L)
                .createdBy(curator.getUserId())
                .name("Valid name")
                .description("Valid desc")
                .candidates(List.of(candidate))
                .project(project)
                .count(1)
                .build();
        vacancyDto = VacancyDto.builder()
                .id(vacancy.getId())
                .curatorId(vacancy.getCreatedBy())
                .name(vacancy.getName())
                .description(vacancy.getDescription())
                .candidatesIds(List.of(candidate.getUserId()))
                .projectId(project.getId())
                .workersRequired(vacancy.getCount())
                .build();
    }

    @Test
    void validateVacancyFields_FieldsAreValid_ShouldNotThrow() {
        assertDoesNotThrow(() ->
                vacancyValidator.validateVacancyFields(vacancyDto));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    void validateVacancyFields_TitleIsInvalid_ShouldThrowDataValidationException(String name) {
        vacancyDto.setName(name);
        assertThrows(DataValidationException.class, () ->
                vacancyValidator.validateVacancyFields(vacancyDto));
    }

    @Test
    void validateVacancyFields_VacancyGotNoCurator_ShouldThrowDataValidationException() {
        vacancyDto.setCuratorId(null);
        assertThrows(DataValidationException.class, () ->
                vacancyValidator.validateVacancyFields(vacancyDto));
    }

    @Test
    void validateVacancyFields_VacancyGotNoProject_ShouldThrowDataValidationException() {
        vacancyDto.setProjectId(null);
        assertThrows(DataValidationException.class, () ->
                vacancyValidator.validateVacancyFields(vacancyDto));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    void validateVacancyFields_VacancyGotNoCurator_ShouldThrowDataValidationException(String description) {
        vacancyDto.setDescription(description);
        assertThrows(DataValidationException.class, () ->
                vacancyValidator.validateVacancyFields(vacancyDto));
    }

    @Test
    void validateIfVacancyCanBeClosed_TeamMemberAccepted_ShouldNotThrow() {
        candidateTeamMember.setRoles(List.of(TeamRole.DEVELOPER));
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);

        vacancyValidator.validateIfVacancyCanBeClosed(vacancyDto);

        assertAll(
                () -> verify(projectRepository, times(1)).getProjectById(project.getId()),
                () -> assertDoesNotThrow(() ->
                        vacancyValidator.validateIfVacancyCanBeClosed(vacancyDto))
        );
    }

    @Test
    void validateIfVacancyCanBeClosed_TeamMemberNotYetAccepted_ShouldThrowDataValidationException() {
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);

        assertThrows(DataValidationException.class, () ->
                vacancyValidator.validateIfVacancyCanBeClosed(vacancyDto));
    }

    @Test
    void validateIfCandidatesNoMoreNeeded_VacancyFulfilled_ShouldNotThrow() {
        candidate.setCandidateStatus(CandidateStatus.ACCEPTED);
        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.ofNullable(vacancy));

        vacancyValidator.validateIfCandidatesNoMoreNeeded(vacancyDto);

        assertAll(
                () -> verify(vacancyRepository, times(1)).findById(vacancyDto.getId()),
                () -> assertDoesNotThrow(() ->
                        vacancyValidator.validateIfCandidatesNoMoreNeeded(vacancyDto))
        );
    }

    @Test
    void validateIfCandidatesNoMoreNeeded_VacancyIsNotFulfilled_ShouldThrowDataValidationException() {
        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.ofNullable(vacancy));

        assertThrows(DataValidationException.class, () ->
                vacancyValidator.validateIfCandidatesNoMoreNeeded(vacancyDto));
    }

    @Test
    void validateCuratorRole_CuratorHasValidRole_ShouldNotThrow() {
        when(teamMemberRepository.findById(anyLong())).thenReturn(curator);

        vacancyValidator.validateCuratorRole(curator.getUserId());

        assertAll(
                () -> verify(teamMemberRepository, times(1)).findById(curator.getUserId()),
                () -> assertDoesNotThrow(() ->
                        vacancyValidator.validateCuratorRole(curator.getUserId()))
        );
    }

    @Test
    void validateCuratorRole_CuratorHasInvalidRole_ShouldThrowDataValidationException() {
        curator.setRoles(List.of(TeamRole.ANALYST));
        when(teamMemberRepository.findById(anyLong())).thenReturn(curator);

        assertThrows(DataValidationException.class, () ->
                vacancyValidator.validateCuratorRole(curator.getUserId()));
    }
}
