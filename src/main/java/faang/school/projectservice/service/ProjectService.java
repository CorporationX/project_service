package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static faang.school.projectservice.model.ProjectStatus.CANCELLED;
import static faang.school.projectservice.model.ProjectStatus.COMPLETED;
import static faang.school.projectservice.model.ProjectStatus.CREATED;
import static faang.school.projectservice.model.ProjectStatus.ON_HOLD;
import static faang.school.projectservice.model.ProjectVisibility.PRIVATE;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final MomentService momentService;

    @Transactional
    public ProjectDto createSubProject(Long parentId, CreateSubProjectDto subProjectDto) {
        Project parent = projectRepository.getProjectById(parentId);

        if (parent.getVisibility().equals(PRIVATE) &&
                subProjectDto.getVisibility().equals(PUBLIC)) {
            throw new IllegalArgumentException("Нельзя добавить публичный подпроект с приватному проекту");
        }

        Project projectToCreate = projectMapper.toModel(subProjectDto);

        if (parent.getChildren().isEmpty()) {
            parent.setChildren(new ArrayList<>());
        }
        parent.getChildren().add(projectToCreate);

        projectToCreate.setParentProject(parent);
        projectToCreate.setStatus(CREATED);
        projectRepository.save(projectToCreate);
        return projectMapper.toDto(projectToCreate);
    }

    @Transactional
    public ProjectDto updateSubProject(long projectId, ProjectDto projectDto) {
        Project projectToUpdate = projectRepository.getProjectById(projectId);
        Project parent = projectRepository.getProjectById(projectToUpdate.getParentProject().getId());

        if (!projectToUpdate.getVisibility().equals(projectDto.getVisibility())) {
            changeVisibility(parent, projectToUpdate, projectDto);
        }

        ProjectStatus updatedStatus = checkAndChangeStatus(projectToUpdate, projectDto);
        projectToUpdate.setStatus(updatedStatus);

        projectToUpdate.setName(projectDto.getName());
        projectToUpdate.setDescription(projectDto.getDescription());

        projectToUpdate.setUpdatedAt(LocalDateTime.now());

        if (updatedStatus.equals(COMPLETED) && isAllSubProjectsCompleted(parent)) {
            MomentDto momentDto = MomentDto.builder()
                    .title("Проект со всеми подзадачами выполенен")
                    .projectId(projectId)
                    .build();
            momentService.createMoment(momentDto);
        }
        return projectMapper.toDto(projectToUpdate);
    }

    private boolean isAllSubProjectsCompleted(Project parent) {
        return parent.getChildren().stream().allMatch(pr -> pr.getStatus().equals(COMPLETED));
    }

    private void changeVisibility(Project parent,Project projectToUpdate , ProjectDto projectDto) {

        if (projectDto.getVisibility().equals(PUBLIC) && parent.getVisibility().equals(PRIVATE)) {
            throw new IllegalArgumentException("Нельзя установить публичный статус подпроекта для приватного проекта");
        } else {
            projectToUpdate.setVisibility(projectDto.getVisibility());
            if (!projectToUpdate.getChildren().isEmpty()) {
                projectToUpdate.getChildren().forEach(pr -> pr.setVisibility(projectDto.getVisibility()));
            }
        }
    }

    private ProjectStatus checkAndChangeStatus(Project project, ProjectDto projectDto) {
        ProjectStatus updatedStatus = projectDto.getStatus();

        if (updatedStatus.equals(ON_HOLD) || updatedStatus.equals(CANCELLED)) {
            project.getChildren().forEach(pr -> pr.setStatus(updatedStatus));
            project.setStatus(updatedStatus);
        }

        return updatedStatus;
    }
}
