package faang.school.projectservice.validator.internship;

import faang.school.projectservice.adapter.*;
import faang.school.projectservice.dto.internship.*;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InternshipServiceValidator {
    private final ProjectRepositoryAdapter projectRepositoryAdapter;
    private final TeamMemberRepositoryAdapter teamMemberRepositoryAdapter;
    private final InternshipRepositoryAdapter internshipRepositoryAdapter;

    public void checkDataBeforeCreate(InternshipDto internshipDto) {
        Project project = getProjectById(internshipDto);
        checkProjectStatus(project);
        checkMentorProjectTeamMember(internshipDto.getMentorId(), project.getId());
    }

    public void checkDataBeforeUpdate(InternshipUpdateDto internshipUpdateDto) {
        Internship internship = internshipRepositoryAdapter.findById(internshipUpdateDto.getId());
        Project project = internship.getProject();
        checkMentorProjectTeamMember(internshipUpdateDto.getMentorId(), project.getId());
        checkNewInternAdd(internshipUpdateDto);
    }

    public void checkTeamRoleIsNotNull(TeamRole teamRole) {
        if (teamRole == null) {
            throw new DataValidationException("Team role can't be empty.");
        }
    }

    private void checkNewInternAdd(InternshipUpdateDto internshipUpdateDto) {
        Internship internship = internshipRepositoryAdapter.findById(internshipUpdateDto.getId());
        if (internship.getInterns().size() < internshipUpdateDto.getInterns().size()) {
            throw new DataValidationException("You can't add a new intern after starting of internship. Please, " +
                    "create a new internship for new intern");
        }
    }

    private Project getProjectById(InternshipDto internshipDto) {
        return projectRepositoryAdapter.findById(internshipDto.getProjectId());
    }

    private void checkMentorProjectTeamMember(Long mentorId, Long projectId) {
        TeamMember mentorFromProject = teamMemberRepositoryAdapter.findByUserIdAndProjectId(mentorId,
                projectId);
        if (mentorFromProject == null) {
            throw new DataValidationException(String.format("Mentor with id %d not from project %d team",
                    mentorId, projectId));
        }
    }

    private void checkProjectStatus(Project project) {
        ProjectStatus projectStatus = project.getStatus();
        if (projectStatus == ProjectStatus.ON_HOLD || projectStatus == ProjectStatus.CANCELLED
                || projectStatus == ProjectStatus.COMPLETED) {
            throw new DataValidationException(String.format("It is not possible to add an internship to a project " +
                    "with the status: %s", projectStatus));
        }
    }
}
