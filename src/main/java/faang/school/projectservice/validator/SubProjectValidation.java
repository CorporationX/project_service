package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.springframework.stereotype.Component;

@Component
public class SubProjectValidation {

    public void updateSubProject(long userId, UpdateSubProjectDto updateSubProjectDto, Project project) {
        if (!project.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("Only the owner can change the project");
        }

        if (updateSubProjectDto.status().equals(ProjectStatus.COMPLETED)) {
            project.getChildren()
                    .forEach(subProject -> {
                        if (!subProject.getStatus().equals(ProjectStatus.COMPLETED) &&
                                !subProject.getStatus().equals(ProjectStatus.CANCELLED)) {
                            throw new IllegalArgumentException("All subprojects must be completed or cancelled");
                        }
                    });
        }
    }
}