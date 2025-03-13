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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
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

        if (subProjectDto.getStages()!=null && !subProjectDto.getStages().isEmpty()) {
            List<Stage> stages = new ArrayList<>();
            for (String stageName : subProjectDto.getStages()) {
                Stage stage = new Stage();
                stage.setProject(savedSubProject);
                stage.setStageName(stageName);
                stages.add(stageRepository.save(stage));
            }
            savedSubProject.setStages(stages);
        }

        if (subProjectDto.getChildren()!=null && !subProjectDto.getChildren().isEmpty()) {
            List<Project> children = new ArrayList<>();
            for (CreateSubProjectDto child : subProjectDto.getChildren()) {
                child.setParentProject(savedSubProject.getId());
                children.add(projectMapper.toEntity(create(child)));
            }
            savedSubProject.setChildren(children);
        }

        return projectMapper.toDto(projectRepository.save(savedSubProject));
    }

    private Project validateAndRetrieveParentProject(CreateSubProjectDto subProjectDto) {
        if (subProjectDto.getParentProject() == null) {
            throw new IllegalArgumentException("Parent project is required");
        }
        if (subProjectDto.getVisibility() == null) {
            throw new IllegalArgumentException("Visibility project is required");
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
