package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.InvalidOperationException;
import faang.school.projectservice.exception.PermissionDeniedException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyValidatorTest {
    private static final Long USER_ID = 1L;
    private static final Long PROJECT_ID = 1L;
    private static final Long VACANCY_ID = 1L;
    private static final Long CANDIDATE_ID = 101L;
    private static final TeamRole VALID_ROLE = TeamRole.DEVELOPER;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private VacancyValidator validator;

    private Vacancy vacancy;
    private TeamMember teamMember;
    private Team team;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        team = Team.builder()
                .id(1L)
                .project(Project.builder().id(PROJECT_ID).build())
                .build();

        teamMember = TeamMember.builder()
                .id(1L)
                .userId(USER_ID)
                .nickname("test_user")
                .roles(List.of(TeamRole.OWNER))
                .team(team)
                .build();

        vacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .project(Project.builder().id(PROJECT_ID).build())
                .count(1)
                .status(VacancyStatus.OPEN)
                .candidates(new ArrayList<>())
                .build();

        candidate = Candidate.builder()
                .id(CANDIDATE_ID)
                .userId(USER_ID)
                .candidateStatus(CandidateStatus.ACCEPTED)
                .build();

        vacancy.getCandidates().add(candidate);
    }

    @Nested
    @DisplayName("Проверки существования проекта")
    class ProjectValidationTests {
        @Test
        @DisplayName("Успешная проверка существующего проекта")
        void validateProjectExists_WhenProjectExists_ShouldNotThrowException() {
            when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
            assertDoesNotThrow(() -> validator.validateProjectExists(PROJECT_ID));
        }

        @Test
        @DisplayName("Ошибка при проверке несуществующего проекта")
        void validateProjectExists_WhenProjectNotExists_ShouldThrowEntityNotFoundException() {
            when(projectRepository.existsById(PROJECT_ID)).thenReturn(false);
            assertThrows(EntityNotFoundException.class,
                    () -> validator.validateProjectExists(PROJECT_ID));
        }
    }

    @Nested
    @DisplayName("Проверки роли вакансии")
    class TeamRoleValidationTests {
        @Test
        @DisplayName("Успешная проверка валидной роли")
        void validateTeamRole_WhenRoleIsValid_ShouldNotThrowException() {
            assertDoesNotThrow(() -> validator.validateTeamRole(VALID_ROLE));
        }

        @Test
        @DisplayName("Ошибка при отсутствии роли")
        void validateTeamRole_WhenRoleIsNull_ShouldThrowIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> validator.validateTeamRole(null));
        }
    }

    @Nested
    @DisplayName("Проверки прав на создание вакансии")
    class CreateRightsValidationTests {
        @Test
        @DisplayName("Успешная проверка для OWNER")
        void validateUserHasCreateRights_WhenUserIsOwner_ShouldNotThrowException() {
            when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID))
                    .thenReturn(Optional.of(teamMember));
            assertDoesNotThrow(() -> validator.validateUserHasCreateRights(USER_ID, PROJECT_ID));
        }

        @Test
        @DisplayName("Успешная проверка для MANAGER")
        void validateUserHasCreateRights_WhenUserIsManager_ShouldNotThrowException() {
            teamMember.setRoles(List.of(TeamRole.MANAGER));
            when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID))
                    .thenReturn(Optional.of(teamMember));
            assertDoesNotThrow(() -> validator.validateUserHasCreateRights(USER_ID, PROJECT_ID));
        }

        @Test
        @DisplayName("Ошибка при недостаточных правах")
        void validateUserHasCreateRights_WhenUserHasNoRights_ShouldThrowPermissionDeniedException() {
            teamMember.setRoles(List.of(TeamRole.DEVELOPER));
            when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID))
                    .thenReturn(Optional.of(teamMember));
            assertThrows(PermissionDeniedException.class,
                    () -> validator.validateUserHasCreateRights(USER_ID, PROJECT_ID));
        }

        @Test
        @DisplayName("Ошибка при отсутствии пользователя в команде")
        void validateUserHasCreateRights_WhenUserNotTeamMember_ShouldThrowPermissionDeniedException() {
            when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID))
                    .thenReturn(Optional.empty());
            assertThrows(PermissionDeniedException.class,
                    () -> validator.validateUserHasCreateRights(USER_ID, PROJECT_ID));
        }
    }

    @Nested
    @DisplayName("Проверки прав на обновление вакансии")
    class UpdateRightsValidationTests {
        @Test
        @DisplayName("Успешная проверка для OWNER")
        void checkRoleUpdatingUser_WhenUserIsOwner_ShouldReturnTrue() {
            when(teamMemberRepository.findByUserId(USER_ID))
                    .thenReturn(List.of(teamMember));
            assertTrue(validator.checkRoleUpdatingUser(USER_ID));
        }

        @Test
        @DisplayName("Ошибка при отсутствии пользователя")
        void checkRoleUpdatingUser_WhenUserNotFound_ShouldThrowDataValidationException() {
            when(teamMemberRepository.findByUserId(USER_ID))
                    .thenReturn(List.of());
            assertThrows(DataValidationException.class,
                    () -> validator.checkRoleUpdatingUser(USER_ID));
        }
    }

    @Nested
    @DisplayName("Проверки закрытия вакансии")
    class CloseVacancyValidationTests {
        @Test
        @DisplayName("Успешная проверка закрытия вакансии")
        void validateCandidateSelectionForClosing_WhenValid_ShouldNotThrowException() {
            when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));
            assertDoesNotThrow(() ->
                    validator.validateCandidateSelectionForClosing(vacancy, List.of(CANDIDATE_ID)));
        }

        @Test
        @DisplayName("Ошибка при закрытии уже закрытой вакансии")
        void validateCandidateSelectionForClosing_WhenVacancyNotOpen_ShouldThrowInvalidOperationException() {
            vacancy.setStatus(VacancyStatus.CLOSED);
            assertThrows(InvalidOperationException.class,
                    () -> validator.validateCandidateSelectionForClosing(vacancy, List.of(CANDIDATE_ID)));
        }

        @Test
        @DisplayName("Ошибка при неверном количестве кандидатов")
        void validateCandidateSelectionForClosing_WhenWrongCandidateCount_ShouldThrowDataValidationException() {
            assertThrows(DataValidationException.class,
                    () -> validator.validateCandidateSelectionForClosing(vacancy, List.of()));
        }

        @Test
        @DisplayName("Ошибка при невалидном ID кандидата")
        void validateCandidateSelectionForClosing_WhenInvalidCandidateId_ShouldThrowDataValidationException() {
            assertThrows(DataValidationException.class,
                    () -> validator.validateCandidateSelectionForClosing(vacancy, List.of(999L)));
        }

        @Test
        @DisplayName("Ошибка при неподтвержденном кандидате")
        void validateCandidateSelectionForClosing_WhenCandidateNotAccepted_ShouldThrowDataValidationException() {
            candidate.setCandidateStatus(CandidateStatus.REJECTED);
            when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));
            assertThrows(DataValidationException.class,
                    () -> validator.validateCandidateSelectionForClosing(vacancy, List.of(CANDIDATE_ID)));
        }

        @Test
        @DisplayName("Ошибка при отсутствии кандидата")
        void validateCandidateSelectionForClosing_WhenCandidateNotFound_ShouldThrowEntityNotFoundException() {
            when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class,
                    () -> validator.validateCandidateSelectionForClosing(vacancy, List.of(CANDIDATE_ID)));
        }
    }
}