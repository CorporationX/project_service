package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;

import java.util.List;

public class MomentValidator {
    public void validateMoment(Moment moment) {
        if (moment.getName().isBlank()) {
            throw new DataValidationException("Name is blank");
        }
        if (moment.getProjects().isEmpty()) {
            throw new DataValidationException("The moment is not assigned to any project");
        }
        List<Project> projects = moment.getProjects();
        for (Project project : projects) {
            if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
                throw new DataValidationException("The moment is assigned to a project that either cancelled or completed");
            }
        }
    }
}
