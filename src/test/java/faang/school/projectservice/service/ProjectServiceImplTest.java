package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectFilterDto;
import faang.school.projectservice.dto.client.project.ProjectViewDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;

import faang.school.projectservice.service.filter.project.ProjectFilterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Тесты для проверки логики создания, обновления и получения проектов")
public class ProjectServiceImplTest {
    @Mock
    private ProjectRepository repository;

    @Mock
    private UserContext userContext;

    @Spy
    private ProjectMapperImpl mapper;

    @Spy
    private Project project;

    @Mock
    private ProjectFilterServiceImpl filterService;

    @InjectMocks
    private ProjectServiceImpl service;

    private ProjectFilterDto projectFilterDto;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Проверка создания проекта")
    void createProject() {
        ProjectCreateDto projectCreateDto = new ProjectCreateDto(
                "someProject",
                "someDescription",
                new BigInteger("4934823"),
                new BigInteger("654654645646546456"),
                1L,
                project,
                ProjectVisibility.PUBLIC,
                ProjectStatus.CREATED,
                "randomText",
                List.of("randomText")
        );

        when(userContext.getUserId()).thenReturn(1L);

        service.createProject(projectCreateDto);
        Project project = mapper.toEntity(projectCreateDto);

        verify(repository).save(refEq(project, "createdAt"));
    }

    @Test
    @DisplayName("Проверка обновления проекта")
    void updateProjectTest() {
        ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto(
                "someProject",
                "someDescription",
                new BigInteger("4934823"),
                new BigInteger("654654645646546456"),
                1L,
                project,
                ProjectVisibility.PUBLIC,
                ProjectStatus.CREATED,
                "randomText",
                List.of("randomText")
        );

        when(repository.findById(5L)).thenReturn(Optional.of(project));
        project.setVisibility(ProjectVisibility.PUBLIC);

        service.updateProject(5L, projectUpdateDto);
        verify(repository).findById(5L);
        verify(mapper).update(projectUpdateDto, project);
        verify(repository).save(project);
    }

    @Test
    @DisplayName("Проверка получения списка проектов, отфильтрованных по заданному статусу")
    void getProjectsFilteredByStatusTest() {
        projectFilterDto = new ProjectFilterDto(null, ProjectStatus.IN_PROGRESS);

        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setVisibility(ProjectVisibility.PUBLIC);
        ProjectViewDto projectViewDto = mapper.toViewDto(project);

        Project projectWithAnotherStatus = new Project();
        projectWithAnotherStatus.setStatus(ProjectStatus.ON_HOLD);
        projectWithAnotherStatus.setVisibility(ProjectVisibility.PUBLIC);

        when(repository.findAll()).thenReturn(List.of(project, projectWithAnotherStatus));
        when(filterService.getFilteredList(List.of(project, projectWithAnotherStatus), projectFilterDto))
                .thenReturn(List.of(project));

        assertEquals(List.of(projectViewDto), service.getByFilters(projectFilterDto));
    }

    @Test
    @DisplayName("Проверка получения списка проектов, отсортированных по названия в алфавитном порядке")
    void getProjectsFilteredByNameTest() {
        projectFilterDto = new ProjectFilterDto("some name", null);

        Project aNameProject = new Project();
        aNameProject.setName("A");
        aNameProject.setVisibility(ProjectVisibility.PUBLIC);
        ProjectViewDto aNameProjectViewDto = mapper.toViewDto(aNameProject);

        Project bNameProject = new Project();
        bNameProject.setName("B");
        bNameProject.setVisibility(ProjectVisibility.PUBLIC);
        ProjectViewDto bNameProjectViewDto = mapper.toViewDto(bNameProject);

        when(repository.findAll()).thenReturn(List.of(aNameProject, bNameProject));
        when(filterService.getFilteredList(List.of(aNameProject, bNameProject), projectFilterDto))
                .thenReturn(List.of(aNameProject, bNameProject));

        assertEquals(List.of(aNameProjectViewDto, bNameProjectViewDto), service.getByFilters(projectFilterDto));
    }

    @Test
    @DisplayName("Проверка получения списка всех проектов")
    void getAllProjectsTest() {
        project.setVisibility(ProjectVisibility.PUBLIC);
        ProjectViewDto projectViewDto = mapper.toViewDto(project);

        projectFilterDto = new ProjectFilterDto(null, null);

        when(repository.findAll()).thenReturn(List.of(project));
        when(filterService.getFilteredList(List.of(project), projectFilterDto)).thenReturn(List.of(project));

        assertEquals(List.of(projectViewDto), service.getByFilters(projectFilterDto));
    }

    @Test
    @DisplayName("Проверка получения проекта по его id")
    void getProjectByIdTest() {
        project.setVisibility(ProjectVisibility.PUBLIC);
        project.setId(5L);
        ProjectViewDto projectViewDto = mapper.toViewDto(project);

        when(repository.findById(5L)).thenReturn(Optional.of(project));

        assertEquals(projectViewDto, service.getProjectById(5L));
    }

    @Test
    @DisplayName("Проверка фильтрации доступа к проектам по их приватности")
    void visibilityFilterTest() {
        project.setVisibility(ProjectVisibility.PRIVATE);
        project.setId(5L);

        when(repository.findById(5L)).thenReturn(Optional.of(project));
        assertThrows(RuntimeException.class, () -> service.getProjectById(5L));
    }
}
