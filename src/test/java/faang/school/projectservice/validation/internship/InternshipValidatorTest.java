package faang.school.projectservice.validation.internship;

import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validation.user.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InternshipValidatorTest {

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private InternshipValidator internshipValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateFinishInternshipForIntern_success() {
        Internship internship = mock(Internship.class);
        TeamMember intern = mock(TeamMember.class);
        when(internship.getEndDate()).thenReturn(LocalDateTime.now().plusDays(10));

        internshipValidator.validateFinishInternshipForIntern(internship, intern);
    }

    @Test
    void validateFinishInternshipForIntern_afterEndDate_throwsException() {
        Internship internship = mock(Internship.class);
        TeamMember intern = mock(TeamMember.class);
        when(internship.getEndDate()).thenReturn(LocalDateTime.now().minusDays(10));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            internshipValidator.validateFinishInternshipForIntern(internship, intern);
        });

        assertEquals(String.format("Internship %d already finished.", internship.getId()), exception.getMessage());
    }

    @Test
    void validateRemoveInternFromInternship_success() {
        Internship internship = mock(Internship.class);
        TeamMember intern = mock(TeamMember.class);
        when(internship.getInterns()).thenReturn(List.of(intern));

        internshipValidator.validateRemoveInternFromInternship(internship, intern);
    }

    @Test
    void validateRemoveInternFromInternship_notInInternship_throwsException() {
        Internship internship = mock(Internship.class);
        TeamMember intern = mock(TeamMember.class);
        when(internship.getInterns()).thenReturn(Collections.emptyList());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            internshipValidator.validateRemoveInternFromInternship(internship, intern);
        });

        assertEquals(String.format("Intern with id %d is not in internship with id %d", intern.getId(), internship.getId()), exception.getMessage());
    }

    @Test
    void validateTeamMemberDontHaveThisRole_alreadyHasRole_throwsException() {
        TeamMember teamMember = mock(TeamMember.class);
        when(teamMember.getRoles()).thenReturn(List.of(TeamRole.DEVELOPER));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            internshipValidator.validateTeamMemberDontHaveThisRole(teamMember, TeamRole.DEVELOPER);
        });

        assertEquals(String.format("Team member %d already has role %s", teamMember.getId(), TeamRole.DEVELOPER.name()), exception.getMessage());
    }
}
