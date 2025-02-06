package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipEditDto;
import faang.school.projectservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class InternshipValidatorTest {
    private static final Long PROJECT_ID = 1L;
    private static final Long MENTOR_ID = 2L;
    private static final Long TEAM_ID = 3L;
    private static final Long INTERN_ID = 4L;
    private static final Long INTERNSHIP_ID = 5L;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2024, 12, 1, 12, 0);
    private static final LocalDateTime INCORRECT_END_DATE = LocalDateTime.of(2025, 5, 1, 12, 0);
    private static final LocalDateTime CORRECT_END_DATE = LocalDateTime.of(2025, 2, 1, 12, 0);

    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private InternshipValidator internshipValidator;

    @Test
    void testProjectExists() {
        InternshipCreateDto internshipDto = InternshipCreateDto.builder()
                .projectId(PROJECT_ID)
                .build();

        Mockito.when(projectRepository.existsById(PROJECT_ID)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> internshipValidator.validateInternshipCreation(internshipDto));
    }

    @Test
    void testInternshipDuration() {
        InternshipCreateDto internshipDto = InternshipCreateDto.builder()
                .projectId(PROJECT_ID)
                .startDate(START_DATE)
                .endDate(INCORRECT_END_DATE)
                .build();

        Mockito.when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> internshipValidator.validateInternshipCreation(internshipDto));
    }

    @Test
    void testMentorBelongsProject() {
        InternshipCreateDto internshipDto = InternshipCreateDto.builder()
                .projectId(PROJECT_ID)
                .mentorId(MENTOR_ID)
                .startDate(START_DATE)
                .endDate(CORRECT_END_DATE)
                .build();

        Team team = Team.builder()
                .id(TEAM_ID)
                .build();

        Project project = Project.builder()
                .id(PROJECT_ID)
                .teams(List.of(team))
                .build();

        Mockito.when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        Mockito.when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        assertThrows(EntityNotFoundException.class, () -> internshipValidator.validateInternshipCreation(internshipDto));
    }

    @Test
    void testAddingInterns() {
        InternshipEditDto internshipDto = InternshipEditDto.builder()
                .id(INTERNSHIP_ID)
                .internsIds(new ArrayList<>(List.of(INTERN_ID)))
                .startDate(START_DATE)
                .endDate(CORRECT_END_DATE)
                .build();


        TeamMember intern = TeamMember.builder()
                .id(INTERN_ID)
                .build();

        Internship internship = Internship.builder()
                .interns(new ArrayList<>(List.of(intern)))
                .build();

        Mockito.when(internshipRepository.findById(INTERNSHIP_ID)).thenReturn(Optional.of(internship));
        assertThrows(DataValidationException.class, () -> internshipValidator.validateInternshipUpdating(internshipDto));
    }

    @Test
    void testInternCompletedInternship() {
        InternshipEditDto internshipDto = InternshipEditDto.builder()
                .status(InternshipStatus.COMPLETED)
                .build();

        Task task = Task.builder()
                .performerUserId(INTERN_ID)
                .status(TaskStatus.DONE)
                .build();

        Project project = Project.builder()
                .id(PROJECT_ID)
                .tasks(List.of(task))
                .build();

        Mockito.when(projectRepository.findById(any())).thenReturn(Optional.of(project));
        assertTrue(internshipValidator.validateInternCompletedInternship(internshipDto, INTERN_ID));
    }
}