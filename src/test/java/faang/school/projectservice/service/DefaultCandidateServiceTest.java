package faang.school.projectservice.service;

import faang.school.projectservice.event.CandidateAcceptedEvent;
import faang.school.projectservice.event.CandidateRejectedEvent;
import faang.school.projectservice.event.DomainEventPublisher;
import faang.school.projectservice.exception.BusinessValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultCandidateServiceTest {
    private static final Long VACANCY_ID = 1L;
    private static final Long CANDIDATE_ID = 2L;
    private static final Long TEAM_ID = 3L;
    private static final Long PROJECT_ID = 4L;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private DefaultCandidateService candidateService;

    private Candidate candidate;
    private Vacancy vacancy;
    private Team team;
    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(PROJECT_ID);

        vacancy = new Vacancy();
        vacancy.setId(VACANCY_ID);
        vacancy.setProject(project);
        vacancy.setStatus(VacancyStatus.OPEN);

        candidate = new Candidate();
        candidate.setId(CANDIDATE_ID);
        candidate.setVacancy(vacancy);

        team = new Team();
        team.setId(TEAM_ID);
    }

    @Nested
    @DisplayName("Tests for acceptCandidate method")
    class AcceptCandidateTests {

        @BeforeEach
        void setUp() {
            candidate.setCandidateStatus(CandidateStatus.WAITING_RESPONSE);
            team.setProject(project);
        }

        @Test
        @DisplayName("Should successfully accept a candidate and publish event")
        void testAcceptCandidateSuccess() {
            when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));
            when(teamService.getById(TEAM_ID)).thenReturn(team);

            candidateService.acceptCandidate(VACANCY_ID, CANDIDATE_ID, TEAM_ID);

            assertEquals(CandidateStatus.ACCEPTED, candidate.getCandidateStatus());
            assertEquals(team, candidate.getTeam());
            verify(candidateRepository).save(candidate);
            verify(eventPublisher).publishEvent(isA(CandidateAcceptedEvent.class));
        }

        @Test
        @DisplayName("Should throw BusinessValidationException if team is not part of vacancy's project")
        void testAcceptCandidateTeamNotInProject() {
            when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));
            Project newProject = new Project();
            newProject.setId(55L);
            team.setProject(newProject);
            when(teamService.getById(TEAM_ID)).thenReturn(team);

            BusinessValidationException ex = assertThrows(
                    BusinessValidationException.class,
                    () -> candidateService.acceptCandidate(VACANCY_ID, CANDIDATE_ID, TEAM_ID),
                    "Expected acceptCandidate() to throw BusinessValidationException when " +
                            "team is not part of vacancy's project");
            assertEquals("Team %d is not part of vacancy's project %d"
                    .formatted(TEAM_ID, PROJECT_ID), ex.getMessage());
            verify(candidateRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should throw BusinessValidationException if candidate is already accepted")
        void testAcceptCandidateAlreadyAccepted() {
            when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));
            candidate.setCandidateStatus(CandidateStatus.ACCEPTED);

            BusinessValidationException ex = assertThrows(
                    BusinessValidationException.class,
                    () -> candidateService.acceptCandidate(VACANCY_ID, CANDIDATE_ID, TEAM_ID),
                    "Expected acceptCandidate() to throw BusinessValidationException when " +
                            "candidate is already accepted");
            assertEquals("Candidate %d already has status %s"
                    .formatted(CANDIDATE_ID, CandidateStatus.ACCEPTED), ex.getMessage());
            verify(candidateRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should throw BusinessValidationException when candidate does not belong to vacancy (simulating getCandidateOrThrow)")
        void acceptCandidate_whenCandidateNotBelongingToVacancy_shouldThrowException() {
            when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));
            candidate.getVacancy().setId(VACANCY_ID + 1);

            BusinessValidationException ex = assertThrows(
                    BusinessValidationException.class, () ->
                candidateService.acceptCandidate(VACANCY_ID, CANDIDATE_ID, TEAM_ID)
            );
            assertEquals("Candidate %d does not belong to vacancy %d"
                    .formatted(CANDIDATE_ID, VACANCY_ID), ex.getMessage());
        }


        @Test
        @DisplayName("Should throw BusinessValidationException when vacancy is not open (simulating getCandidateOrThrow)")
        void acceptCandidate_whenVacancyNotOpen_shouldThrowException() {
            when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));
            candidate.getVacancy().setStatus(VacancyStatus.CLOSED);

            BusinessValidationException ex = assertThrows(
                    BusinessValidationException.class,
                    () -> candidateService.acceptCandidate(VACANCY_ID, CANDIDATE_ID, TEAM_ID)
            );
            assertEquals("Vacancy %d is not open"
                    .formatted(VACANCY_ID), ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for rejectedCandidate method")
    class RejectedCandidateTests {
        @BeforeEach
        void setUp() {
            candidate.setCandidateStatus(CandidateStatus.WAITING_RESPONSE);
            candidate.setTeam(team);
        }

        @Test
        @DisplayName("Should successfully reject a candidate and publish event")
        void testRejectCandidateSuccess() {
            when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));

            candidateService.rejectCandidate(VACANCY_ID, CANDIDATE_ID);

            assertEquals(CandidateStatus.REJECTED, candidate.getCandidateStatus());
            assertNull(candidate.getTeam());
            verify(candidateRepository).save(candidate);
            verify(eventPublisher).publishEvent(isA(CandidateRejectedEvent.class));
        }

        @Test
        @DisplayName("Should throw BusinessValidationException if candidate is already rejected")
        void testRejectCandidateAlreadyRejected() {
            when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));
            candidate.setCandidateStatus(CandidateStatus.REJECTED);

            BusinessValidationException ex = assertThrows(
                    BusinessValidationException.class,
                    () -> candidateService.rejectCandidate(VACANCY_ID, CANDIDATE_ID),
                    "Expected acceptCandidate() to throw BusinessValidationException when " +
                            "candidate is already accepted");
            assertEquals("Candidate %d already has status %s"
                    .formatted(CANDIDATE_ID, CandidateStatus.REJECTED), ex.getMessage());
            verify(candidateRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Tests for removeRejectedCandidates method")
    class RemoveRejectedCandidatesTests {
        private final Long specificVacancyId = 55L;

        @Test
        @DisplayName("Should invoke repository delete method with correct parameters")
        void testRemoveRejectedCandidatesSuccess() {
            List<Long> specificCandidateIds = List.of(100L, 101L, 102L);

            candidateService.removeRejectedCandidates(specificVacancyId, specificCandidateIds);

            verify(candidateRepository).deleteByVacancyIdAndCandidateStatusAndIdIn(
                    specificVacancyId,
                    CandidateStatus.REJECTED,
                    specificCandidateIds
            );
            verifyNoMoreInteractions(candidateRepository);
            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("Should throw BusinessValidationException and not invoke delete when candidateIds is null")
        void testRemoveRejectedCandidatesNullCandidateIds() {
            List<Long> nullCandidateIds = null;

            BusinessValidationException ex = assertThrows(
                    BusinessValidationException.class,
                    () -> candidateService.removeRejectedCandidates(specificVacancyId, nullCandidateIds), // Correct method call
                    "Expected removeRejectedCandidates() to throw BusinessValidationException when candidateIds is null");

            assertEquals("Invalid parameters. vacancyId=%s, candidateIds=%s"
                    .formatted(specificVacancyId, nullCandidateIds), ex.getMessage());

            verify(candidateRepository, never()).deleteByVacancyIdAndCandidateStatusAndIdIn(
                    anyLong(),
                    any(CandidateStatus.class),
                    isNull()
            );

            verifyNoMoreInteractions(candidateRepository);
            verifyNoInteractions(eventPublisher);
        }
    }
}
