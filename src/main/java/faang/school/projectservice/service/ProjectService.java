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
    public ProjectDto createProject(ProjectDto projectDto, long userId) {
        validationOfExistingProjectFromUser(projectDto, userId);
        Project project = projectMapper.toEntity(projectDto);

        project.setOwnerId(userId);
        project.setStatus(ProjectStatus.CREATED);
        project = projectRepository.save(project);

        return projectMapper.toDto(project);
    }

    private void validationOfExistingProjectFromUser(ProjectDto projectDto, long userId) {
        if (projectRepository.existsByOwnerUserIdAndName(userId, projectDto.getName())) {
            throw new DataValidationException("The user has already created a project with this name");
        }
    }
}
