package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.CreateSubProjectMapper;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;
    private final CreateSubProjectMapper subProjectMapper;
    private final ProjectMapper projectMapper;

    public ProjectDto create(CreateSubProjectDto subProjectDto) {
        Project parentProject = validateAndRetrieveParentProject(subProjectDto);
        Project subProject = subProjectMapper.toEntity(subProjectDto);
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        Project savedSubProject = projectRepository.save(subProject);

        if (!subProjectDto.getStages().isEmpty()) {
            List<Stage> stages = subProjectDto.getStages().stream()
                    .map(stageName -> {
                        Stage stage = new Stage();
                        stage.setProject(savedSubProject);
                        stage.setStageName(stageName);
                        return stageRepository.save(stage);
                    })
                    .toList();
            savedSubProject.setStages(stages);
        }

        if (!subProjectDto.getChildren().isEmpty()) {
            List<Project> children = subProjectDto.getChildren().stream()
                    .peek(child -> child.setParentProject(savedSubProject.getId()))
                    .map(child -> projectMapper.toEntity(create(child)))
                    .toList();
            savedSubProject.setChildren(children);
        }

        return projectMapper.toDto(projectRepository.save(savedSubProject));
    }

    private Project validateAndRetrieveParentProject(CreateSubProjectDto subProjectDto) {
        if (subProjectDto.getParentProject() == null) {
            throw new IllegalArgumentException("Parent project is required");
        }
        var parentId = subProjectDto.getParentProject();
        Project parentProject = projectRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("Parent project with id = " + parentId + " doesn't exist"));

        if (parentProject.getVisibility() == ProjectVisibility.PRIVATE
                && subProjectDto.getVisibility() == ProjectVisibility.PUBLIC) {
            throw new IllegalArgumentException("Project " + subProjectDto.getName() + " cannot be public");
        }

        return parentProject;
    }
}
