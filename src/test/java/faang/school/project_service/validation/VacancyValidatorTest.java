package faang.school.project_service.validation;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.exception.VacancyValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validation.vacancy.VacancyValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class VacancyValidatorTest {
    private static final long DEFAULT_CONTEXT_USER_ID = 1L;
    private static final long DEFAULT_PROJECT_ID = 2L;
    private static final long DEFAULT_TEAM_MEMBER_ID = 1L;
    @InjectMocks
    private VacancyValidatorImpl vacancyValidator;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    private static final long contextUserId = DEFAULT_CONTEXT_USER_ID;
    private static final long projectId = DEFAULT_PROJECT_ID;
    private final TeamMember teamMemberOwner = TeamMember.builder()
            .id(DEFAULT_TEAM_MEMBER_ID)
            .roles(List.of(TeamRole.OWNER))
            .build();

    private final TeamMember teamMemberIntern = TeamMember.builder()
            .id(DEFAULT_TEAM_MEMBER_ID)
            .roles(List.of(TeamRole.INTERN))
            .build();
    private final Vacancy newVacancy = new Vacancy();

    private final Project project = Project.builder()
            .id(2L)
            .build();

    private Vacancy existingVacancyWithoutCandidates;
    private Vacancy existingVacancyWithCandidates;
    private Vacancy existingVacancyWithEnoughCandidates;
    private UpdateVacancyDto openVacancyDto;
    private UpdateVacancyDto closedVacancyDto;
    private Candidate candidate;

    @BeforeEach
    void prepareData() {
        existingVacancyWithoutCandidates = Vacancy.builder()
                .id(4L)
                .count(5)
                .name("VacancyWithoutCandidates")
                .description("VacancyWithoutCandidates")
                .position(TeamRole.DEVELOPER)
                .project(project)
                .status(VacancyStatus.OPEN)
                .workSchedule(WorkSchedule.OTHER)
                .candidates(new ArrayList<>())
                .build();

        candidate = new Candidate();
        candidate.setUserId(100L);
        existingVacancyWithCandidates = Vacancy.builder()
                .id(7L)
                .count(5)
                .candidates(List.of(candidate))
                .build();

        existingVacancyWithEnoughCandidates = Vacancy.builder()
                .id(7L)
                .count(1)
                .project(project)
                .position(TeamRole.DEVELOPER)
                .candidates(List.of(candidate))
                .build();

        openVacancyDto = UpdateVacancyDto.builder()
                .name("VacancyForUnitTest")
                .vacancyStatus("OPEN")
                .count(5)
                .description("VacancyForUnitTest")
                .position("TESTER")
                .workSchedule("ROTATING")
                .projectId(projectId).
                build();
        closedVacancyDto = UpdateVacancyDto.builder()
                .name("ClosedVacancyForUnitTest")
                .vacancyStatus("CLOSED")
                .count(1)
                .description("ClosedVacancyForUnitTest")
                .position("ANALYST")
                .workSchedule("ON_CALL")
                .projectId(projectId).
                build();
    }

    @Test
    void testSuccessfullyCreateValidation() {
        when(teamMemberRepository.findByUserIdAndProjectId(contextUserId, projectId)).thenReturn(teamMemberOwner);
        assertDoesNotThrow(() -> vacancyValidator.validateCreate(contextUserId, projectId));
    }

    @Test
    void testCreateValidationWhenUserNotTeamMember() {
        when(teamMemberRepository.findByUserIdAndProjectId(contextUserId, projectId)).thenReturn(null);
        assertThrows(VacancyValidationException.class,
                () -> vacancyValidator.validateCreate(contextUserId, projectId));
    }

    @Test
    void testCreateValidationWhenUserHasNotAllowedRole() {
        when(teamMemberRepository.findByUserIdAndProjectId(contextUserId, projectId)).thenReturn(teamMemberIntern);
        assertThrows(VacancyValidationException.class,
                () -> vacancyValidator.validateCreate(contextUserId, projectId));
    }

    @Test
    void testUpdateValidationWhenUserNotTeamMember() {
        when(teamMemberRepository.findByUserIdAndProjectId(contextUserId, projectId))
                .thenReturn(null);
        assertThrows(VacancyValidationException.class,
                () -> vacancyValidator.validateUpdate(contextUserId,
                        newVacancy,
                        openVacancyDto));
    }

    @Test
    void testUpdateValidationWhenUserHasNotAllowedRole() {
        when(teamMemberRepository.findByUserIdAndProjectId(contextUserId, projectId))
                .thenReturn(teamMemberIntern);
        assertThrows(VacancyValidationException.class,
                () -> vacancyValidator.validateUpdate(contextUserId,
                        newVacancy,
                        openVacancyDto));
    }

    @Test
    void testUpdateValidationWhenVacancyStatusNotClosed() {
        when(teamMemberRepository.findByUserIdAndProjectId(contextUserId, projectId))
                .thenReturn(teamMemberOwner);
        assertDoesNotThrow(() ->
                vacancyValidator.validateUpdate(contextUserId,
                        newVacancy,
                        openVacancyDto));
    }

    @Test
    void testUpdateValidationWhenVacancyCloseWithoutCandidates() {
        when(teamMemberRepository.findByUserIdAndProjectId(contextUserId, projectId))
                .thenReturn(teamMemberOwner);
        assertThrows(VacancyValidationException.class,
                () -> vacancyValidator.validateUpdate(contextUserId,
                        existingVacancyWithoutCandidates,
                        closedVacancyDto));
    }

    @Test
    void testUpdateValidationWhenNotEnoughCandidates() {
        when(teamMemberRepository.findByUserIdAndProjectId(contextUserId, projectId))
                .thenReturn(teamMemberOwner);
        assertThrows(VacancyValidationException.class,
                () -> vacancyValidator.validateUpdate(contextUserId,
                        existingVacancyWithCandidates,
                        closedVacancyDto));
    }

    @Test
    void testUpdateValidationWhenNotAllCandidatesAssigned() {
        when(teamMemberRepository.findByUserIdAndProjectId(contextUserId, projectId))
                .thenReturn(teamMemberOwner);
        when(teamMemberRepository.findByUserIdAndProjectId(candidate.getUserId(), projectId))
                .thenReturn(TeamMember.builder()
                        .id(115L)
                        .roles(List.of(TeamRole.OWNER))
                        .build());
        assertThrows(VacancyValidationException.class,
                () -> vacancyValidator.validateUpdate(contextUserId,
                        existingVacancyWithEnoughCandidates,
                        closedVacancyDto));
    }
}
