package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.ProjectValidate;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectDto create(ProjectDto projectDto) {
        ProjectValidate.validateProjectDtoToCreate(projectDto);

        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new IllegalArgumentException(
                    "You already have project with name: " + projectDto.getName() +
                    ". Choose another name"
            );
        }

        projectDto.setStatus(ProjectStatus.CREATED);
        if (projectDto.getVisibility() == null) {
            projectDto.setVisibility(ProjectVisibility.PUBLIC);
        }

        Project savedProject = projectRepository.save(projectMapper.toEntity(projectDto));

        return projectMapper.toDto(savedProject);
    }
}