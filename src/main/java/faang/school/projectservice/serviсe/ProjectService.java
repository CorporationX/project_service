package faang.school.projectservice.serviсe;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectUpDateDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;
    private final UserContext userContext;
    private final ProjectValidator projectValidator;
    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectDto.getOwnerId() == null) {
            projectDto.setOwnerId(userContext.getUserId());
        }
        var project = projectMapper.toEntity(projectDto);
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Transactional
    public ProjectDto updateProject(Long id, ProjectUpDateDto projectUpDateDto) {
        var projectToUpdate = projectRepository.getProjectById(id);
        projectValidator.validateServiceOwnerOfProject(userContext.getUserId(), projectToUpdate);
        var projectMapperEntity = projectMapper.toEntity(projectUpDateDto);
        if (projectMapperEntity.getStatus() != null) {
            projectToUpdate.setStatus(projectMapperEntity.getStatus());
        }
        if (projectMapperEntity.getDescription() != null) {
            projectToUpdate.setDescription(projectMapperEntity.getDescription());
        }
        projectRepository.save(projectToUpdate);
        return projectMapper.toDto(projectToUpdate);
    }

    @Transactional(readOnly = true) //консистентность данных, атомарность.
    public List<ProjectDto> getAllProjectsWithFilter(ProjectFilterDto projectFilterDto) {
        List<Project> projects = projectRepository.findAll();
        System.err.println(projectFilters.size());
        return projectFilters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
                .flatMap(projectFilter -> {
                    // Создаем новый стрим перед каждым использованием,потому что иначе НЕ РАБОТАЕЕЕТ
                    Stream<Project> projectsStream = projects.stream()
                            .map(project -> projectValidator.validateServiceGetProject(userContext.getUserId(), project))
                            .filter(project -> project != null);
                    //^это нужно потому что валидация может вернуть null, если проект приватный. И тогда все ломается.
                    return projectFilter.applyFilter(projectFilterDto, projectsStream);
                })
                .filter(Objects::nonNull)  // Фильтр на null
                .map(projectMapper::toDto)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(project -> projectValidator.validateServiceGetProject(userContext.getUserId(), project))
                .map(projectMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectDto getProjectById(Long id) {
        var project = projectRepository.getProjectById(id);
        return projectMapper.toDto(projectValidator.validateServiceGetProject(userContext.getUserId(), project));
    }
}
