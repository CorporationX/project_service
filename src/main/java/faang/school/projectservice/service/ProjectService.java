package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;

    public ProjectDto create(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwner().getUserId(), projectDto.getName())) {
            throw new DataValidationException("This project already exist");
        }

        projectDto.setStatus(ProjectStatus.CREATED);
        projectDto.setCreatedAt(LocalDateTime.now());
        projectDto.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(projectRepository.save(mapper.toEntity(projectDto)));
    }

    public ProjectDto updateStatusAndDescription(ProjectDto projectDto, Long id) {
        Project projectById = projectRepository.getProjectById(id);
        projectById.setStatus(projectDto.getStatus());
        projectById.setDescription(projectDto.getDescription());
        projectDto.setUpdatedAt(LocalDateTime.now());

        return mapper.toDto(projectRepository.save(mapper.toEntity(projectDto)));
    }

}
