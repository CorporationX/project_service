package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.enums.InternshipStatus;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.enums.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class InternshipValidator {
    private static final int MAX_INTERNSHIP_DURATION_IN_MONTHS = 3;

    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipRepository internshipRepository;

    public void validateProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException(String.format("Project not found with id: %s", projectId));
        }
    }

    public void validateInternsPresence(List<Long> internUserIds) {
        if (internUserIds == null || internUserIds.isEmpty()) {
            throw new DataValidationException("An internship should have interns");
        }
    }

    public void validateInternshipStatus(InternshipStatus currentStatus, InternshipStatus nextStatus) {
        if (currentStatus != null && currentStatus != nextStatus && currentStatus.getNextStatus() != nextStatus) {
            throw new DataValidationException(String.format("Invalid internship status transition from %s to %s", currentStatus, nextStatus));
        }
    }

    public void validateProjectUnchanged(Project project, Long projectId) {
        if (!project.getId().equals(projectId)) {
            throw new DataValidationException("The project can't be changed. Please create a new internship");
        }
    }

    public void validateNoNewInternsAfterStart(Set<Long> internshipInternsUserIds, Set<Long> dtoInternsUserIds) {
        Set<Long> internsUserIdsForAdd = new HashSet<>(dtoInternsUserIds);
        internsUserIdsForAdd.removeAll(internshipInternsUserIds);
        if (!internsUserIdsForAdd.isEmpty()) {
            throw new DataValidationException("It is forbidden to add interns after the internship has started");
        }
    }

    public void validateNewTeamRolePresence(TeamRole newTeamRole) {
        if (newTeamRole == null) {
            throw new DataValidationException("A new team role must be selected");
        }
    }

    public void validateIdFromPath(long id, Long internshipDtoId) {
        if (internshipDtoId != null && internshipDtoId != id) {
            throw new DataValidationException("The ID in the path must match the ID in the DTO");
        }
    }

    public void validateInternshipDuration(LocalDateTime startDate, LocalDateTime endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new DataValidationException("End date of internship should be after start date");
        }

        if (startDate.plusMonths(MAX_INTERNSHIP_DURATION_IN_MONTHS).isBefore(endDate)) {
            throw new DataValidationException(
                    String.format("Internship duration shouldn't be more than %d months", MAX_INTERNSHIP_DURATION_IN_MONTHS));
        }
    }

    public void validateMentorAssignedToProject(Long mentorId, Long projectId) {
        TeamMember mentor = teamMemberRepository.findByUserIdAndProjectId(mentorId, projectId);
        if (mentor == null) {
            throw new DataValidationException(String.format("Mentor id = %d doesn't work with project id = %d", mentorId, projectId));
        }
    }

    public void validateInternshipNotCreated(Long projectId, Long mentorId, Long mentorUserId) {
        if (internshipRepository.existsByProjectIdAndMentorIdAndStatusNotCompleted(projectId, mentorId)) {
            throw new DataValidationException(
                    String.format("An incomplete internship already exists on the project " +
                            "with id = %d and mentor userId = %d", projectId, mentorUserId));
        }
    }
}
