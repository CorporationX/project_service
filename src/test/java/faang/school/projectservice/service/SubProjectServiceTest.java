package faang.school.projectservice.service;

import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.google.GoogleCalendarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class SubProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private GoogleCalendarService googleCalendarService;

    @Mock
    private ProjectScheduleService projectScheduleService;

    @Mock
    private ProjectMeetService projectMeetService;

    @InjectMocks
    private ProjectService projectService;

    private Project parentProject;
    private Project subProject;

    @BeforeEach
    void setUp() {
        parentProject = Project.builder()
                .id(1L)
                .name("Parent Project")
                .build();

        subProject = Project.builder()
                .id(2L)
                .name("Sub Project")
                .description("Sub Project Description")
                .parentProject(parentProject)
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createSubProject_ShouldSaveAndReturnSubProject() {
        Project parentProject = Project.builder()
                .id(1L)
                .name("Parent Project")
                .status(ProjectStatus.CREATED)
                .build();

        Project subProject = Project.builder()
                .name("Subproject")
                .parentProject(parentProject)
                .status(ProjectStatus.CREATED)
                .build();

        when(projectRepository.findById(parentProject.getId())).thenReturn(Optional.of(parentProject));
        when(projectRepository.save(any(Project.class))).thenReturn(subProject);
        when(googleCalendarService.createCalendar(any())).thenReturn(new com.google.api.services.calendar.model.Calendar());

        Project result = projectService.createSubProject(subProject, 1L);

        assertNotNull(result);
        assertEquals("Subproject", result.getName());
        assertEquals(parentProject, result.getParentProject());
        verify(projectRepository, times(1)).save(subProject);
    }

    @Test
    void createSubProject_ShouldThrowExceptionIfParentNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Project subProject = Project.builder()
                .name("Subproject")
                .parentProject(Project.builder().id(1L).build())
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.createSubProject(subProject, 1L));

        assertEquals("Project not found", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }
}