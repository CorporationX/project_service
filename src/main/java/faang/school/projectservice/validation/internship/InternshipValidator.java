package faang.school.projectservice.validation.internship;

import faang.school.projectservice.dto.internship.InternshipToCreateDto;
import faang.school.projectservice.dto.internship.InternshipToUpdateDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validation.user.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class InternshipValidator {
    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserValidator userValidator;

    public void validateCreateInternship(long userId, Internship internship, InternshipToCreateDto internshipDto) {
        userValidator.validateUserExistence(userId);
        validateMentorExistence(internshipDto.getMentorId());
        validateInternsExistence(internship.getInterns());
        validateProjectExistence(internship.getProject().getId());
        validateInternshipHaveAnyIntern(internship);
        validateInternshipDuration(internship);
        validateDates(internship.getStartDate(), internship.getEndDate());
    }

    public void validateUpdateInternship(Internship internship, InternshipToUpdateDto updatedInternshipDto) {
        validateInternshipExists(internship);
        validateDates(updatedInternshipDto.getStartDate(), updatedInternshipDto.getEndDate());
        validateMentorExistence(updatedInternshipDto.getMentorId());
        validateInternsExistence(internship.getInterns());
        validateProjectExistence(internship.getProject().getId());
        validateInternshipDuration(internship);
    }

    public void validateFinishInternshipForIntern(Internship internship, TeamMember intern) {
        validateDeadline(internship);
        checkAllTasksDone(intern);
    }

    public void validateRemoveInternFromInternship(Internship internship, TeamMember intern) {
        validateInternshipContainsThisIntern(internship, intern);
    }

    public void validateTeamMemberDontHaveThisRole(TeamMember teamMember, TeamRole newRole) {
        if (teamMember.getRoles().contains(newRole)) {
            throw new DataValidationException(String.format("Team member %d already has role %s", teamMember.getId(), newRole.name()));
        }
    }

    private void validateInternshipExists(Internship internship) {
        if (!internshipRepository.existsById(internship.getId())) {
            throw new NotFoundException("Internship does not exist.");
        }
    }

    private void validateMentorExistence(long mentorId) {
        TeamMember mentor = teamMemberRepository.findById(mentorId);
        userValidator.validateUserExistence(mentor.getUserId());
    }

    private void validateInternsExistence(Iterable<TeamMember> interns) {
        for (TeamMember intern : interns) {
            TeamMember user = teamMemberRepository.findById(intern.getId());
            userValidator.validateUserExistence(user.getUserId());
        }
    }

    private void validateProjectExistence(long projectId) {
        projectRepository.findById(projectId);
    }

    private void validateInternshipHaveAnyIntern(Internship internship) {
        if (internship.getInterns().isEmpty()) {
            throw new DataValidationException("Internship must have at least one intern.");
        }
    }

    private void validateInternshipDuration(Internship internship) {
        long monthsBetween = ChronoUnit.MONTHS.between(internship.getStartDate(), internship.getEndDate());
        if (monthsBetween > 3 || (monthsBetween == 3 && ChronoUnit.DAYS.between(internship.getStartDate().plusMonths(3), internship.getEndDate()) > 0)) {
            throw new DataValidationException("Internship duration exceeds three months.");
        }
    }

    private void validateInternshipContainsThisIntern(Internship internship, TeamMember intern) {
        if (!internship.getInterns().contains(intern)) {
            throw new DataValidationException(String.format("Intern with id %d is not in internship with id %d", intern.getId(), internship.getId()));
        }
    }

    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new DataValidationException("Start date cannot be greater than or equal to end date.");
        }
    }

    private void validateDeadline(Internship internship) {
        if (!LocalDateTime.now().isBefore(internship.getEndDate())) {
            throw new DataValidationException(String.format("Internship %d already finished.", internship.getId()));
        }
    }

    private void checkAllTasksDone(TeamMember intern) {
        boolean notDoneTask = intern.getStages().stream()
                .flatMap(stage -> stage.getTasks().stream())
                .anyMatch(task -> !task.getStatus().equals(TaskStatus.DONE));
        if (notDoneTask) {
            throw new DataValidationException(String.format("Intern with id %d has unfinished tasks.", intern.getId()));
        }
    }
}
