package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectFilterDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.dto.client.project.ProjectViewDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.FilterService;
import faang.school.projectservice.util.project.ProjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.LocalDateTime;
import java.util.List;

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
        Project project = repository.getByIdOrThrow(id);
        if (!ProjectUtil.isAvailable(project, userContext.getUserId())) {
            throw new RuntimeException("У пользователя нет доступа к указанному проекту");
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
        Project project = repository.getByIdOrThrow(id);
        if (!ProjectUtil.isAvailable(project, userContext.getUserId())) {
            throw new RuntimeException("У пользователя нет доступа к указанному проекту");
        }
        log.info("Получение проекта по id = {}", id);
        return mapper.toViewDto(project);
    }
}
