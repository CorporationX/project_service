package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.VisibilitySubprojectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;

@Service
@RequiredArgsConstructor
public class SubProjectService {
    private final ProjectRepository projectRepository;

    public void updateVisibilitySubProject(VisibilitySubprojectUpdateDto updateStatusSubprojectDto) {
        Project project = getProjectById(updateStatusSubprojectDto.getId());
        Project parentProject = project.getParentProject();
        ProjectVisibility visibility = updateStatusSubprojectDto.getVisibility();

        if (visibility == ProjectVisibility.PUBLIC && parentProject.getVisibility() == ProjectVisibility.PRIVATE) {
            throw new DataValidationException("Ypu can't make public project in private project");
        }

        if (visibility == ProjectVisibility.PRIVATE) {
            changeAllVisibilityInSubproject(project);
        }

        project.setUpdatedAt(LocalDateTime.now());
        project.setVisibility(visibility);
    }

    public Project getProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public boolean isExistProjectById(long projectId) {
        return projectRepository.existsById(projectId);
    }

    private void changeAllVisibilityInSubproject(Project project) {
        Deque<Project> stack = new ArrayDeque<>();
        stack.push(project);

        while (!stack.isEmpty()) {
            Project currentProject = stack.pop();
            currentProject.setVisibility(ProjectVisibility.PRIVATE);

            if (currentProject.getChildren() == null) {
                continue;
            }

            for (Project subproject : currentProject.getChildren()) {
                stack.push(subproject);
            }
        }
    }
}
