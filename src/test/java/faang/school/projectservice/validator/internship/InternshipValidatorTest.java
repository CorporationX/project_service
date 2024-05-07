package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InternshipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipValidatorTest {
    @Mock
    private InternshipRepository internshipRepository;

    @InjectMocks
    private InternshipValidator internshipValidator;

    @Test
    void validateInternshipExistence() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);

        when(internshipRepository.existsById(1L)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> internshipValidator.validateInternshipExistence(internshipDto));

        verify(internshipRepository).existsById(1L);
    }

    @Test
    void validateInternshipNotExistence() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);

        when(internshipRepository.existsById(1L)).thenReturn(false);

        assertDoesNotThrow(() -> internshipValidator.validateInternshipExistence(internshipDto));

        verify(internshipRepository).existsById(1L);
    }

    @Test
    void validateInternshipNotStarted_ShouldThrowIfStarted() {
        Internship internship = new Internship();
        internship.setStartDate(LocalDateTime.now().minusDays(1));

        assertThrows(DataValidationException.class, () -> internshipValidator.validateInternshipNotStarted(internship));
    }

    @Test
    void validateInternshipNotStarted_ShouldNotThrowIfNotStarted() {
        Internship internship = new Internship();
        internship.setStartDate(LocalDateTime.now().plusDays(1));

        assertDoesNotThrow(() -> internshipValidator.validateInternshipNotStarted(internship));
    }

    @Test
    void validateInternNotAlreadyInInternship_ShouldThrowIfInternIsInInternship() {
        Internship internship = new Internship();
        TeamMember intern = new TeamMember();
        intern.setId(1L);
        internship.setInterns(Collections.singletonList(intern));

        assertThrows(DataValidationException.class, () -> internshipValidator.validateInternNotAlreadyInInternship(internship, intern));
    }

    @Test
    void validateInternNotAlreadyInInternship_ShouldNotThrowIfInternIsNotInInternship() {
        Internship internship = new Internship();
        TeamMember intern = new TeamMember();
        intern.setId(1L);
        internship.setInterns(new ArrayList<>());

        assertDoesNotThrow(() -> internshipValidator.validateInternNotAlreadyInInternship(internship, new TeamMember()));
    }

    @Test
    void validateInternshipNotCompleted_ShouldThrowIfCompleted() {
        Internship internship = new Internship();
        internship.setStatus(InternshipStatus.COMPLETED);

        assertThrows(IllegalStateException.class, () -> internshipValidator.validateInternshipNotCompleted(internship));
    }

    @Test
    void validateInternshipNotCompleted_ShouldNotThrowIfNotCompleted() {
        Internship internship = new Internship();
        internship.setStatus(InternshipStatus.IN_PROGRESS);

        assertDoesNotThrow(() -> internshipValidator.validateInternshipNotCompleted(internship));
    }

    @Test
    void validateUpdatedInternshipDiffersByLast_ShouldThrowIfSame() {
        Internship original = new Internship();
        original.setId(1L);
        original.setStatus(InternshipStatus.IN_PROGRESS);

        Internship updated = new Internship();
        updated.setId(1L);
        updated.setStatus(InternshipStatus.IN_PROGRESS);

        assertThrows(DataValidationException.class, () -> internshipValidator.validateUpdatedInternshipDiffersByLast(original, updated));
    }

    @Test
    void validateUpdatedInternshipDiffersByLast_ShouldNotThrowIfDifferent() {
        Internship original = new Internship();
        original.setId(1L);
        original.setStatus(InternshipStatus.IN_PROGRESS);

        Internship updated = new Internship();
        updated.setId(1L);
        updated.setStatus(InternshipStatus.COMPLETED);

        assertDoesNotThrow(() -> internshipValidator.validateUpdatedInternshipDiffersByLast(original, updated));
    }

    @Test
    void checkAllTasksDone_ShouldThrowIfNotAllTasksDone() {
        Task task1 = new Task();
        task1.setStatus(TaskStatus.DONE);
        Task task2 = new Task();
        task2.setStatus(TaskStatus.IN_PROGRESS);
        TeamMember intern = new TeamMember();
        Stage stage = new Stage();
        stage.setTasks(Arrays.asList(task1, task2));
        intern.setId(1L);
        intern.setStages(List.of(stage));

        assertThrows(DataValidationException.class, () -> internshipValidator.checkAllTasksDone(intern));
    }

    @Test
    void checkAllTasksDone_ShouldNotThrowIfAllTasksDone() {
        Task task1 = new Task();
        task1.setStatus(TaskStatus.DONE);
        Task task2 = new Task();
        task2.setStatus(TaskStatus.DONE);

        Stage stage = new Stage();
        stage.setTasks(Arrays.asList(task1, task2));

        TeamMember intern = new TeamMember();
        intern.setId(1L);
        intern.setStages(List.of(stage));

        assertDoesNotThrow(() -> internshipValidator.checkAllTasksDone(intern));
    }

    @Test
    void validateInternNotAlreadyInInternship() {
    }

    @Test
    void validateInternshipContainsThisIntern_ShouldThrowIfInternNotContained() {
        Internship internship = new Internship();
        internship.setId(1L);
        internship.setInterns(Collections.emptyList());
        TeamMember intern = new TeamMember();
        intern.setId(1L);

        assertThrows(DataValidationException.class, () -> internshipValidator.validateInternshipContainsThisIntern(internship, intern));
    }

    @Test
    void validateInternshipContainsThisIntern_ShouldNotThrowIfInternIsContained() {
        Internship internship = new Internship();
        internship.setId(1L);
        internship.setInterns(Collections.emptyList());
        TeamMember intern = new TeamMember();
        intern.setId(1L);

        assertThrows(DataValidationException.class, () -> internshipValidator.validateInternshipContainsThisIntern(internship, intern));
    }

}