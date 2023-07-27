package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.mapper.project.UpdateProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UpdateProjectMapper updateProjectMapper;

    @Transactional
    public UpdateProjectDto update(UpdateProjectDto dto) {
        Project project = projectRepository.getProjectById(dto.getId());

        updateEntityFields(dto, project);

        project.setUpdatedAt(LocalDateTime.now());

        return updateProjectMapper.toDto(project);
    }

    private static void updateEntityFields(UpdateProjectDto dto, Project project) {
        if (dto.getDescription() != null) {
            project.setDescription(dto.getDescription());
        }

        if (dto.getStatus() != null) {
            project.setStatus(dto.getStatus());
        }
    }
}
