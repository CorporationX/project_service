package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.project.ResponseProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ResponseProjectMapper responseProjectMapper;
    private final List<ProjectFilter> projectFilters;

    @Transactional(readOnly = true)
    public List<ResponseProjectDto> getAllByFilter(ProjectFilterDto dto, long userId) {
        Stream<Project> projects = projectRepository.findAllByVisibilityOrOwnerId(ProjectVisibility.PUBLIC, userId).stream();

        for (ProjectFilter filter : projectFilters) {
            if (filter.isApplicable(dto)) {
                projects = filter.apply(projects, dto);
            }
        };

        return responseProjectMapper.toDtoList(projects.collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public List<ResponseProjectDto> getAll() {
        return responseProjectMapper.toDtoList(projectRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ResponseProjectDto getById(Long id) {
        return responseProjectMapper.toDto(projectRepository.getProjectById(id));
    }
}
