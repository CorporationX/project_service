package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.event.ProjectFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> filters;

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsByFilter(ProjectFilterDto filter) {
        Stream<Project> projects = StreamSupport.stream(projectRepository.findAll().spliterator(), false);
        return filterProjects(projects, filter);
    }


    public ProjectDto getProjectById(Long id) {
        return projectMapper.toDto(projectRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public ProjectDto save(ProjectDto project) {
        return projectMapper.toDto(projectRepository.save(projectMapper.toEntity(project)));
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    private List<ProjectDto> filterProjects(Stream<Project> projects, ProjectFilterDto filter) {
        filters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .forEach(eventFilter -> eventFilter.applyFilter(projects, filter));
        return projects
                .skip((long) filter.getPageSize() * filter.getPage())
                .limit(filter.getPageSize())
                .map(projectMapper::toDto)
                .toList();
    }
}
