package faang.school.projectservice.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.subProject.*;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.mapper.project.SubProjectMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.MomentService;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.project.ProjectValidateService;
import faang.school.projectservice.service.utilservice.ProjectUtilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectMapperImpl projectMapper;
    @Mock
    private SubProjectMapperImpl subProjectMapper;
    @Mock
    private List<Filter<FilterProjectDto, Project>> filters = new ArrayList<>();
    @Mock
    private ProjectUtilService projectUtilService;
    @Mock
    private ProjectValidateService projectValidateService;
    @Mock
    private MomentService momentService;

    @InjectMocks
    private ProjectService service;

    @BeforeEach
    public void init() {
        Filter<FilterProjectDto, Project> createdAtFilter = mock(CreatedAtFilter.class);
        Filter<FilterProjectDto, Project> nameFilter = mock(NameFilter.class);
        Filter<FilterProjectDto, Project> statusFilter = mock(StatusFilter.class);
        Filter<FilterProjectDto, Project> updatedAtFilter = mock(UpdatedAtFilter.class);
        Filter<FilterProjectDto, Project> visibilityFilter = mock(VisibilityFilter.class);
        filters = List.of(createdAtFilter, nameFilter, statusFilter, updatedAtFilter, visibilityFilter);
    }

    @Test
    public void testCreateProject() {
        ProjectDto projectDto = new ProjectDto();
        Project project = new Project();
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        service.createProject(projectDto);
        verify(projectUtilService, times(1)).save(project);
    }

    @Test
    public void testCreateSubProject() {
        CreateSubProjectDto projectDto = new CreateSubProjectDto();
        Project parentProject = new Project();
        Project childProject = new Project();
        List<Project> childrenList = new ArrayList<>();
        parentProject.setId(1L);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        childProject.setVisibility(ProjectVisibility.PUBLIC);
        parentProject.setChildren(childrenList);
        when(projectUtilService.getProjectById(any())).thenReturn(parentProject);
        when(subProjectMapper.toEntity(any())).thenReturn(childProject);
        service.createSubProject(1L, projectDto);
        assertEquals(childProject.getParentProject(), parentProject);
        verify(projectUtilService, times(1)).saveAllProjects(List.of(parentProject, childProject));
    }

    @Test
    public void testCreateSubProjectDifferentVisibility() {
        CreateSubProjectDto projectDto = new CreateSubProjectDto();
        Project parentProject = new Project();
        Project childProject = new Project();
        List<Project> childrenList = new ArrayList<>();
        parentProject.setId(1L);
        parentProject.setChildren(childrenList);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        childProject.setVisibility(ProjectVisibility.PRIVATE);
        when(projectUtilService.getProjectById(any())).thenReturn(parentProject);
        when(subProjectMapper.toEntity(any())).thenReturn(childProject);
        assertThrows(DataValidationException.class, () -> service.createSubProject(1L, projectDto));
    }

    @Test
    public void testUpdateProject() {
        CreateSubProjectDto parentDto = CreateSubProjectDto.builder()
                .id(1L)
                .name("UpdatedName")
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        Project firstChildProject = Project.builder()
                .id(3L)
                .name("firstChild")
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
                .visibility(ProjectVisibility.PUBLIC)
                .moments(List.of(new Moment()))
                .build();
        Project parentProject = Project.builder()
                .id(1L)
                .name("Test")
                .parentProject(null)
                .children(List.of(firstChildProject, secondChildProject))
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        when(projectUtilService.getProjectById(any())).thenReturn(parentProject);
        when(projectUtilService.getAllSubprojectsToProjectById(any())).thenReturn(parentProject.getChildren());
        when(projectUtilService.save(parentProject)).thenReturn(parentProject);
        when(projectValidateService.dtoParentProjectIdDoesNotMatchProjectIdAndNotNull(any(), any())).thenReturn(true);
        when(projectValidateService.visibilityDtoAndProjectDoesNotMatches(any(), any())).thenReturn(true);
        when(projectValidateService.statusDtoAndProjectDoesNotMatches(any(), any())).thenReturn(true);
        service.updateProject(1L, parentDto, 1L);
        verify(projectUtilService, times(1)).save(parentProject);
        assertEquals(parentProject.getStatus(), parentDto.getStatus());
        assertEquals(parentProject.getVisibility(), parentDto.getVisibility());
        assertEquals(parentProject.getVisibility(), firstChildProject.getVisibility());
        assertEquals(parentProject.getVisibility(), secondChildProject.getVisibility());
    }

    @Test
    public void testChangeParentProjectExists() {
        CreateSubProjectDto parentDto = CreateSubProjectDto.builder()
                .id(1L)
                .name("Test")
                .parentProjectId(6L).build();
        Project firstChildProject = Project.builder()
                .id(3L)
                .name("firstChild")
                .children(List.of())
                .build();
        Project secondChildProject = Project.builder()
                .id(4L)
                .name("SecondChild")
                .children(List.of())
                .build();
        Project firstBaseProject = Project.builder()
                .id(2L)
                .name("FirstBaseProject")
                .build();
        Project secondBaseProject = Project.builder()
                .id(6L)
                .name("SecondBaseProject")
                .build();
        Project parentProject = Project.builder()
                .id(1L)
                .name("Test")
                .parentProject(firstBaseProject)
                .children(List.of(firstChildProject, secondChildProject))
                .build();
        firstBaseProject.setChildren(List.of(parentProject));
        when(projectUtilService.getProjectById(1L)).thenReturn(parentProject);
        when(projectUtilService.getAllSubprojectsToProjectById(any())).thenReturn(parentProject.getChildren());
        when(projectUtilService.getProjectById(parentDto.getParentProjectId())).thenReturn(secondBaseProject);
        when(projectValidateService.dtoParentProjectIdDoesNotMatchProjectIdAndNotNull(any(), any())).thenReturn(true);
        service.updateProject(1L, parentDto, 1L);
        assertEquals(secondBaseProject.getChildren().get(0), parentProject);
        assertEquals(firstBaseProject.getChildren().size(), 0);
    }

    @Test
    public void testStatusHasChildProject() {
        CreateSubProjectDto parentDto = CreateSubProjectDto.builder()
                .id(1L)
                .name("Test")
                .parentProjectId(1L)
                .childrenIds(List.of(3L, 4L))
                .status(ProjectStatus.COMPLETED)
                .build();

        Project firstChildProject = Project.builder()
                .id(3L)
                .name("firstChild")
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
                .name("Test")
                .parentProject(null)
                .children(List.of(firstChildProject, secondChildProject))
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        when(projectUtilService.getProjectById(any())).thenReturn(parentProject);
        when(projectUtilService.getAllSubprojectsToProjectById(any())).thenReturn(parentProject.getChildren());
        when(projectValidateService.statusDtoAndProjectDoesNotMatches(any(), any())).thenReturn(true);
        service.updateProject(1L, parentDto, 1L);
        assertEquals(parentDto.getStatus(), parentProject.getStatus());
    }

    @Test
    public void testStatusDoesntHaveChildProject() {
        CreateSubProjectDto parentDto = CreateSubProjectDto.builder()
                .id(1L)
                .name("Test")
                .parentProjectId(1L)
                .childrenIds(List.of(3L, 4L))
                .status(ProjectStatus.COMPLETED)
                .build();
        Project parentProject = Project.builder()
                .id(1L)
                .name("Test")
                .parentProject(null)
                .children(List.of())
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        when(projectUtilService.getProjectById(any())).thenReturn(parentProject);
        when(projectUtilService.getAllSubprojectsToProjectById(any())).thenReturn(parentProject.getChildren());
        when(projectValidateService.statusDtoAndProjectDoesNotMatches(any(), any())).thenReturn(true);
        service.updateProject(1L, parentDto, 1L);
        assertEquals(parentDto.getStatus(), parentProject.getStatus());
    }

    @Test
    public void testStatusUnfinishedSubprojects() {
        CreateSubProjectDto parentDto = CreateSubProjectDto.builder()
                .id(2L)
                .name("Test")
                .parentProjectId(1L)
                .childrenIds(List.of(3L, 4L))
                .status(ProjectStatus.COMPLETED)
                .build();

        Project firstChildProject = Project.builder()
                .id(3L)
                .name("firstChild")
                .children(List.of())
                .status(ProjectStatus.COMPLETED)
                .build();
        Project secondChildProject = Project.builder()
                .id(4L)
                .name("SecondChild")
                .children(List.of())
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        Project parentProject = Project.builder()
                .id(2L)
                .name("Test")
                .parentProject(null)
                .children(List.of(firstChildProject, secondChildProject))
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        when(projectUtilService.getProjectById(any())).thenReturn(parentProject);
        when(projectUtilService.getAllSubprojectsToProjectById(any())).thenReturn(parentProject.getChildren());
        when(projectValidateService.statusDtoAndProjectDoesNotMatches(any(), any())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> service.updateProject(2L, parentDto, 1L));
    }

    @Test
    public void testGetProjectByFilters() {
        FilterProjectDto nameFilter = new FilterProjectDto();
        nameFilter.setName("Second");
        Long id = 1L;
        Project p1 = new Project();
        p1.setName("First");
        Project p2 = new Project();
        p1.setName("Second");
        Project p3 = new Project();
        p1.setName("Fifth");
        Project p4 = new Project();
        p1.setName("Second");
        Project p5 = new Project();
        p1.setName("Second");
        List<Project> projects = List.of(p1, p2, p3, p4, p5);
        when(projectUtilService.getAllSubprojectsToProjectById(any())).thenReturn(projects);

        List<CreateSubProjectDto> resultProjects = service.getProjectByFilters(nameFilter, 1L);
    }
}
