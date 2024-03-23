package faang.school.projectservice.service;

import static faang.school.projectservice.model.ProjectStatus.COMPLETED;
import static faang.school.projectservice.model.ProjectVisibility.PRIVATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.projectservice.model.ProjectStatus.CREATED;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;

@ExtendWith(MockitoExtension.class)
public class SubProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectFilter projectFilter;
    @InjectMocks
    private SubProjectService subProjectService;

    Project firstRootProject;
    ProjectDto testUpdateRootProject;
    CreateSubProjectDto testCreateSubProject;
    Project secondSubProject;
    Project rootProjectVisibilityPrivate;
    ProjectDto RootProjectToComplete;

    List<ProjectFilter> projectFilters;

    @BeforeEach
    public void init() {
        projectFilters = List.of(projectFilter);

        firstRootProject = Project.builder()
                .id(1L)
                .name("secondRootProject")
                .description("secondRootProject")
                .children(null)
                .parentProject(null)
                .visibility(PUBLIC)
                .status(CREATED)
                .build();

        testCreateSubProject = CreateSubProjectDto.builder()
                .name("secondSubProject")
                .description("new subproject for the secondRootProject")
                .parentProjectId(1L)
                .visibility(PUBLIC)
                .build();
        testUpdateRootProject = ProjectDto.builder()
                .id(1L)
                .name("firstRootProject")
                .visibility(PRIVATE)
                .status(COMPLETED)
                .build();
        rootProjectVisibilityPrivate = Project.builder()
                .id(1L)
                .name("secondRootProject")
                .children(null)
                .parentProject(null)
                .visibility(PRIVATE)
                .status(CREATED)
                .build();
    }

    @Test
    public void testCreateSubProject() {
        when(projectRepository.getProjectById(1L)).thenReturn(firstRootProject);

        subProjectService.createSubProject(testCreateSubProject);

        verify(projectRepository, times(1)).save(firstRootProject);
    }

    @Test
    public void testCreateSubProjectStatusPublic() {
        when(projectRepository.getProjectById(1L)).thenReturn(rootProjectVisibilityPrivate);

        assertThrows(IllegalArgumentException.class, () -> subProjectService.createSubProject(testCreateSubProject));
    }

    @Test
    public void testUpdateProject() {
        when(projectRepository.getProjectById(1L)).thenReturn(firstRootProject);
        Project completedSubProject = Project.builder()
                .id(3L)
                .name("firstSubProject")
                .description("new subproject for the secondRootProject")
                .parentProject(null)
                .visibility(PUBLIC)
                .status(COMPLETED)
                .build();
        firstRootProject.setChildren(List.of(completedSubProject));

        subProjectService.updateProject(testUpdateRootProject);

        verify(projectRepository, times(1)).save(firstRootProject);
    }

    @Test
    public void testUpdateStatusProjectToCompleteIncorrect() {
        when(projectRepository.getProjectById(1L)).thenReturn(firstRootProject);

        RootProjectToComplete = ProjectDto.builder()
                .id(1L)
                .name("secondRootProject")
                .visibility(PUBLIC)
                .status(COMPLETED)
                .build();
        Project completedSubProject = Project.builder()
                .id(3L)
                .name("firstSubProject")
                .description("new subproject for the secondRootProject")
                .parentProject(null)
                .visibility(PUBLIC)
                .status(CREATED)
                .build();
        firstRootProject.setChildren(List.of(completedSubProject));

        assertThrows(IllegalArgumentException.class, () -> subProjectService.updateProject(RootProjectToComplete));
    }

    @Test
    public void testReturnMomentWhenUpdateProject() {
        when(projectRepository.getProjectById(1L)).thenReturn(firstRootProject);

        secondSubProject = Project.builder()
                .id(2L)
                .name("secondSubProject")
                .description("new subproject for the secondRootProject")
                .parentProject(null)
                .visibility(PRIVATE)
                .status(COMPLETED)
                .build();
        firstRootProject.setChildren(List.of(secondSubProject));

        Moment returnMoment = Moment.builder()
                .name(testUpdateRootProject.getName())
                .description(firstRootProject.getDescription())
                .projects(firstRootProject.getChildren()).build();

        assertEquals(returnMoment, subProjectService.updateProject(testUpdateRootProject));
    }
}