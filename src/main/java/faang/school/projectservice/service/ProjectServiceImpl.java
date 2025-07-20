package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectViewDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository repository;
    private final ProjectMapper mapper;
    private final UserContext userContext;

    @Override
    @Transactional
    public ProjectViewDto createProject(ProjectCreateDto projectDto) {
        boolean isHaveProjectWithSameName =
                repository.existsByOwnerIdAndName(userContext.getUserId(), projectDto.name());

        if (isHaveProjectWithSameName) {
            throw new RuntimeException("Пользователь пытается создать уже имеющийся у него проект");
        }

        Project project = mapper.toEntity(projectDto);

        project.setOwnerId(userContext.getUserId());
        project.setCreatedAt(LocalDateTime.now());
        log.info("Проект {} был создан.", projectDto.name());
        return mapper.toViewDto(repository.save(project));
    }

    @Override
    @Transactional
    public ProjectViewDto updateProject(long id, ProjectUpdateDto projectDto) {
        Project project = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.valueOf(id)));

        if (!visibilityFilter(project)) {
            throw new ForbiddenException("У пользователя нет доступа к указанному проекту");
        }

        project.setUpdatedAt(LocalDateTime.now());
        mapper.update(projectDto, project);
        repository.save(project);
        log.info("Проект с id = {} был обновлен входными данными", id);
        return mapper.toViewDto(repository.save(project));
    }

    @Override
    @Transactional
    public List<ProjectViewDto> getProjectsFilteredByStatus(ProjectViewDto projectViewDto) {
        List<Project> projectList = repository.findAll();
        log.info("Получение списка проектов, отфильтрованных по статусу {}",
                projectViewDto.status());
        return projectList.stream()
                .filter(this::visibilityFilter)
                .map(mapper::toViewDto)
                .filter(project -> project.status().equals(projectViewDto.status()))
                .toList();
    }

    @Override
    @Transactional
    public List<ProjectViewDto> getProjectsFilteredByName() {
        List<Project> projectList = repository.findAll();
        log.info("Получение списка проектов, отсортированных по имени");
        return projectList.stream()
                .filter(this::visibilityFilter)
                .map(mapper::toViewDto)
                .filter(projectDto -> projectDto.name() != null &&
                        !projectDto.name().isEmpty())
                .sorted(Comparator.comparing(ProjectViewDto::name))
                .toList();
    }

    @Override
    @Transactional
    public List<ProjectViewDto> getAllProjects() {
        List<Project> projectList = repository.findAll();
        log.info("Получение списка проектов");
        return projectList.stream()
                .filter(this::visibilityFilter)
                .map(mapper::toViewDto)
                .toList();
    }

    @Override
    public ProjectViewDto getProjectById(long id) {
        Optional<Project> project = repository.findById(id);
        if (project.isEmpty()) {
            throw new RuntimeException("Проекта с указанным айди не существует");
        } else if (!visibilityFilter(project.get())) {
            throw new RuntimeException("У пользователя нет доступа к указанному проекту");
        }
        log.info("Получение проекта по id = {}", id);
        return mapper.toViewDto(project.get());
    }

    private boolean visibilityFilter(Project project) {
        return project.getVisibility().equals(ProjectVisibility.PUBLIC) ||
                project.getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .findFirst()
                        .filter(teamMember -> teamMember.getId() == userContext.getUserId())
                        .isPresent();
    }
}
