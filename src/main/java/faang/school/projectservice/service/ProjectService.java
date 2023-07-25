package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataAlreadyExistingException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;

    public ProjectDto create(ProjectDto projectDto) {
        long ownerId = projectDto.getOwnerId();
        String projectName = projectDto.getName();
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, projectName)) {
            throw new DataAlreadyExistingException(String
                    .format("User conflict2 with id: %d already exist project %s",ownerId, projectName));
        }
        Project project = projectMapper.toModel(projectDto);
        LocalDateTime now = LocalDateTime.now();
        project.setCreatedAt(now);
        project.setUpdatedAt(now);
        project.setStatus(ProjectStatus.CREATED);
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }
}
