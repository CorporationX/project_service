package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.project.ProjectFilterDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<Filter<Project, ProjectFilterDto>> projectFilters;

    @Override
    public ProjectDto createProject(ProjectDto dto) {
        if (projectRepository.existsByOwnerIdAndName(dto.getOwnerId(), dto.getName())) {
            throw new IllegalArgumentException(String.format(
                    "User with id = %d has already project with name = %s", dto.getOwnerId(), dto.getName()
            ));
        }
        Project project = buildProject(dto);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public ProjectDto updateProject(long id, ProjectDto dto) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Project with id = %d not found", id
        )));
        Project updatedProject = projectMapper.update(dto, project);
        updatedProject.setUpdatedAt(LocalDateTime.now());
        return projectMapper.toDto(projectRepository.save(updatedProject));
    }

    @Override
    public List<ProjectDto> getAllProjects(int pageNumber, int pageSize, ProjectFilterDto filters) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Project> projects = projectRepository.findAll(pageable).toList();
        for (var filter : projectFilters) {
            if (filter.isApplicable(filters)) {
                projects = filter.apply(projects, filters);
            }
        }
        return projects.stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    public ProjectDto getProjectById(long id) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Project with id = %d not found", id
        )));
        return projectMapper.toDto(project);
    }

    private Project buildProject(ProjectDto dto) {
        Project project = projectMapper.toEntity(dto);
        project.setStatus(ProjectStatus.CREATED);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return project;
    }
}
