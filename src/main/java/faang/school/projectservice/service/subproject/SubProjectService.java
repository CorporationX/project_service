package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.StatusSubprojectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubProjectService {
    private final ProjectRepository projectRepository;
    private final MomentService momentService;
    private final ProjectMapper projectMapper;

    public long updateStatusSubProject(StatusSubprojectUpdateDto statusSubprojectUpdateDto) {
        Project project = getProjectById(statusSubprojectUpdateDto.getId());
        ProjectStatus status = statusSubprojectUpdateDto.getStatus();

        validateSubProjectStatus(project,status);

        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());

        return momentService.createMoment(projectMapper.toProjectDto(project));
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

    private void validateSubProjectStatus(Project project, ProjectStatus status){
        if (status == ProjectStatus.COMPLETED && project.getChildren() != null) {
            if (!checkStatusChildren(project.getChildren())) {
                throw new DataValidationException("You can make the project completed only after finishing all subprojects");
            }
        }
    }
}
