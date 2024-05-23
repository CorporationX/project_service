package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;
    private final List<ProjectFilter> projectFilters;

    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        Project project = projectRepository.save(projectMapper.dtoToProject(projectDto));
        return projectMapper.projectToDto(project);
    }

    @Transactional
    public ProjectDto update(long projectId, ProjectDto projectDto) {
        projectValidator.isExists(projectId);
        Project project = projectRepository.save(projectMapper.dtoToProject(projectDto));
        return projectMapper.projectToDto(project);
    }

    @Transactional
    public Project findById(Long id) {
        return projectRepository.getProjectById(id);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getFilteredProject(ProjectFilterDto filters) {
        List<Project> projects = projectRepository.findAll();
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(projects.stream(), filters))
                .map(projectMapper::projectToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProject() {
        return projectMapper.projectsToDtos(projectRepository.findAll());
    }

    public void delete(long projectId) {
        projectRepository.delete(projectId);
    }
}
