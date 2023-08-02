package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.UpdateStatusSubprojectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubProjectService {
    private final ProjectRepository projectRepository;

    public void updateStatusSubProject(UpdateStatusSubprojectDto updateStatusSubprojectDto) {
        Project project = getProjectById(updateStatusSubprojectDto.getId());
        ProjectStatus status = updateStatusSubprojectDto.getStatus();
        if (status == ProjectStatus.COMPLETED && project.getChildren() != null) {
            if (!checkStatusChildren(project.getChildren())) {
                throw new DataValidationException("You can make the project completed only after finishing all subprojects");
            }
        }
        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());
    }

    public Project getProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public boolean isExistProjectById(long projectId) {
        return projectRepository.existsById(projectId);
    }

    private boolean checkStatusChildren(List<Project> projects) {
        for (Project project : projects) {
            if (project.getStatus() != ProjectStatus.COMPLETED) {
                return false;
            }
        }
        return true;
    }
}
