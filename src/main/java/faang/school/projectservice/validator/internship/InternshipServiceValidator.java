package faang.school.projectservice.validator.internship;

import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.adapter.TeamMemberRepositoryAdapter;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class InternshipServiceValidator {
    private final ProjectRepositoryAdapter projectRepositoryAdapter;
    private final TeamMemberRepositoryAdapter teamMemberRepositoryAdapter;

    public void checkDataBeforeCreate(InternshipDto internshipDto) {
        Project project = projectRepositoryAdapter.findById(internshipDto.getProjectId());
        ProjectStatus projectStatus = project.getStatus();
        if (Objects.equals(projectStatus, ProjectStatus.ON_HOLD) ||
                Objects.equals(projectStatus, ProjectStatus.CANCELLED) ||
                Objects.equals(projectStatus, ProjectStatus.COMPLETED)) {
            throw new DataValidationException(String.format("It is not possible to add an internship to a project " +
                    "with the status: %s", projectStatus));
        }

        TeamMember mentor = teamMemberRepositoryAdapter.findById(internshipDto.getMentorId());

        TeamMember mentorFromProject = teamMemberRepositoryAdapter.findByUserIdAndProjectId(internshipDto.getMentorId(),project.getId());
        if ( mentorFromProject == null){
            throw new DataValidationException(String.format("Mentor with id %d not from project %d team",
                    internshipDto.getMentorId(), internshipDto.getProjectId()));
        }
    }
}
