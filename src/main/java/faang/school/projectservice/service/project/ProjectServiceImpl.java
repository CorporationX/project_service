package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.ExceptionMessages;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;

    @Override
    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        var isDuplicateProject = projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName());
        if (isDuplicateProject) {
            throw new IllegalArgumentException(ExceptionMessages.PROJECT_ALREADY_EXISTS_FOR_OWNER_ID);
        }
        projectDto.setStatus(ProjectStatus.CREATED);
        return mapper.toDto(projectRepository.save(mapper.toEntity(projectDto)));
    }
}
