package faang.school.projectservice.service;

import faang.school.projectservice.config.feign.UserContext;
import faang.school.projectservice.dto.task.TaskCreateDto;
import faang.school.projectservice.dto.task.TaskUpdateDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.filter.task.TaskFilter;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Spy
    private TaskMapper taskMapper;
    @Mock
    private ProjectService projectService;
    @Mock
    private UserContext userContext;
    @Mock
    private List<TaskFilter> taskFilters;
    @Mock
    private Project project;
    @Mock
    private Team team;
    @Mock
    private TeamMember teamMember;

    @InjectMocks
    private TaskService taskService;

    @Test
    public void testCreateSuccessCase() {
        TaskCreateDto createDto = TaskCreateDto.builder()
                .name("Test")
                .projectId(1L)
                .performerUserId(5L)
                .reporterUserId(10L)
                .build();

        long currentUserId = 3L;

        Mockito.when(projectService.getProjectById(createDto.getProjectId())).thenReturn(project);
        Mockito.when(userContext.getUserId()).thenReturn(currentUserId);

        List<Team> teams = List.of(team);
        Mockito.when(project.getTeams()).thenReturn(teams);
        Mockito.when(team.getTeamMembers()).thenReturn(List.of(teamMember));

        Mockito.when(teamMember.getUserId()).thenReturn(3L);

        Task newTask = taskMapper.toEntity(createDto);

        taskService.create(createDto);
        Mockito.verify(taskRepository, Mockito.times(1)).save(newTask);
    }

    @Test
    public void testCreateUserNotInProject() {
        TaskCreateDto createDto = TaskCreateDto.builder()
                .name("Test")
                .projectId(1L)
                .performerUserId(5L)
                .reporterUserId(10L)
                .build();

        long currentUserId = 1L;

        Mockito.when(projectService.getProjectById(createDto.getProjectId())).thenReturn(project);
        Mockito.when(userContext.getUserId()).thenReturn(currentUserId);

        List<Team> teams = List.of(team);
        Mockito.when(project.getTeams()).thenReturn(teams);
        Mockito.when(team.getTeamMembers()).thenReturn(List.of(teamMember));

        Mockito.when(teamMember.getUserId()).thenReturn(3L);

        assertThrows(BusinessException.class, () -> taskService.create(createDto));
    }

    @Test
    public void testUpdateSuccessCase() {
        TaskUpdateDto updateDto = TaskUpdateDto.builder()
                .id(1L)
                .description("Test")
                .performerUserId(5L)
                .build();

        long currentUserId = 3L;

        Task task = Task.builder()
                .id(1L)
                .project(Project.builder().id(1L).build())
                .performerUserId(1L)
                .reporterUserId(2L)
                .build();

        Mockito.when(taskRepository.getReferenceById(updateDto.getId())).thenReturn(task);

        Mockito.when(projectService.getProjectById(task.getProject().getId())).thenReturn(project);
        Mockito.when(userContext.getUserId()).thenReturn(currentUserId);

        List<Team> teams = List.of(team);
        Mockito.when(project.getTeams()).thenReturn(teams);
        Mockito.when(team.getTeamMembers()).thenReturn(List.of(teamMember));

        Mockito.when(teamMember.getUserId()).thenReturn(3L);

        taskMapper.updateEntityFromDto(task, updateDto, taskRepository);

        taskService.update(updateDto);
        Mockito.verify(taskRepository, Mockito.times(1)).save(task);
    }

    @Test
    public void testUpdateUserNotInProject() {
        TaskUpdateDto updateDto = TaskUpdateDto.builder()
                .id(1L)
                .description("Test")
                .performerUserId(5L)
                .build();

        long currentUserId = 3L;

        Task task = Task.builder()
                .id(1L)
                .project(Project.builder().id(1L).build())
                .performerUserId(1L)
                .reporterUserId(2L)
                .build();

        Mockito.when(taskRepository.getReferenceById(updateDto.getId())).thenReturn(task);

        Mockito.when(projectService.getProjectById(task.getProject().getId())).thenReturn(project);
        Mockito.when(userContext.getUserId()).thenReturn(currentUserId);

        List<Team> teams = List.of(team);
        Mockito.when(project.getTeams()).thenReturn(teams);
        Mockito.when(team.getTeamMembers()).thenReturn(List.of(teamMember));

        Mockito.when(teamMember.getUserId()).thenReturn(1L);

        taskMapper.updateEntityFromDto(task, updateDto, taskRepository);

        assertThrows(BusinessException.class, () -> taskService.update(updateDto));
    }

    @Test
    public void testGetAllTasksByProjectIdSuccessCase() {
        long projectId = 1L;
        long currentUserId = 3L;

        Mockito.when(projectService.getProjectById(projectId)).thenReturn(project);
        Mockito.when(userContext.getUserId()).thenReturn(currentUserId);

        List<Team> teams = List.of(team);
        Mockito.when(project.getTeams()).thenReturn(teams);
        Mockito.when(team.getTeamMembers()).thenReturn(List.of(teamMember));

        Mockito.when(teamMember.getUserId()).thenReturn(3L);

        taskService.getAllTasksByProjectId(projectId);
        Mockito.verify(taskRepository, Mockito.times(1)).findAllByProjectId(projectId);
    }

    @Test
    public void testGetAllTasksByProjectIdUserNotInProject() {
        long projectId = 1L;
        long currentUserId = 3L;

        Mockito.when(projectService.getProjectById(projectId)).thenReturn(project);
        Mockito.when(userContext.getUserId()).thenReturn(currentUserId);

        List<Team> teams = List.of(team);
        Mockito.when(project.getTeams()).thenReturn(teams);
        Mockito.when(team.getTeamMembers()).thenReturn(List.of(teamMember));

        Mockito.when(teamMember.getUserId()).thenReturn(1L);

        assertThrows(BusinessException.class, () -> taskService.getAllTasksByProjectId(projectId));
    }
}
