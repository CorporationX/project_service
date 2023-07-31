package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.mapper.project.CreateProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CreateProjectMapper createProjectMapper;

    @Transactional
    public ResponseProjectDto create(CreateProjectDto dto, long userId) {
        Project project = createProjectMapper.toEntity(dto);

        validateCreateDtoAndMap(dto, project, userId);

        project.setCreatedAt(LocalDateTime.now());
        project.setStatus(ProjectStatus.CREATED);

        if (dto.getParentProjectId() != null) {
            project.setParentProject(projectRepository.getProjectById(dto.getParentProjectId()));
        }

        if (dto.getChildrenIds() != null && !dto.getChildrenIds().isEmpty()) {
            project.setChildren(projectRepository.findAllByIds(dto.getChildrenIds()));
        }

        return createProjectMapper.toDto(projectRepository.save(project));
    }

    private void validateCreateDtoAndMap(CreateProjectDto dto, Project project, long userId) {
        if (projectRepository.existsByOwnerUserIdAndName(userId, dto.getName())) {
            throw new IllegalArgumentException("User with id " + userId + " already has a project with name " + dto.getName());
        }

        project.setOwnerId(userId);
    }
}
