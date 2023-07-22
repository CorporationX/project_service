package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        validateOfExistingProjectFromUser(projectDto);

        Project project = projectMapper.toEntity(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        return saveEntityAndReturnDto(project);
    }

    private void validateOfExistingProjectFromUser(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getId(), projectDto.getName())) {
            throw new DataValidationException("The user has already created a project with this name");
        }
    }

    private ProjectDto saveEntityAndReturnDto(Project project) {
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }
}
