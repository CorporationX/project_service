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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InternshipValidator {
    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserValidator userValidator;
    @Value("${internship.duration.max.month}")
    private int MONTH_AMOUNT;

    public void validateCreateInternship(long userId, InternshipToCreateDto internshipDto) {
        userValidator.validateUserExistence(userId);
        validateMentorExistence(internshipDto.getMentorId());
        validateInternsExistence(internshipDto.getInternsId());
        validateProjectExistence(internshipDto.getProjectId());
        validateInternshipHaveAnyIntern(internshipDto.getInternsId());
        validateInternshipDuration(internshipDto.getStartDate(), internshipDto.getEndDate());
        validateDates(internshipDto.getStartDate(), internshipDto.getEndDate());
    }

    public void validateUpdateInternship(Internship internship, InternshipToUpdateDto internshipDto) {
        validateInternshipExists(internship.getId());
        validateDates(internshipDto.getStartDate(), internshipDto.getEndDate());
        validateMentorExistence(internshipDto.getMentorId());
        validateInternsExistence(internshipDto.getInternsId());
        validateProjectExistence(internshipDto.getProjectId());
        validateInternshipDuration(internshipDto.getStartDate(), internshipDto.getEndDate());
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

    private void validateInternshipExists(long internshipId) {
        if (!internshipRepository.existsById(internshipId)) {
            throw new NotFoundException("Internship does not exist.");
        }
    }

    private void validateMentorExistence(long mentorId) {
        TeamMember mentor = teamMemberRepository.findById(mentorId);
        userValidator.validateUserExistence(mentor.getUserId());
    }

    private void validateInternsExistence(List<Long> internIds) {
        for (Long internId : internIds) {
            TeamMember intern = teamMemberRepository.findById(internId);
            userValidator.validateUserExistence(intern.getUserId());
        }
    }

    private void validateProjectExistence(long projectId) {
        projectRepository.findById(projectId);
    }

    private void validateInternshipHaveAnyIntern(List<Long> internIds) {
        if (internIds.isEmpty()) {
            throw new DataValidationException("Internship must have at least one intern.");
        }
    }

    private void validateInternshipDuration(LocalDateTime startDate, LocalDateTime endDate) {
        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
        if (monthsBetween > MONTH_AMOUNT || (monthsBetween == MONTH_AMOUNT && ChronoUnit.DAYS.between(startDate.plusMonths(MONTH_AMOUNT), endDate) > 0)) {
            throw new DataValidationException("Internship duration exceeds the maximum allowed duration.");
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
