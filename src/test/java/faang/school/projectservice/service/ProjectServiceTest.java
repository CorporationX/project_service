package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.project.ProjectNameFilter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    @Spy
    private List<ProjectFilter> filtersFilt;
    @Mock
    private ProjectStatusFilter projectStatusFilter;
    @Mock
    private ProjectNameFilter projectNameFilter;
    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    @InjectMocks
    private ProjectService projectService;

    Project project;
    Project project2;
    Project project3;
    Project project4;
    Project project5;
    Project project6;
    ProjectDto project3Dto;
    Project pubProject;
    Project parent;
    Project updatePublic;
    ProjectDto projectDto;
    ProjectDto project1Dto;
    ProjectDto project2Dto;
    ProjectDto project4Dto;
    ProjectDto project5Dto;
    ProjectDto project6Dto;
    CreateSubProjectDto createProjectDto;
    Project updatePubProjectDto;
    UpdateSubProjectDto updateSubProjectDto;
    UpdateSubProjectDto updateSubProjectDto1;
    Project child1;
    Project child2;
    Project child3;
    Project child4;
    Project child33;
    ProjectDto child33Dto;
    ProjectDto child44Dto;
    ProjectDto child55Dto;
    Project child44;
    Project child55;
    ProjectFilterDto projectFilterDto;
    Stream<ProjectFilter> filterStream;
    Stream<Project> projects;
    Stream<Project> projectsDto;


    @BeforeEach
    void init() {
//        List<ProjectFilter> listFilters = new ArrayList<>(List.of( new ProjectNameFilter(), new ProjectStatusFilter()));
//        ReflectionTestUtils.setField(projectService, "filters", listFilters);

        parent = Project.builder()
                .id(1L)
                .build();

        pubProject = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .parentProject(parent)
                .build();

        projectDto = ProjectDto.builder()
                .id(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        child1 = Project.builder()
                .id(11L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        child2 = Project.builder()
                .id(22L)
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        child3 = Project.builder()
                .id(33L)
                .status(ProjectStatus.ON_HOLD)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        child4 = Project.builder()
                .id(44L)
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        child33 = Project.builder()
                .id(33L)
                .name("Poker")
                .status(ProjectStatus.CANCELLED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        child44 = Project.builder()
                .id(44L)
                .name("Tetris")
                .status(ProjectStatus.CANCELLED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        child55 = Project.builder()
                .id(55L)
                .name("Sony")
                .status(ProjectStatus.CANCELLED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        createProjectDto = CreateSubProjectDto.builder()
                .parentId(123L)
                .build();
        child33Dto = ProjectDto.builder()
                .id(33L)
                .name("Poker")
                .status(ProjectStatus.ON_HOLD)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        child44Dto = ProjectDto.builder()
                .id(44L)
                .name("Tetris")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        child55Dto = ProjectDto.builder()
                .id(55L)
                .name("Sony")
                .status(ProjectStatus.CANCELLED)
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>())
                .build();

        projects = Stream.of(child55,child44);
        projectsDto = Stream.of(child55,child44);

        filtersFilt = Arrays.asList(projectNameFilter,projectStatusFilter);
//        ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();
//        ProjectNameFilter projectNameFilter = new ProjectNameFilter();

//        filterStream = Stream.of(new ProjectNameFilter());
    }

    @Test
    public void testCreateSubProjectSuccessfully() {
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        projectRepository.getProjectById(anyLong());
        projectRepository.save(project);

        verify(projectRepository, times(1)).getProjectById(anyLong());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testUpdateProjectStatusToInProgressAndVisibilityToPrivate() {
        project = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .children(Arrays.asList(
                        child4,
                        child2
                ))
                .status(ProjectStatus.ON_HOLD)
                .build();
        updateSubProjectDto = UpdateSubProjectDto.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        project1Dto = ProjectDto.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .children(Arrays.asList(
                        child4.getId(),
                        child2.getId()
                ))
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        project5Dto = ProjectDto.builder()
                .id(5L)
                .children(Arrays.asList(
                        child33.getId(),
                        child44.getId(),
                        child55.getId()
                ))
                .build();
        project6Dto = ProjectDto.builder()
                .id(6L)
                .children(Arrays.asList(
                        child33.getId(),
                        child44.getId(),
                        child55.getId()
                ))
                .build();

        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        assertEquals(project1Dto, projectService.updateProject(anyLong(), updateSubProjectDto));

//        projectService.updateProject(anyLong(), updateSubProjectDto);
//        verify(projectRepository, times(1)).save(projectCaptor.capture());
    }

    @Test
    public void testUpdateProjectStatusToCompletedWhenConditions() {
        project2 = Project.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        updateSubProjectDto = UpdateSubProjectDto.builder()
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        project2Dto = ProjectDto.builder()
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .children(new ArrayList<>())
                .build();
        when(projectRepository.getProjectById(anyLong())).thenReturn(project2);

        assertEquals(project2Dto, projectService.updateProject(anyLong(), updateSubProjectDto));
    }

    @Test
    public void testThrowDataValidationExceptionWhenCompletingProjectWithIncompleteChildren() {
        project3 = Project.builder()
                .id(3L)
                .children(Arrays.asList(
                        child1,
                        child2
                ))
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        updateSubProjectDto1 = UpdateSubProjectDto.builder()
                .status(ProjectStatus.COMPLETED)
                .build();
        when(projectRepository.getProjectById(anyLong())).thenReturn(project3);

        Assertions.assertThrows(DataValidationException.class,
                () -> projectService.updateProject(project3.getId(), updateSubProjectDto1));
    }

    @Test
    public void testReturnUpdatedProjectDtoWhenMomentReceived() {
        project4 = Project.builder()
                .id(4L)
                .children(Collections.singletonList(child4))
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.IN_PROGRESS)
                .moments(new ArrayList<>())
                .build();
        updateSubProjectDto = UpdateSubProjectDto.builder()
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        project4Dto = ProjectDto.builder()
                .id(4L)
                .children(Collections.singletonList(child4.getId()))
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.COMPLETED)
                .build();
        when(projectRepository.getProjectById(anyLong())).thenReturn(project4);

        assertEquals(project4Dto, projectService.updateProject(anyLong(), updateSubProjectDto));
    }

    @Test
    public void testFilteredPublicSubProject() {
        projectFilterDto = ProjectFilterDto.builder()
                .name("Sony")
                .status(ProjectStatus.CANCELLED)
                .build();
        project5 = Project.builder()
                .id(5L)
                .visibility(ProjectVisibility.PUBLIC)
                .children(Arrays.asList(
                        child33,
                        child44,
                        child55
                ))
                .build();

       when(projectRepository.getProjectById(anyLong())).thenReturn(project5);
       when(projectNameFilter.apply(projects,projectFilterDto)).thenReturn(projectsDto);

        List<ProjectDto> actualSubProjects = projectService.getFilteredPublicSubProjects(project5.getId(), projectFilterDto);
        List<ProjectDto> expectedSubProjects = new ArrayList<>(List.of(child55Dto));

        assertTrue(expectedSubProjects.size() == actualSubProjects.size()
                 && expectedSubProjects.containsAll(actualSubProjects));
    }

}


//        project6 = Project.builder()
//                .id(6L)
//                .visibility(ProjectVisibility.PRIVATE)
//                .children(Arrays.asList(
//                        child33,
//                        child44,
//                        child55
//                ))
//                .build();


















