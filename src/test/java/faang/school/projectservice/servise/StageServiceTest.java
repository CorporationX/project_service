package faang.school.projectservice.servise;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.FateOfTasksAfterDelete;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.StageRolesService;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.TeamMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    StageRepository stageRepository = Mockito.mock(StageRepository.class);
    TaskRepository taskRepository = Mockito.mock(TaskRepository.class);
    TeamMemberService teamMembersService = Mockito.mock(TeamMemberService.class);
    ProjectService projectService = Mockito.mock(ProjectService.class);
    StageRolesService stageRolesService = Mockito.mock(StageRolesService.class);
    StageMapper stageMapperMock = Mockito.mock(StageMapper.class);
    StageRolesMapper stageRolesMapperMock = Mockito.mock(StageRolesMapper.class);
    StageFilter filterMock = Mockito.mock(StageFilter.class);
    List<StageFilter> filters = List.of(filterMock);

    StageService stageService = new StageService(
            stageRepository,
            taskRepository,
            teamMembersService,
            projectService,
            stageRolesService,
            stageMapperMock,
            stageRolesMapperMock,
            filters);

    private final StageDto dto = new StageDto();
    private final StageFilterDto filterDto = new StageFilterDto();
    private final Stage stage = new Stage();
    private final Project project = new Project();


    StageDto prepareStageDto() {
        dto.setStageId(10L);
        dto.setProjectId(5L);
        dto.setExecutorIds(List.of(1L, 2L));
        return dto;
    }

    Stage prepareStage() {
        stage.setStageId(10L);
        stage.setProject(prepareProject());
        stage.setExecutors(prepareExecutorList());
        stage.setTasks(prepareTaskList());
        return stage;
    }

    Project prepareProject() {
        project.setId(5L);
        project.setTeams(prepareTeams());
        return project;
    }

    List<Team> prepareTeams() {
        Team fistTeam = new Team();
        Team secondTeam = new Team();
        Stage stage = new Stage();
        stage.setStageId(10L);
        TeamMember firstExecutor = new TeamMember();
        firstExecutor.setId(1L);
        firstExecutor.setRoles(List.of(TeamRole.DEVELOPER));
        firstExecutor.setStages(List.of(stage));
        TeamMember secondExecutor = new TeamMember();
        secondExecutor.setId(2L);
        secondExecutor.setRoles(List.of(TeamRole.TESTER));
        secondExecutor.setStages(List.of(stage));
        TeamMember thirdExecutor = new TeamMember();
        thirdExecutor.setId(3L);
        thirdExecutor.setRoles(List.of(TeamRole.ANALYST, TeamRole.MANAGER));
        thirdExecutor.setStages(List.of(stage));
        TeamMember fourthExecutor = new TeamMember();
        fourthExecutor.setId(4L);
        fourthExecutor.setRoles(List.of(TeamRole.DEVELOPER));
        fourthExecutor.setStages(List.of(stage));
        TeamMember fifthExecutor = new TeamMember();
        fifthExecutor.setId(5L);
        fifthExecutor.setRoles(List.of(TeamRole.TESTER));
        fifthExecutor.setStages(List.of(stage));
        TeamMember sixthExecutor = new TeamMember();
        sixthExecutor.setId(1L);
        sixthExecutor.setRoles(List.of(TeamRole.INTERN));
        sixthExecutor.setStages(List.of(stage));
        fistTeam.setTeamMembers(List.of(firstExecutor, secondExecutor, thirdExecutor));
        secondTeam.setTeamMembers(List.of(fourthExecutor, fifthExecutor, sixthExecutor));
        return List.of(fistTeam, secondTeam);
    }

    List<TeamMember> prepareExecutorList() {
        TeamMember firstExecutor = prepareTeams().get(0).getTeamMembers().get(0);
        TeamMember secondExecutor = prepareTeams().get(0).getTeamMembers().get(1);
        return List.of(firstExecutor, secondExecutor);
    }

    List<Task> prepareTaskList() {
        Task firstTask = new Task();
        Task secondTask = new Task();
        return List.of(firstTask, secondTask);
    }

    List<StageRolesDto> prepareStageRolesDto() {
        StageRolesDto firstStageRolesDto = new StageRolesDto(1L, TeamRole.DEVELOPER, 1, 10L);
        StageRolesDto secondStageRolesDto = new StageRolesDto(2L, TeamRole.TESTER, 1, 10L);
        StageRolesDto thirdStageRolesDto = new StageRolesDto(3L, TeamRole.ANALYST, 1, 10L);
        return List.of(firstStageRolesDto, secondStageRolesDto, thirdStageRolesDto);
    }

    List<StageRoles> prepareStageRoles() {
        StageRoles firstStageRoles = new StageRoles(1L, TeamRole.DEVELOPER, 1, prepareStage());
        StageRoles secondStageRoles = new StageRoles(2L, TeamRole.TESTER, 1, prepareStage());
        StageRoles thirdStageRoles = new StageRoles(3L, TeamRole.ANALYST, 1, prepareStage());
        return List.of(firstStageRoles, secondStageRoles, thirdStageRoles);
    }

    @Test
    void testCreateStageIfStatusCancelled() {
        StageDto dto = prepareStageDto();
        Project project = prepareProject();
        when(projectService.getProject(dto.getProjectId())).thenReturn(project);
        project.setStatus(ProjectStatus.CANCELLED);

        assertThrows(DataValidationException.class, () -> stageService.createStage(dto));
    }

    @Test
    void testCreateStageIfStatusProjectCompleted() {
        StageDto dto = prepareStageDto();
        Project project = prepareProject();
        when(projectService.getProject(dto.getProjectId())).thenReturn(project);
        project.setStatus(ProjectStatus.COMPLETED);

        assertThrows(DataValidationException.class, () -> stageService.createStage(dto));
    }

    @Test
    void testCreateStageIfPresentExecutorsWithoutRole() {
        StageDto dto = prepareStageDto();
        Stage stage = prepareStage();
        List<TeamMember> executors = prepareExecutorList();
        stage.setExecutors(executors);
        Project project = prepareProject();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        TeamMember firstExecutor = prepareExecutorList().get(0);
        TeamMember secondExecutor = prepareExecutorList().get(1);
        secondExecutor.setRoles(null);
        when(projectService.getProject(dto.getProjectId())).thenReturn(project);
        when(stageMapperMock.toEntity(dto)).thenReturn(stage);
        when(teamMembersService.findAllById(List.of(firstExecutor.getId(), secondExecutor.getId())))
                .thenReturn(List.of(firstExecutor, secondExecutor));

        assertThrows(DataValidationException.class, () -> stageService.createStage(dto));
    }

    @Test
    void testCreateStageIsSuccessful() {
        StageDto dto = prepareStageDto();
        Stage stage = prepareStage();
        StageDto saveDto = prepareStageDto();
        Stage saveStage = prepareStage();
        Project project = prepareProject();
        List<StageRolesDto> stageRolesDtoList = prepareStageRolesDto();
        dto.setStageRoles(stageRolesDtoList);
        List<StageRoles> stageRolesList = prepareStageRoles();
        stage.setStageRoles(stageRolesList);
        project.setStatus(ProjectStatus.IN_PROGRESS);
        TeamMember firstExecutor = prepareExecutorList().get(0);
        TeamMember secondExecutor = prepareExecutorList().get(1);
        when(projectService.getProject(dto.getProjectId())).thenReturn(project);
        when(teamMembersService.findAllById(List.of(firstExecutor.getId(), secondExecutor.getId())))
                .thenReturn(List.of(firstExecutor, secondExecutor));
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapperMock.toEntity(dto)).thenReturn(stage);
        when(stageMapperMock.toDto(saveStage)).thenReturn(saveDto);
        when(stageRolesMapperMock.toEntityList(stageRolesDtoList)).thenReturn(stageRolesList);
        when(stageRolesMapperMock.toDtoList(stageRolesList)).thenReturn(stageRolesDtoList);

        StageDto resultStageDto = stageService.createStage(dto);

        assertEquals(resultStageDto.getStageRoles(), stageRolesDtoList);
    }

    @Test
    public void testGetFilteredStagesApplyStatusFilter() {
        Stream<Stage> stageStream = Stream.of(new Stage());
        StageDto dto = prepareStageDto();
        when(filters.get(0).isApplicable(new StageFilterDto())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(stageStream);
        when(stageMapperMock.toDto(stage)).thenReturn(dto);

        List<StageDto> methodResult = stageService.getFilteredStages(filterDto);

        assertEquals(methodResult, List.of(dto));
    }

    @Test
    void testDeleteStageWhenTasksClothing() {
        Long id = 10L;
        Stage stage = prepareStage();
        when(stageRepository.getById(id)).thenReturn(stage);
        List<Task> deletedStagesTasks = stage.getTasks();

        stageService.deleteStage(id, FateOfTasksAfterDelete.CLOTHING, null);

        assertEquals(deletedStagesTasks.get(0).getStatus(), TaskStatus.CANCELLED);
        assertEquals(deletedStagesTasks.get(1).getStatus(), TaskStatus.CANCELLED);
    }

    @Test
    void testDeleteStageWhenTasksCascadeDelete() {
        Long id = 10L;
        Stage stage = prepareStage();
        List<Task> deletedStagesTasks = stage.getTasks();
        when(stageRepository.getById(id)).thenReturn(stage);

        stageService.deleteStage(id, FateOfTasksAfterDelete.CASCADE_DELETE, null);

        verify(taskRepository, times(1)).deleteAll(deletedStagesTasks);
        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    void testDeleteStageWhenTransferTasksAfterDeleteStageIfReceivingStageIdIsNull() {
        Long deletedStageId = 10L;

        assertThrows(DataValidationException.class,
                () -> stageService.deleteStage(deletedStageId,
                        FateOfTasksAfterDelete.TRANSFER_TO_ANOTHER_STAGE,
                        null));
    }

    @Test
    void testDeleteStageWhenTransferTasksAfterDeleteStageSuccessful() {
        Long deletedStageId = 10L;
        Long receivingStageId = 17L;
        Stage deletedStage = prepareStage();
        List<Task> transferredTasks = deletedStage.getTasks();
        Stage receivingStage = new Stage();
        receivingStage.setStageId(receivingStageId);
        when(stageRepository.getById(deletedStageId)).thenReturn(deletedStage);
        when(stageRepository.getById(receivingStageId)).thenReturn(receivingStage);

        stageService.deleteStage(deletedStageId,
                FateOfTasksAfterDelete.TRANSFER_TO_ANOTHER_STAGE,
                receivingStageId);

        assertEquals(transferredTasks, receivingStage.getTasks());
    }

    @Test
    void testGetExecutorsForRoleIsSuccessful() {
        StageDto dto = prepareStageDto();
        Stage stage = prepareStage();
        Project project = prepareProject();
        List<TeamMember> executors = prepareExecutorList();
        stage.setExecutors(executors);
        List<StageRolesDto> stageRolesDtoList = prepareStageRolesDto();
        dto.setStageRoles(stageRolesDtoList);
        List<StageRoles> stageRolesList = prepareStageRoles();
        stage.setStageRoles(stageRolesList);
        TeamMember firstExecutor = prepareExecutorList().get(0);
        TeamMember secondExecutor = prepareExecutorList().get(1);
        when(projectService.getProject(dto.getProjectId())).thenReturn(project);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(teamMembersService.findAllById(List.of(firstExecutor.getId(), secondExecutor.getId())))
                .thenReturn(List.of(firstExecutor, secondExecutor));
        when(stageMapperMock.toEntity(dto)).thenReturn(stage);
        when(stageMapperMock.toDto(stage)).thenReturn(dto);
        when(stageRolesMapperMock.toEntityList(stageRolesDtoList)).thenReturn(stageRolesList);
        when(stageRolesMapperMock.toDtoList(stageRolesList)).thenReturn(stageRolesDtoList);


        StageDto updatedStageDto = stageService.updateStage(dto);

        assertEquals(updatedStageDto.getStageRoles(), stageRolesDtoList);
    }

    @Test
    void testGetAllStages() {

        stageService.getAllStages();

        verify(stageRepository, times(1)).findAll();
    }

    @Test
    void testGetStage() {
        Long id = 12L;

        stageService.getStage(id);

        verify(stageRepository, times(1)).getById(id);
    }
}
