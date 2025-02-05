package faang.school.projectservice.validator;

import faang.school.projectservice.exception.VacancyValidationException;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

        assertDoesNotThrow(() -> vacancyValidator.validateCreatedVacancy(vacancy));
    }

    @Test
    public void validateCreateVacancy_ShouldThrowVacancyExceptionWhenCreatedByUserIsNotOwnerOrManager() {
        Vacancy vacancy = Vacancy.builder()
                .createdBy(19L)
                .project(Project.builder().id(1L).build())
                .build();

        when(teamMemberRepository.findByUserIdAndProjectId(vacancy.getCreatedBy(), vacancy.getProject().getId()))
                .thenReturn(TeamMember.builder().roles(List.of(TeamRole.DEVELOPER)).build());

        assertThrows(VacancyValidationException.class, () -> vacancyValidator.validateCreatedVacancy(vacancy));
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

        when(teamMemberRepository.findByUserIdAndProjectId(candidates.get(0).getUserId(), vacancy.getProject().getId()))
                .thenReturn(null);
        when(teamMemberRepository.findByUserIdAndProjectId(candidates.get(1).getUserId(), vacancy.getProject().getId()))
                .thenReturn(null);
        when(teamMemberRepository.findByUserIdAndProjectId(candidates.get(2).getUserId(), vacancy.getProject().getId()))
                .thenReturn(null);

        assertDoesNotThrow(() -> vacancyValidator.validateUpdatedVacancy(vacancy));

        verify(teamMemberRepository, times(1))
                .findByUserIdAndProjectId(candidates.get(0).getUserId(), vacancy.getProject().getId());
        verify(teamMemberRepository, times(1))
                .findByUserIdAndProjectId(candidates.get(1).getUserId(), vacancy.getProject().getId());
        verify(teamMemberRepository, times(1))
                .findByUserIdAndProjectId(candidates.get(2).getUserId(), vacancy.getProject().getId());
    }

    @Test
    public void validateUpdateVacancy_ShouldThrowVacancyExceptionWhenUpdatedByUserIsNotOwnerOrManager() {
        Vacancy vacancy = Vacancy.builder()
                .updatedBy(19L)
                .project(Project.builder().id(1L).build())
                .build();

        when(teamMemberRepository.findByUserIdAndProjectId(vacancy.getUpdatedBy(), vacancy.getProject().getId()))
                .thenReturn(TeamMember.builder().roles(List.of(TeamRole.DEVELOPER)).build());

        assertThrows(VacancyValidationException.class, () -> vacancyValidator.validateUpdatedVacancy(vacancy));
    }

    @Test
    public void validateUpdateVacancy_ShouldThrowVacancyExceptionWhenCandidateIsTeamMember() {
        List<Candidate> candidates = List.of(new Candidate(), new Candidate(), new Candidate());
        candidates.get(0).setUserId(16L);
        candidates.get(1).setUserId(18L);
        candidates.get(2).setUserId(19L);

        Vacancy vacancy = Vacancy.builder()
                .updatedBy(19L)
                .project(Project.builder().id(1L).build())
                .candidates(candidates)
                .build();

        when(teamMemberRepository.findByUserIdAndProjectId(vacancy.getUpdatedBy(), vacancy.getProject().getId()))
                .thenReturn(TeamMember.builder().roles(List.of(TeamRole.MANAGER)).build());

        when(teamMemberRepository.findByUserIdAndProjectId(candidates.get(0).getUserId(), vacancy.getProject().getId()))
                .thenReturn(new TeamMember());

        assertThrows(VacancyValidationException.class, () -> vacancyValidator.validateUpdatedVacancy(vacancy));
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

        when(teamMemberRepository.findByUserIdAndProjectId(candidates.get(0).getUserId(), vacancy.getProject().getId()))
                .thenReturn(null);
        when(teamMemberRepository.findByUserIdAndProjectId(candidates.get(1).getUserId(), vacancy.getProject().getId()))
                .thenReturn(null);
        when(teamMemberRepository.findByUserIdAndProjectId(candidates.get(2).getUserId(), vacancy.getProject().getId()))
                .thenReturn(null);

        assertThrows(VacancyValidationException.class, () -> vacancyValidator.validateUpdatedVacancy(vacancy));

        verify(teamMemberRepository, times(1))
                .findByUserIdAndProjectId(candidates.get(0).getUserId(), vacancy.getProject().getId());
        verify(teamMemberRepository, times(1))
                .findByUserIdAndProjectId(candidates.get(1).getUserId(), vacancy.getProject().getId());
        verify(teamMemberRepository, times(1))
                .findByUserIdAndProjectId(candidates.get(2).getUserId(), vacancy.getProject().getId());
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

        when(teamMemberRepository.findByUserIdAndProjectId(candidates.get(0).getUserId(), vacancy.getProject().getId()))
                .thenReturn(null);
        when(teamMemberRepository.findByUserIdAndProjectId(candidates.get(1).getUserId(), vacancy.getProject().getId()))
                .thenReturn(null);
        when(teamMemberRepository.findByUserIdAndProjectId(candidates.get(2).getUserId(), vacancy.getProject().getId()))
                .thenReturn(null);

        assertThrows(VacancyValidationException.class, () -> vacancyValidator.validateUpdatedVacancy(vacancy));

        verify(teamMemberRepository, times(1))
                .findByUserIdAndProjectId(candidates.get(0).getUserId(), vacancy.getProject().getId());
        verify(teamMemberRepository, times(1))
                .findByUserIdAndProjectId(candidates.get(1).getUserId(), vacancy.getProject().getId());
        verify(teamMemberRepository, times(1))
                .findByUserIdAndProjectId(candidates.get(2).getUserId(), vacancy.getProject().getId());
    }
}
