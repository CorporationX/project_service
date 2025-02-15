package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyValidatorTest {
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private VacancyValidator vacancyValidator;

    @Test
    public void validateCreateVacancy_ShouldValidateSuccessfully() {
        Vacancy vacancy = Vacancy.builder()
                .createdBy(19L)
                .project(Project.builder().id(1L).build())
                .build();

        when(teamMemberRepository.findByUserIdAndProjectId(vacancy.getCreatedBy(), vacancy.getProject().getId()))
                .thenReturn(TeamMember.builder().roles(List.of(TeamRole.MANAGER)).build());

        assertDoesNotThrow(() -> vacancyValidator.validateCreatingVacancy(vacancy));
    }

    @Test
    public void validateCreateVacancy_ShouldThrowVacancyExceptionWhenCreatedByUserIsNotOwnerOrManager() {
        Vacancy vacancy = Vacancy.builder()
                .createdBy(19L)
                .project(Project.builder().id(1L).build())
                .build();

        when(teamMemberRepository.findByUserIdAndProjectId(vacancy.getCreatedBy(), vacancy.getProject().getId()))
                .thenReturn(TeamMember.builder().roles(List.of(TeamRole.DEVELOPER)).build());

        assertThrows(DataValidationException.class, () -> vacancyValidator.validateCreatingVacancy(vacancy));
    }

    @Test
    public void validateUpdateVacancy_ShouldValidateSuccessfully() {
        List<Candidate> candidates = List.of(new Candidate(), new Candidate(), new Candidate());
        candidates.get(0).setUserId(209L);
        candidates.get(1).setUserId(188L);
        candidates.get(2).setUserId(201L);

        candidates.get(0).setCandidateStatus(CandidateStatus.ACCEPTED);
        candidates.get(1).setCandidateStatus(CandidateStatus.ACCEPTED);
        candidates.get(2).setCandidateStatus(CandidateStatus.REJECTED);

        Vacancy vacancy = Vacancy.builder()
                .updatedBy(19L)
                .project(Project.builder().id(1L).build())
                .candidates(candidates)
                .status(VacancyStatus.CLOSED)
                .count(2)
                .build();

        when(teamMemberRepository.findByUserIdAndProjectId(vacancy.getUpdatedBy(), vacancy.getProject().getId()))
                .thenReturn(TeamMember.builder().roles(List.of(TeamRole.MANAGER)).build());

        assertDoesNotThrow(() -> vacancyValidator.validateUpdatingVacancy(vacancy));
    }

    @Test
    public void validateUpdateVacancy_ShouldThrowVacancyExceptionWhenUpdatedByUserIsNotOwnerOrManager() {
        Vacancy vacancy = Vacancy.builder()
                .updatedBy(19L)
                .project(Project.builder().id(1L).build())
                .build();

        when(teamMemberRepository.findByUserIdAndProjectId(vacancy.getUpdatedBy(), vacancy.getProject().getId()))
                .thenReturn(TeamMember.builder().roles(List.of(TeamRole.DEVELOPER)).build());

        assertThrows(DataValidationException.class, () -> vacancyValidator.validateUpdatingVacancy(vacancy));
    }

    @Test
    public void validateUpdateVacancy_ShouldThrowVacancyExceptionWhenIsNotEnoughNumberOfCandidates() {
        List<Candidate> candidates = List.of(new Candidate(), new Candidate(), new Candidate());
        candidates.get(0).setUserId(209L);
        candidates.get(1).setUserId(188L);
        candidates.get(2).setUserId(201L);

        candidates.get(0).setCandidateStatus(CandidateStatus.ACCEPTED);
        candidates.get(1).setCandidateStatus(CandidateStatus.REJECTED);
        candidates.get(2).setCandidateStatus(CandidateStatus.REJECTED);

        Vacancy vacancy = Vacancy.builder()
                .updatedBy(19L)
                .project(Project.builder().id(1L).build())
                .candidates(candidates)
                .status(VacancyStatus.CLOSED)
                .count(2)
                .build();

        when(teamMemberRepository.findByUserIdAndProjectId(vacancy.getUpdatedBy(), vacancy.getProject().getId()))
                .thenReturn(TeamMember.builder().roles(List.of(TeamRole.MANAGER)).build());

        assertThrows(DataValidationException.class, () -> vacancyValidator.validateUpdatingVacancy(vacancy));
    }

    @Test
    public void validateUpdateVacancy_ShouldThrowVacancyExceptionWhenThereAreCandidatesWaitingForResponse() {
        List<Candidate> candidates = List.of(new Candidate(), new Candidate(), new Candidate());
        candidates.get(0).setUserId(209L);
        candidates.get(1).setUserId(188L);
        candidates.get(2).setUserId(201L);

        candidates.get(0).setCandidateStatus(CandidateStatus.ACCEPTED);
        candidates.get(1).setCandidateStatus(CandidateStatus.ACCEPTED);
        candidates.get(2).setCandidateStatus(CandidateStatus.WAITING_RESPONSE);

        Vacancy vacancy = Vacancy.builder()
                .updatedBy(19L)
                .project(Project.builder().id(1L).build())
                .candidates(candidates)
                .status(VacancyStatus.CLOSED)
                .count(2)
                .build();

        when(teamMemberRepository.findByUserIdAndProjectId(vacancy.getUpdatedBy(), vacancy.getProject().getId()))
                .thenReturn(TeamMember.builder().roles(List.of(TeamRole.MANAGER)).build());

        assertThrows(DataValidationException.class, () -> vacancyValidator.validateUpdatingVacancy(vacancy));
    }
}
