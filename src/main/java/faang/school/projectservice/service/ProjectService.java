package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final StageRepository stageRepository;

    public ProjectDto createSubProject(ProjectDto projectDto) {
        validateSubProject(projectDto);
        Project subProject = subProjectMapper.toEntity(projectDto);
        subProject.setChildren(projectRepository.findAllByIds(projectDto.getChildrenIds()));
        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        subProject.setCreatedAt(LocalDateTime.now());
        subProject.setUpdatedAt(LocalDateTime.now());
        List<Stage> stages = projectDto.getStagesId().stream()
                .map(stageRepository::getById)
                .toList();
        subProject.setStages(stages);
        parentProject.getChildren().add(subProject);
        projectRepository.save(subProject);
        projectRepository.save(parentProject);
        return subProjectMapper.toDto(subProject);
    }

    public List<ProjectDto> createSubProjects(List<ProjectDto> projectsDtos) {
        projectsDtos.forEach(this::validateSubProject);
        return projectsDtos.stream()
                .map(this::createSubProject)
                .toList();
    }

    public void validateSubProject(ProjectDto projectDto) {
        if (projectDto.getOwnerId() <= 0) {
            throw new DataValidationException("Owner id cant be less then 1");
        }
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException(String.format("Project %s is already exist", projectDto.getName()));
        }
        if (projectDto.getChildrenIds() == null) {
            throw new DataValidationException("Subprojects can be empty but not null");
        }
        if (projectDto.getVisibility() == null) {
            throw new DataValidationException(String.format("Visibility of subProject '%s' must be specified as 'private' or 'public'.", projectDto.getName()));
        }
        if (projectDto.getParentProjectId() <= 0) {
            throw new DataValidationException("ParentProjectId cant be less 0 or 0");
        }
        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
        if (parentProject == null) {
            throw new EntityNotFoundException(String.format("Project not found by id: %s", projectDto.getParentProjectId()));
        }
        if (parentProject.getVisibility().equals(ProjectVisibility.PUBLIC) && projectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new DataValidationException(String.format("Cant create private SubProject; %s, on a public Project: %s", projectDto.getName(), parentProject.getName()));
        }
    }
}
