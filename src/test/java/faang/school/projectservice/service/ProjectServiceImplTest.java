package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.project.CreateProjectDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.UpdateProjectDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
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

    @InjectMocks
    private ProjectServiceImpl service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Проверка создания проекта")
    void createProject() {
        CreateProjectDto createProjectDto = new CreateProjectDto(
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

        service.createProject(createProjectDto);
        Project project = mapper.toProject(createProjectDto);

        verify(repository).save(refEq(project, "createdAt"));
    }

    @Test
    @DisplayName("Проверка обновления проекта")
    void updateProjectTest() {
        UpdateProjectDto updateProjectDto = new UpdateProjectDto(
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

        service.updateProject(5L, updateProjectDto);
        verify(repository).findById(5L);
        verify(mapper).update(updateProjectDto, project);
        verify(repository).save(project);
    }


    @Test
    @DisplayName("Проверка получения списка проектов, отсортированных по заданному статусу")
    void getProjectsFilteredByStatusTest() {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setVisibility(ProjectVisibility.PUBLIC);
        ProjectDto projectDto = mapper.toProjectDto(project);

        Project projectWithInappropriateStatus = new Project();
        projectWithInappropriateStatus.setStatus(ProjectStatus.ON_HOLD);
        projectWithInappropriateStatus.setVisibility(ProjectVisibility.PUBLIC);

        when(repository.findAll()).thenReturn(List.of(project, projectWithInappropriateStatus));

        assertEquals(List.of(projectDto), service.getProjectsFilteredByStatus(projectDto));
    }

    @Test
    @DisplayName("Проверка получения списка проектов, отсортированных по названия в алфавитном порядке")
    void getProjectsFilteredByNameTest() {
        Project aNameProject = new Project();
        aNameProject.setName("A");
        aNameProject.setVisibility(ProjectVisibility.PUBLIC);
        ProjectDto aNameProjectDto = mapper.toProjectDto(aNameProject);

        Project bNameProject = new Project();
        bNameProject.setName("B");
        bNameProject.setVisibility(ProjectVisibility.PUBLIC);
        ProjectDto bNameProjectDto = mapper.toProjectDto(bNameProject);

        when(repository.findAll()).thenReturn(List.of(aNameProject, bNameProject));

        assertEquals(List.of(aNameProjectDto, bNameProjectDto), service.getProjectsFilteredByName());
    }

    @Test
    @DisplayName("Проверка получения списка всех проектов")
    void getAllProjectsTest() {
        project.setVisibility(ProjectVisibility.PUBLIC);
        ProjectDto projectDto = mapper.toProjectDto(project);

        when(repository.findAll()).thenReturn(List.of(project));

        assertEquals(List.of(projectDto), service.getAllProjects());
    }

    @Test
    @DisplayName("Проверка получения проекта по его id")
    void getProjectByIdTest() {
        project.setVisibility(ProjectVisibility.PUBLIC);
        project.setId(5L);
        ProjectDto projectDto = mapper.toProjectDto(project);

        when(repository.findById(5L)).thenReturn(Optional.of(project));

        assertEquals(projectDto, service.getProjectById(5L));
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
