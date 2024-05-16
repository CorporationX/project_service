package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;
    private final ProjectValidator validator;
    private final List<ProjectFilter> filters;

    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        validator.verifyCanBeCreated(projectDto);

        projectDto.setStatus(ProjectStatus.CREATED);
        Project saved = projectRepository.save(mapper.fromDto(projectDto));

        return mapper.toDto(saved);
    }

    @Transactional
    public ProjectDto update(ProjectDto projectDto) {
        validator.verifyCanBeUpdated(projectDto);

        Project saved = projectRepository.save(mapper.fromDto(projectDto));

        return mapper.toDto(saved);
    }

    public ProjectDto getById(Long id) {
        Project project = projectRepository.getProjectById(id);
        return mapper.toDto(project);
    }

    public List<ProjectDto> getAll() {
        return mapper.toDto(projectRepository.findAll());
    }

    public List<ProjectDto> search(ProjectFilterDto filter) {
        List<Project> projects = projectRepository.findAll();
        return filters.stream()
                .filter(streamFilter -> streamFilter.isApplicable(filter))
                .flatMap(streamFilter -> streamFilter.apply(projects.stream(), filter))
                .distinct()
                .map(mapper::toDto)
                .toList();
    }
}
