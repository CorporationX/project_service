package faang.school.projectservice.service;

import static faang.school.projectservice.model.ProjectStatus.COMPLETED;
import static faang.school.projectservice.model.ProjectVisibility.PRIVATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.handler.exceptions.EntityNotFoundException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.subproject.SubProjectService;
import faang.school.projectservice.service.subproject.filter.SubProjectNameFilter;
import faang.school.projectservice.service.subproject.filter.SubProjectStatusFilter;
import faang.school.projectservice.validation.subproject.SubProjectValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.projectservice.model.ProjectStatus.CREATED;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;

@ExtendWith(MockitoExtension.class)
public class SubProjectServiceTest {
    private ProjectRepository projectRepository;
    private SubProjectService subProjectService;

    Project firstRootProject;
    SubProjectDto testUpdateRootProject;
    CreateSubProjectDto testCreateSubProject;
    Project secondSubProject;
    Project rootProjectVisibilityPrivate;
    SubProjectDto RootProjectToComplete;
    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        SubProjectValidation subProjectValidation = new SubProjectValidation(projectRepository);
        ProjectMapper projectMapper = mock(ProjectMapper.class);
        SubProjectNameFilter subProjectNameFilter = mock(SubProjectNameFilter.class);
        SubProjectStatusFilter subProjectStatusFilter = mock(SubProjectStatusFilter.class);
        subProjectService = new SubProjectService(subProjectValidation,projectRepository, projectMapper, subProjectNameFilter, subProjectStatusFilter);
    }

    @BeforeEach
    public void init() {
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
        testUpdateRootProject = SubProjectDto.builder()
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
    public void testCreateSubProjectSuccess() {
        when(projectRepository.getProjectById(1L)).thenReturn(firstRootProject);
        when(projectRepository.existsById(1L)).thenReturn(true);

        subProjectService.createSubProject(testCreateSubProject);

        verify(projectRepository, times(1)).save(firstRootProject);
    }

    @Test
    public void testCreateSubProjectWithChildrenProjectSuccess() {
        when(projectRepository.getProjectById(1L)).thenReturn(firstRootProject);
        when(projectRepository.existsById(1L)).thenReturn(true);
        firstRootProject.setChildren(List.of(Project.builder()
                .id(2L)
                .name("Test SubProject")
                .parentProject(firstRootProject)
                .status(CREATED)
                .visibility(PUBLIC)
                .build()));
        subProjectService.createSubProject(testCreateSubProject);

        verify(projectRepository, times(1)).save(firstRootProject);
    }

    @Test
    public void testCreateSubProjectFailure() {
        when(projectRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> subProjectService.createSubProject(testCreateSubProject));
    }

    @Test
    public void testCreateSubProjectStatusPublic() {
        when(projectRepository.getProjectById(1L)).thenReturn(rootProjectVisibilityPrivate);
        when(projectRepository.existsById(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> subProjectService.createSubProject(testCreateSubProject));
    }

    @Test
    public void testUpdateProject() {
        when(projectRepository.getProjectById(1L)).thenReturn(firstRootProject);
        when(projectRepository.existsById(1L)).thenReturn(true);
        Project completedSubProject = Project.builder()
                .id(3L)
                .name("firstSubProject")
                .description("new subproject for the secondRootProject")
                .parentProject(null)
                .visibility(PUBLIC)
                .status(COMPLETED)
                .build();
        firstRootProject.setChildren(List.of(completedSubProject));

        subProjectService.updateSubProject(testUpdateRootProject);

        verify(projectRepository, times(1)).save(firstRootProject);
    }

    @Test
    public void testUpdateStatusProjectToCompleteIncorrect() {
        when(projectRepository.getProjectById(1L)).thenReturn(firstRootProject);
        when(projectRepository.existsById(1L)).thenReturn(true);

        RootProjectToComplete = SubProjectDto.builder()
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

        assertThrows(IllegalArgumentException.class, () -> subProjectService.updateSubProject(RootProjectToComplete));
    }

    @Test
    public void testReturnMomentWhenUpdateProject() {
        when(projectRepository.getProjectById(1L)).thenReturn(firstRootProject);
        when(projectRepository.existsById(1L)).thenReturn(true);

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

        assertEquals(returnMoment, subProjectService.updateSubProject(testUpdateRootProject));
    }
}