package faang.school.projectservice.serviсe;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectUpDateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j // для логирования
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;
    private final UserContext userContext;
    private final ProjectValidator projectValidator;

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("Preject " + projectDto.getName() + " already exist");
        }
        projectDto.setOwnerId(userContext.getUserId());
        log.info("Project creation started {}", projectDto.getName());
        var project = projectMapper.toEntity(projectDto);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto updateProject(Long id, ProjectUpDateDto projectUpDateDto) {
        var projectToUpdate = projectRepository.getProjectById(id);
        projectValidator.validateServiceOwnerOfProject(userContext.getUserId(), projectToUpdate);
        projectRepository.save(projectMapper.update(projectToUpdate, projectUpDateDto));
        log.info("Project updated {}", projectToUpdate.getName());
        return projectMapper.toDto(projectToUpdate);
    }

    @Transactional(readOnly = true) //консистентность данных, атомарность.
    public List<ProjectDto> getAllProjectsWithFilter(ProjectFilterDto projectFilterDto) {
        List<Project> projects = projectRepository.findAll();
        return projectFilters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
                .flatMap(projectFilter -> {
                    log.info("Applicable filter {}", projectFilter.getClass().getSimpleName());
                    // Создаем новый стрим перед каждым использованием фильтра, потому что иначе НЕ РАБОТААААЛОООО
                    Stream<Project> projectsStream = projects.stream()
                            .map(project -> projectValidator.validateServiceGetProject(userContext.getUserId(), project))
                            .filter(project -> project != null);
                    //^это нужно потому что валидация может вернуть null, если проект приватный и недоступен для запросившего пользователя. В таком случае все ломается.
                    log.info("Projects started filtering");
                    return projectFilter.applyFilter(projectFilterDto, projectsStream);
                })
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

    @Transactional
    public void deleteProjectById(Long id) {
        var project = projectRepository.getProjectById(id);
        projectValidator.validateServiceOwnerOfProject(userContext.getUserId(), project);
        projectRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ProjectDto getProjectById(Long id) {
        var project = projectRepository.getProjectById(id);
        return projectMapper.toDto(projectValidator.validateServiceGetProject(userContext.getUserId(), project));
    }
}
