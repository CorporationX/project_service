package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.project.CreateProjectDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.UpdateProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
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
    public void createProject(CreateProjectDto projectDto) {
        Optional<Project> isHaveProjectWithSameName =
                getAllProjects().stream()
                .map(mapper::toProject)
                .filter(project -> project.getOwnerId().equals(userContext.getUserId()) &&
                        project.getName().equals(projectDto.name()))
                .findAny();

        if (isHaveProjectWithSameName.isPresent()) {
            throw new RuntimeException("Пользователь пытается создать уже " +
                    "имеющийся у него проект");
        }

        Project project = mapper.toProject(projectDto);

        project.setOwnerId(userContext.getUserId());
        project.setCreatedAt(LocalDateTime.now());
        repository.save(project);
        log.info("Проект {} был создан.", projectDto.name());
    }

    @Override
    @Transactional
    public void updateProject(long id, UpdateProjectDto projectDto) {
        Optional<Project> project = repository.findById(id);
        if (project.isPresent()) {
            project.get().setUpdatedAt(LocalDateTime.now());
            mapper.update(projectDto, project.get());
            repository.save(project.get());
        } else if (!visibilityFilter(project.get())) {
            throw new RuntimeException("У пользователя нет доступа к указанному проекту");
        } else {
            throw new RuntimeException("Пользователь пытается обновить несуществующий проект.");
        }
        log.info("Проект с id = {} был обновлен входными данными", id);
    }

    @Override
    @Transactional
    public List<ProjectDto> getProjectsFilteredByStatus(ProjectDto projectDto) {
        List<Project> projectList = repository.findAll();
        log.info("Получение списка проектов, отсортированных по статусу {}",
                projectDto.status());
        return projectList.stream()
                .filter(this::visibilityFilter)
                .map(mapper::toProjectDto)
                .filter(project -> project.status().equals(projectDto.status()))
                .toList();
    }

    @Override
    @Transactional
    public List<ProjectDto> getProjectsFilteredByName() {
        List<Project> projectList = repository.findAll();
        log.info("Получение списка проектов, отсортированных по имени");
        return projectList.stream()
                .filter(this::visibilityFilter)
                .map(mapper::toProjectDto)
                .filter(projectDto -> projectDto.name() != null &&
                        !projectDto.name().isEmpty())
                .sorted(Comparator.comparing(ProjectDto::name))
                .toList();
    }

    @Override
    @Transactional
    public List<ProjectDto> getAllProjects() {
        List<Project> projectList = repository.findAll();
        log.info("Получение списка проектов");
        return projectList.stream()
                .filter(this::visibilityFilter)
                .map(mapper::toProjectDto)
                .toList();
    }

    @Override
    public ProjectDto getProjectById(long id) {
        Optional<Project> project = repository.findById(id);
        if (project.isEmpty()) {
            throw new RuntimeException("Проекта с указанным айди не существует");
        } else if (!visibilityFilter(project.get())) {
            throw new RuntimeException("У пользователя нет доступа к указанному проекту");
        }
        log.info("Получение проекта по id = {}", id);
        return mapper.toProjectDto(project.get());
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
