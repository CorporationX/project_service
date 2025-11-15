package faang.school.projectservice.validator.internship;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.internship.AlreadyCompletedException;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;

import java.time.LocalDateTime;
import java.util.List;

public class InternshipValidator {

    public static final long MAX_INTERNSHIP_LENGTH_MONTHS = 3L;

    public static void validateLengthDate(LocalDateTime start, LocalDateTime end) {
        if (start == null || start.isAfter(end)) {
            throw new DataValidationException("The beginning of the internship cannot be null or after the end, or end is beginning before start internship.");
        }
        if (end.isAfter(start.plusMonths(MAX_INTERNSHIP_LENGTH_MONTHS))) {
            throw new DataValidationException("Internship can't be longer than three months.");
        }
    }

    public static void validateMentorBelongsToProject(Project project, TeamMember mentor) {
        if (project.getTeams() == null || project.getTeams().isEmpty()) {
            throw new EntityNotFoundException("Project has no associated teams.");
        }
        boolean belongsToAnyTeam = project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().contains(mentor));
        if (!belongsToAnyTeam) {
            throw new EntityNotFoundException("Mentor is not a member of the project team");
        }
    }

    public static void validateInternsNotNullAndNotEmpty(List<TeamMember> interns) {
        if (interns == null || interns.isEmpty()) {
            throw new EntityNotFoundException("Interns are missing.");
        }
    }

    public static void validateIfInternshipIsStatusComplete(InternshipStatus status) {
        if (status == InternshipStatus.COMPLETED) {
            throw new AlreadyCompletedException();
        }
    }
}