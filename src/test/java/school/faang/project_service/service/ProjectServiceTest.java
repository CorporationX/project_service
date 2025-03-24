package school.faang.project_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.mapper.SubProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.filters.FilterProjects;

import faang.school.projectservice.validations.validator.ProjectValidator;
import faang.school.projectservice.validations.validator.SubProjectValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private SubProjectMapperImpl subProjectMapper;

    @Mock
    private SubProjectValidator subProjectValidator;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private FilterProjects nameProjectFilter;

    @Mock
    private FilterProjects statusProjectFilter;

    @InjectMocks
    private ProjectService projectService;

    private CreateSubProjectDto createDto;
    private FilterSubProjectDto filterDto;
    private Project parentProject;
    private Project subProjectAlpha;
    private Project subProjectBeta;
    private FilterSubProjectDto filters;
    private List<FilterProjects> projectFilters;

    @BeforeEach
    public void setUp() {
        createDto = new CreateSubProjectDto("subproject", "new", 21L, 1L);

        parentProject = new Project();
        parentProject.setId(1L);
        parentProject.setStatus(ProjectStatus.CREATED);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        subProjectAlpha = new Project();
        subProjectAlpha.setId(2L);
        subProjectAlpha.setStatus(ProjectStatus.CREATED);
        subProjectAlpha.setVisibility(ProjectVisibility.PRIVATE);

        subProjectBeta = new Project();
        subProjectBeta.setId(3L);
        subProjectBeta.setStatus(ProjectStatus.COMPLETED);
        subProjectBeta.setVisibility(ProjectVisibility.PUBLIC);

        parentProject.setChildren(List.of(subProjectAlpha, subProjectBeta));

        filterDto = new FilterSubProjectDto();
        filterDto.setName("project");
    }

    @Test
    public void testCreateSubProjectSuccess() {
        Long parentId = 1L;

        when(projectRepository.findById(parentId))
                .thenReturn(Optional.of(parentProject));
        //doNothing().when(projectValidator).doesProjectExist(Optional.of(parentProject));
        //doNothing().when(subProjectValidator).canBeParentProject(parentProject);

        Project newSubProject = subProjectMapper.toSubEntity(createDto);
        when(projectRepository.save(newSubProject))
                .thenReturn(newSubProject);

        Project result = projectService.createSubProject(parentId, newSubProject);

        assertNotNull(result);
        assertEquals(parentId, result.getParentProject().getId());
        verify(projectRepository, times(1)).save(newSubProject);
    }

    @Test
    public void testCreateSubProjectParentNotFound() {
        Long parentId = 1L;
        when(projectRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> projectService.createSubProject(parentId, subProjectAlpha));
    }

    @Test
    public void testUpdateSubProject_Success() {
        Long projectId = 2L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(subProjectAlpha));
        //doNothing().when(projectValidator).doesProjectExist(Optional.of(subProjectAlpha));
        //doNothing().when(subProjectValidator).childCompleted(subProjectAlpha.getChildren());
        when(projectRepository.save(subProjectAlpha)).thenReturn(subProjectAlpha);

        Project result = projectService.updateSubProject(projectId, ProjectStatus.COMPLETED, ProjectVisibility.PUBLIC);

        assertNotNull(result);
        assertEquals(ProjectStatus.COMPLETED, result.getStatus());
        assertEquals(ProjectVisibility.PUBLIC, result.getVisibility());
    }


    @Test
    public void testGetSubProjects_Success() {
        Long parentId = 1L;

        when(nameProjectFilter.isApplicable(any())).thenReturn(true);
        when(statusProjectFilter.isApplicable(any())).thenReturn(true);
        when(nameProjectFilter.apply(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(statusProjectFilter.apply(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
        projectFilters = List.of(nameProjectFilter, statusProjectFilter);
        projectService = new ProjectService(
                projectRepository, subProjectValidator, projectValidator, projectFilters
        );

        when(projectRepository.findById(parentId)).thenReturn(Optional.of(parentProject));
        //doNothing().when(projectValidator).doesProjectExist(Optional.of(parentProject));
        //doNothing().when(subProjectValidator).shouldBePublic(parentProject);

        List<Project> result = projectService.getSubProjects(parentId, filterDto, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetSubProjects_ParentNotFound() {
        Long parentId = 1L;
        when(projectRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> projectService.getSubProjects(parentId, filters, 2));

        verify(projectRepository, times(1)).findById(parentId);
        verify(projectValidator, times(1)).doesProjectExist(Optional.empty());
        verify(subProjectValidator, times(0)).shouldBePublic(parentProject);
    }
}

