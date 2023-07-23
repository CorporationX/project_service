package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectDto createSubProject(CreateSubProjectDto createSubProjectDto) {
        validateParentProjectExist(createSubProjectDto);
        validateVisibilityConsistency(createSubProjectDto);
        validateSubProjectUnique(createSubProjectDto);

        ProjectDto projectDto = projectMapper.toProjectDto(createSubProjectDto);
        Project project = projectMapper.toEntity(projectDto);

        return projectMapper.toDto(projectRepository.save(project));
    }

    private void validateParentProjectExist(CreateSubProjectDto createSubProjectDto) {
        if (!projectRepository.existsById(createSubProjectDto.getParentId())) {
            throw new DataValidationException("No such parent project");
        }
    }

    private void validateVisibilityConsistency(CreateSubProjectDto createSubProjectDto) {
        Project parentProject = projectRepository.getProjectById(createSubProjectDto.getParentId());

        if (!createSubProjectDto.getVisibility().equals(parentProject.getVisibility())) {
            throw new DataValidationException("The visibility of the subproject must be - " +
                    parentProject.getVisibility() + " like the parent project");
        }
    }

    private void validateSubProjectUnique(CreateSubProjectDto createSubProjectDto) {
        Project parentProject = projectRepository.getProjectById(createSubProjectDto.getParentId());
        String subProjectName = createSubProjectDto.getName();
        boolean subProjectExists = parentProject.getChildren().stream().anyMatch(
                subProject -> subProject.getName().equals(subProjectName));

        if (subProjectExists) {
            throw new DataValidationException("Subproject with name " + subProjectName + " already exists");
        }
    }
}
