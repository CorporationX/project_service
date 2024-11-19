package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.mapper.project.SubProjectMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubProjectServiceTest {
    @InjectMocks
    private SubProjectService subProjectService;

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private SubProjectMapperImpl subProjectMapper;
    @Mock
    private SubProjectServiceValidate projectValidator;
    @Mock
    private MomentService momentService;

    @Test
    void createSubProject() {
        CreateSubProjectDto subProjectDto = new CreateSubProjectDto();
        Project parentProject = Project.builder()
                .id(1L)
                .children(new ArrayList<>())
                .visibility(ProjectVisibility.PUBLIC).build();
        Project childProject = Project.builder().visibility(ProjectVisibility.PUBLIC).build();
        parentProject.getChildren().add(childProject);

        when(projectRepository.getProjectById(any())).thenReturn(parentProject);
        when(subProjectMapper.toEntity(any())).thenReturn(childProject);

        subProjectService.createSubProject(1L, subProjectDto);
        assertEquals(childProject.getParentProject(), parentProject);
        verify(projectRepository, times(1)).save(childProject);
        verify(projectRepository, times(1)).save(parentProject);
    }

    @Test
    void testCreateSubProjectDifferentVisibility() {
        CreateSubProjectDto subProjectDto = new CreateSubProjectDto();
        Project parentProject = Project.builder()
                .id(1L)
                .children(new ArrayList<>())
                .visibility(ProjectVisibility.PUBLIC).build();
        Project childProject = Project.builder().id(2L).visibility(ProjectVisibility.PRIVATE).build();
        parentProject.getChildren().add(childProject);

        when(projectRepository.getProjectById(any())).thenReturn(parentProject);
        when(subProjectMapper.toEntity(any())).thenReturn(childProject);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> subProjectService.createSubProject(1L, subProjectDto));
        assertTrue(exception.getMessage().contains("Sub project can't be private in public project"));
    }

    @Test
    void testUpdateProject() {
        CreateSubProjectDto parentDto = CreateSubProjectDto.builder()
                .id(1L)
                .name("New name")
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        Project firstChildProject = Project.builder()
                .id(3L)
                .name("FirstChild")
                .children(List.of())
                .status(ProjectStatus.COMPLETED)
                .moments(List.of(new Moment()))
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project secondChildProject = Project.builder()
                .id(4L)
                .name("SecondChild")
                .children(List.of())
                .status(ProjectStatus.COMPLETED)
                .moments(List.of(new Moment()))
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project parentProject = Project.builder()
                .id(1L)
                .name("Parent project")
                .parentProject(null)
                .children(List.of(firstChildProject, secondChildProject))
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        when(projectRepository.getProjectById(any())).thenReturn(parentProject);
        when(projectRepository.getSubProjectsByParentId(any())).thenReturn(parentProject.getChildren());
        when(projectRepository.save(parentProject)).thenReturn(parentProject);
        when(projectValidator.isVisibilityDtoAndProjectNotEquals(any(), any())).thenReturn(true);
        when(projectValidator.isStatusDtoAndProjectNotEquals(any(), any())).thenReturn(true);

        subProjectService.updateProject(1L, parentDto, 1L);
        verify(projectRepository, times(1)).save(parentProject);
        assertEquals(parentProject.getStatus(), parentDto.getStatus());
        assertEquals(parentProject.getVisibility(), parentDto.getVisibility());
        assertEquals(parentProject.getVisibility(), firstChildProject.getVisibility());
        assertEquals(parentProject.getVisibility(), secondChildProject.getVisibility());
    }

    @Test
    void testStatusHasChildProject() {
        CreateSubProjectDto parentDto = CreateSubProjectDto.builder()
                .id(1L)
                .name("New name")
                .status(ProjectStatus.COMPLETED)
                .build();
        Project firstChildProject = Project.builder()
                .id(3L)
                .name("FirstChild")
                .children(List.of())
                .status(ProjectStatus.COMPLETED)
                .moments(List.of(new Moment()))
                .build();
        Project secondChildProject = Project.builder()
                .id(4L)
                .name("SecondChild")
                .children(List.of())
                .status(ProjectStatus.COMPLETED)
                .moments(List.of(new Moment()))
                .build();
        Project parentProject = Project.builder()
                .id(1L)
                .name("Parent project")
                .parentProject(null)
                .children(List.of(firstChildProject, secondChildProject))
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        when(projectRepository.getProjectById(any())).thenReturn(parentProject);
        when(projectRepository.getSubProjectsByParentId(any())).thenReturn(parentProject.getChildren());
        when(projectValidator.isStatusDtoAndProjectNotEquals(any(), any())).thenReturn(true);

        subProjectService.updateProject(1L, parentDto, 1L);
        assertEquals(parentDto.getStatus(), parentProject.getStatus());
    }

    @Test
    void testStatusDoesntHaveChildProject() {
        CreateSubProjectDto parentDto = CreateSubProjectDto.builder()
                .id(1L)
                .name("New name")
                .status(ProjectStatus.COMPLETED)
                .build();
        Project parentProject = Project.builder()
                .id(1L)
                .name("Parent project")
                .parentProject(null)
                .children(List.of())
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        when(projectRepository.getProjectById(any())).thenReturn(parentProject);
        when(projectRepository.getSubProjectsByParentId(any())).thenReturn(parentProject.getChildren());
        when(projectValidator.isStatusDtoAndProjectNotEquals(any(), any())).thenReturn(true);

        subProjectService.updateProject(1L, parentDto, 1L);
        assertEquals(parentDto.getStatus(), parentProject.getStatus());
    }

    @Test
    void testStatusNotCompletedSubProjects() {
        CreateSubProjectDto parentDto = CreateSubProjectDto.builder()
                .id(1L)
                .name("New name")
                .parentProjectId(1L)
                .childrenIds(List.of(3L, 4L))
                .status(ProjectStatus.COMPLETED)
                .build();
        Project firstChildProject = Project.builder()
                .id(3L)
                .name("FirstChild")
                .children(List.of())
                .status(ProjectStatus.COMPLETED)
                .moments(List.of(new Moment()))
                .build();
        Project secondChildProject = Project.builder()
                .id(4L)
                .name("SecondChild")
                .children(List.of())
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        Project parentProject = Project.builder()
                .id(2L)
                .name("Parent project")
                .parentProject(null)
                .children(List.of(firstChildProject, secondChildProject))
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        when(projectRepository.getProjectById(any())).thenReturn(parentProject);
        when(projectRepository.getSubProjectsByParentId(any())).thenReturn(parentProject.getChildren());
        when(projectValidator.isStatusDtoAndProjectNotEquals(any(), any())).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> subProjectService.updateProject(2L, parentDto, 1L));
        assertTrue(exception.getMessage().contains("Current project has unfinished subprojects"));
    }

    @Test
    public void testGetProjectByFiltersSuccess() {
        SubProjectFilter<FilterSubProjectDto, Project> mockSubProjectFilter = mock(SubProjectFilter.class);
        SubProjectFilter<FilterSubProjectDto, Project> mockSubProjectFilter2 = mock(SubProjectFilter.class);
        List<SubProjectFilter<FilterSubProjectDto, Project>> subProjectFilters = List.of(mockSubProjectFilter, mockSubProjectFilter2);
        subProjectService = new SubProjectService(projectRepository, subProjectMapper, momentService, projectValidator, subProjectFilters);

        CreateSubProjectDto projectDto = CreateSubProjectDto.builder().name("Second project").build();
        Long id = 1L;

        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().name("project").status(ProjectStatus.COMPLETED).build();
        List<Project> projects = List.of(
                Project.builder().name("First").status(ProjectStatus.COMPLETED).build(),
                Project.builder().name("Second project").status(ProjectStatus.COMPLETED).build(),
                Project.builder().name("Third project").status(ProjectStatus.COMPLETED).build()
        );
        Stream<Project> projectStream = projects.subList(1, projects.size()).stream();

        when(projectRepository.getSubProjectsByParentId(any())).thenReturn(projects);
        when(subProjectFilters.get(0).isApplicable(any())).thenReturn(true);
        when(subProjectFilters.get(0).apply(any(), any())).thenReturn(projectStream);

        when(subProjectMapper.toDto(any())).thenReturn(projectDto);

        List<CreateSubProjectDto> resultProjects = subProjectService.getProjectsByFilter(id, filterDto);
        assertTrue(resultProjects.get(0).getName().contains(projectDto.getName()));
        assertEquals(2, resultProjects.size());
    }
}