package faang.school.projectservice.validations.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class SubProjectValidator {

    public void canBeParentProject(Project parentProject) {
        boolean canBe = parentProject.getParentProject() == null &&
                parentProject.getStatus() != ProjectStatus.COMPLETED &&
                parentProject.getStatus() != ProjectStatus.CANCELLED;
        if (!canBe) {
            throw new DataValidationException("Project can't be parent");
        }
    }

    public void shouldBePublic(Project project) {
        if (project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new DataValidationException("Parent project is private");
        }
    }

    public void childCompleted(List<Project> projects) {
        if (projects != null) {
            for (Project project : projects) {
                if (!(Objects.equals(project.getStatus(), ProjectStatus.COMPLETED)) &&
                        !(Objects.equals(project.getStatus(), ProjectStatus.CANCELLED))) {
                    throw new DataValidationException("Project can't be closed");
                }
            }
        }
    }
}
