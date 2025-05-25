package school.faang.projectservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.ProjectServiceImpl;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {
    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapperImpl projectMapper;

    @Mock
    private ProjectFilter mockFilter1;

    @Mock
    private ProjectFilter mockFilter2;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private UserContext userContext;

    @Captor
    private ArgumentCaptor<Project> captor;

    private ProjectService projectService;  

    @BeforeEach
    public void setUp() {
        projectService = new ProjectServiceImpl (
            projectRepository,
            teamMemberRepository,
            projectMapper,
            List.of(mockFilter1, mockFilter2),
            userContext
        );
    }

    @Test
    public void testCreate_whenProjectDtoIsPartiallyEmpty_thenFieldsAreAssigned() {
        ProjectDto projectDto = ProjectDto.builder()
            .name("Name")
            .description("Description")
            .build();
        when(projectRepository.save(any())).thenReturn(new Project());

        projectService.create(projectDto);

        verify(projectRepository, times(1)).save(captor.capture());
        Project capturedProject = captor.getValue();
        assertEquals(projectDto.getName(), capturedProject.getName());
        assertEquals(projectDto.getDescription(), capturedProject.getDescription());
        assertEquals(ProjectStatus.CREATED, capturedProject.getStatus());
        assertEquals(ProjectVisibility.PRIVATE, capturedProject.getVisibility());
    }

    @Test
    public void testCreate_whenProjectDtoIsComplete_thenFieldsAreReAssigned() {
        ProjectDto projectDto = ProjectDto.builder()
            .name("Name")
            .description("Description")
            .visibility(ProjectVisibility.PUBLIC)
            .status(ProjectStatus.IN_PROGRESS)
            .build();
        when(projectRepository.save(any())).thenReturn(new Project());

        projectService.create(projectDto);

        verify(projectRepository, times(1)).save(captor.capture());
        Project capturedProject = captor.getValue();
        assertEquals(projectDto.getName(), capturedProject.getName());
        assertEquals(projectDto.getDescription(), capturedProject.getDescription());
        assertEquals(ProjectStatus.CREATED, capturedProject.getStatus());
        assertEquals(ProjectVisibility.PUBLIC, capturedProject.getVisibility());
    }

    @Test
    public void testUpdate_whenProjectNotFoud_thenThrowsEntityNotFoundException() {
        when(projectRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> projectService.update(new ProjectDto()));
    }

    @Test
    public void testUpdate_whenProjectUpdated_thenProjectIsUpdated() {
        Project baseProject = Project.builder()
            .id(1L)
            .name("Base Name")
            .ownerId(1L)
            .description("Description")
            .status(ProjectStatus.CREATED)
            .build();
        ProjectDto updatedProject = ProjectDto.builder()
            .description("Updated Description")
            .status(ProjectStatus.IN_PROGRESS)
            .build();
        when(projectRepository.findById(any())).thenReturn(Optional.of(baseProject));
        when(userContext.getUserId()).thenReturn(1L);
        
        projectService.update(updatedProject);

        verify(projectRepository, times(1)).findById(any());
        verify(projectRepository, times(1)).save(captor.capture());
        Project capturedProject = captor.getValue();
        assertEquals(baseProject.getId(), capturedProject.getId());
        assertEquals(baseProject.getName(), capturedProject.getName());
        assertEquals(updatedProject.getDescription(), capturedProject.getDescription());
        assertEquals(updatedProject.getStatus(), capturedProject.getStatus());
    }

    @Test
    public void testGetAll_whenNoFiltersApplied_thenAllProjectsAreReturned() {
        Project project1 = Project.builder().name("Project 1").status(ProjectStatus.CREATED).build();
        Project project2 = Project.builder().name("Project 2").status(ProjectStatus.IN_PROGRESS).build();

        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));

        when(mockFilter1.isApplicable(any())).thenReturn(false);
        when(mockFilter2.isApplicable(any())).thenReturn(false);

        List<ProjectDto> result = projectService.getAll(new ProjectFilterDto());
        assertEquals(2, result.size());
    }

    @Test
    public void testGetAll_whenAllFiltersApplied_thenFilteredProjectsAreReturned() {
        Project project1 = Project.builder().name("Project 1").status(ProjectStatus.CREATED).build();
        Project project2 = Project.builder().name("Project 2").status(ProjectStatus.IN_PROGRESS).build();

        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));

        when(mockFilter1.isApplicable(any())).thenReturn(true);
        when(mockFilter2.isApplicable(any())).thenReturn(true);

        when(mockFilter1.apply(any(), any())).thenAnswer((Answer<Stream<ProjectDto>>) invocation -> {
            Stream<ProjectDto> stream = invocation.getArgument(0);
            return stream.filter(project -> project.getName().equals("Project 1"));
        });
        when(mockFilter2.apply(any(), any())).thenAnswer((Answer<Stream<ProjectDto>>) invocation -> {
            Stream<ProjectDto> stream = invocation.getArgument(0);
            return stream.filter(project -> project.getStatus().equals(ProjectStatus.IN_PROGRESS));
        });

        List<ProjectDto> result = projectService.getAll(new ProjectFilterDto());
        assertTrue(result.isEmpty());
    }
}
