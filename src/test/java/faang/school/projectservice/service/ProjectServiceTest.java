package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.IllegalSubProjectsStatusException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.project.ProjectNameFilter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Captor
    private ArgumentCaptor<Project> projectArgumentCaptor;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private ProjectRepository projectRepository;


    private ProjectService projectService;

    private CreateSubProjectDto createSubProjectDto;
    private Project parentProject;
    private List<Project> subProjects;
    private ProjectMapperImpl projectMapperImpl;
    private ProjectFilterDto projectFilterDto;

    @BeforeEach
    public void setUp() {
        List<ProjectFilter> projectFilterList = List.of(
                new ProjectNameFilter(),
                new ProjectStatusFilter()
        );

        projectMapperImpl = new ProjectMapperImpl();

        projectService = new ProjectService(projectMapperImpl, projectValidator,
                projectRepository, projectFilterList);

        subProjects = List.of(
                Project.builder()
                        .name("ProjectName")
                        .status(ProjectStatus.IN_PROGRESS)
                        .visibility(ProjectVisibility.PUBLIC)
                        .children(new ArrayList<>()).build(),
                Project.builder()
                        .name("not")
                        .status(ProjectStatus.IN_PROGRESS)
                        .visibility(ProjectVisibility.PUBLIC)
                        .children(new ArrayList<>()).build()
        );

        parentProject = Project.builder()
                .id(1L)
                .children(subProjects)
                .build();

        createSubProjectDto = CreateSubProjectDto.builder()
                .parentProjectId(parentProject.getId())
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectFilterDto = ProjectFilterDto.builder()
                .name("ProjectName")
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .build();

        lenient().when(projectRepository.getProjectById(parentProject.getId())).thenReturn(parentProject);
    }

    @Test
    @DisplayName("testing createSubProject")
    public void testCreateSubProjectWithProjectRepositoryMethodExecution() {
        projectService.createSubProject(createSubProjectDto);
        verify(projectRepository, times(1))
                .getProjectById(createSubProjectDto.getParentProjectId());
        verify(projectValidator, times(1))
                .validateSubProjectVisibility(parentProject.getVisibility(), createSubProjectDto.getVisibility());
        verify(projectRepository, times(1)).save(projectArgumentCaptor.capture());
    }

    @Test
    @DisplayName("testing updateSubProject with non appropriate Status")
    public void testUpdateSubProjectWithNonAppropriateStatus() {
        ProjectDto projectDto = ProjectDto.builder()
                .status(ProjectStatus.COMPLETED).build();
        when(projectRepository.getAllSubProjectsFor(parentProject.getId())).thenReturn(parentProject.getChildren());
        assertThrows(IllegalSubProjectsStatusException.class,
                () -> projectService.updateProject(parentProject.getId(), projectDto));
    }

    @Test
    @DisplayName("testing updateSubProject with changing visibility to private and status to completed")
    public void testUpdateSubProjectWithVisibilityPrivateChangeStatusCompleted() {
        ProjectDto projectDto = ProjectDto.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.COMPLETED).build();
        when(projectRepository.getAllSubProjectsFor(parentProject.getId())).thenReturn(parentProject.getChildren());
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        parentProject.getChildren().forEach(project -> {
            project.setVisibility(ProjectVisibility.PUBLIC);
            project.setStatus(ProjectStatus.COMPLETED);
        });

        projectService.updateProject(parentProject.getId(), projectDto);
        assertEquals(ProjectVisibility.PRIVATE, parentProject.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, parentProject.getChildren().get(0).getVisibility());
        assertEquals(ProjectStatus.COMPLETED, parentProject.getStatus());
    }

    @Test
    @DisplayName("testing updateSubProject with visibility to public changing and status to cancelled")
    public void testUpdateSubProjectWithVisibilityPublicStatusCancelled() {
        ProjectDto projectDto = ProjectDto.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CANCELLED).build();
        parentProject.setVisibility(ProjectVisibility.PRIVATE);
        parentProject.getChildren().forEach(project -> {
            project.setVisibility(ProjectVisibility.PRIVATE);
            project.setStatus(ProjectStatus.CANCELLED);
        });

        projectService.updateProject(parentProject.getId(), projectDto);
        assertEquals(parentProject.getVisibility(), ProjectVisibility.PUBLIC);
        assertEquals(parentProject.getChildren().get(0).getVisibility(), ProjectVisibility.PRIVATE);
        assertEquals(parentProject.getStatus(), ProjectStatus.CANCELLED);
    }

    @Test
    @DisplayName("testing getSubProjects with selection correct subProject")
    public void testGetSubProjects() {
        List<ProjectDto> selectedSubProjects = projectService.getSubProjects(parentProject.getId(), projectFilterDto);
        assertEquals(1, selectedSubProjects.size());
        assertEquals(projectMapperImpl.toDto(subProjects.get(0)), selectedSubProjects.get(0));
    }
}