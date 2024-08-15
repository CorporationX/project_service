package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MomentValidator {
    private final ProjectRepository projectRepository;
    private final MomentRepository momentRepository;

    public void validateProjects(Moment moment) {
        List<Project> projects = moment.getProjects();
        for (Project project : projects) {
            validateProject(project);
        }
    }

    public void validateProject(Project project) {
        if (!projectRepository.existsById(project.getId())) {
            throw new EntityNotFoundException("Project does not exist");
        }
        if (project.getName() == null || project.getName().isBlank()) {
            throw new DataValidationException("Project" + project.getId() + "has blank name");
        }
        if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new DataValidationException("Project " + project.getId() + "has been completed or cancelled");
        }
    }

    public List<Moment> getMomentsAttachedToProject(Long projectId) {
        List<Moment> moments = momentRepository.findAllByProjectId(projectId);
        if (moments == null || moments.isEmpty()) {
            throw new EntityNotFoundException("no moments found for project " + projectId);
        } else return moments;
    }

}
