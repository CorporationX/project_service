package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    public ProjectCreateResponseDto createProject(ProjectCreateRequestDto projectCreateRequestDto) {
        Long ownerId = projectCreateRequestDto.getOwnerId();
        String projectName = projectCreateRequestDto.getName();
        if (projectRepository.existsByOwnerIdAndName(ownerId, projectName)) {
            throw new DataValidationException("User " + ownerId + " already has a project with name " + projectName);
        }

        Project project = projectMapper.toProject(projectCreateRequestDto);

        project.setStatus(ProjectStatus.CREATED);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toCreateResponseDto(savedProject);
    }

    public ProjectUpdateResponseDto updateProject(ProjectUpdateRequestDto projectUpdateRequestDto) {
        Project project = projectRepository.findById(projectUpdateRequestDto.getId())
                .orElseThrow(NoSuchElementException::new);
        projectMapper.update(project, projectUpdateRequestDto);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toUpdateResponseDto(savedProject);
    }

    public List<ProjectResponseDto> getAllVisibleProjects(Long userId, ProjectFilterDto filters) {
        Stream<Project> projectStream = projectRepository.findAll()
                .stream();

        if (filters != null && CollectionUtils.isNotEmpty(projectFilters)) {
            for (ProjectFilter projectFilter : projectFilters) {
                if (projectFilter.isApplicable(filters)) {
                    projectStream = projectFilter.apply(projectStream, filters);
                }
            }
        }

        projectStream = projectStream.filter(project -> project.getVisibility() != ProjectVisibility.PRIVATE ||
                project.getOwnerId().equals(userId) ||
                projectRepository.isUserMemberOfProject(project.getId(), userId));

        return projectStream.map(projectMapper::toResponseDto)
                .toList();
    }

    public List<ProjectResponseDto> getAllProjects() {
        Stream<Project> projectStream = projectRepository.findAll()
                .stream();
        return projectStream.map(projectMapper::toResponseDto)
                .toList();
    }

    public ProjectResponseDto getProjectDtoById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Project not found"));
        return projectMapper.toResponseDto(project);
    }

    public void deleteProjectById(Long id) {
        projectRepository.deleteById(id);
    }

    public Project findEntityById(long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project no found by id: " + id));
    }

}
