package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectFilterDto;
import faang.school.projectservice.dto.client.project.ProjectViewDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.FilterService;
import faang.school.projectservice.util.project.ProjectUtil;
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
    private final FilterService<Project, ProjectFilterDto> filterService;

    @Override
    @Transactional
    public ProjectViewDto createProject(ProjectCreateDto projectDto) {
        boolean isHaveProjectWithSameName =
                repository.existsByOwnerIdAndName(userContext.getUserId(), projectDto.name());

        if (isHaveProjectWithSameName) {
            throw new DataValidationException("Пользователь пытается создать уже имеющийся у него проект");
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

        if (!ProjectUtil.isAvailable(project, userContext.getUserId())) {
            throw new ForbiddenException("У пользователя нет доступа к указанному проекту");
        }

        project.setUpdatedAt(LocalDateTime.now());
        mapper.update(projectDto, project);
        log.info("Проект с id = {} был обновлен входными данными", id);
        return mapper.toViewDto(repository.save(project));
    }

    @Override
    @Transactional
    public List<ProjectViewDto> getByFilters(ProjectFilterDto projectFilterDto) {
        List<Project> projectList = repository.findAll();
        List<Project> filteredList = filterService.getFilteredList(projectList, projectFilterDto);

        log.info("Получения списка всех проектов с фильтрами");
        return filteredList.stream()
                .map(mapper::toViewDto)
                .toList();
    }

    @Override
    @Transactional
    public ProjectViewDto getProjectById(long id) {
        Optional<Project> project = repository.findById(id);
        if (project.isEmpty()) {
            throw new EntityNotFoundException("Проекта с указанным айди не существует");
        } else if (!ProjectUtil.isAvailable(project.get(), userContext.getUserId())) {
            throw new ForbiddenException("У пользователя нет доступа к указанному проекту");
        }
        log.info("Получение проекта по id = {}", id);
        return mapper.toViewDto(project.get());
    }

}
