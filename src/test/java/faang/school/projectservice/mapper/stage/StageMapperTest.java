package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.NewStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stagerole.NewStageRolesDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageMapperImpl;
import faang.school.projectservice.mapper.StageRolesMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.StageStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class StageMapperTest {
    private final StageMapper stageMapper = new StageMapperImpl(new StageRolesMapperImpl());

    private Stage stageEntityWithId1;

    private StageDto stageDtoWithId1;

    private NewStageDto newStageDtoWithId1;

    @BeforeEach
    public void init() {
        long stageId1 = 1;

        long projectId1ForStageWithId1 = 1;

        long stageRolesId1ForStageWithId1 = 1;
        long stageRolesId2ForStageWithId1 = 2;
        long stageRolesId3ForStageWithId1 = 3;

        long taskId1ForStageWithId1 = 1;
        long taskId2ForStageWithId1 = 2;
        long taskId3ForStageWithId1 = 3;

        long executorId1ForStageWithId1 = 1;
        long executorId2ForStageWithId1 = 2;
        long executorId3ForStageWithId1 = 3;

        String stageNameForStageWithId1 = "a stage name for a stage with id 1";
        StageStatus stageStatusForStageWithId1 = StageStatus.IN_PROGRESS;

        Project projectWithId1ForStageWithId1 = Project.builder()
                .id(projectId1ForStageWithId1)
                .build();

        StageRoles stageRolesWithId1ForStageWithId1 = StageRoles.builder()
                .id(stageRolesId1ForStageWithId1)
                .teamRole(TeamRole.DEVELOPER)
                .count(5)
                .build();
        StageRoles stageRolesWithId2ForStageWithId1 = StageRoles.builder()
                .id(stageRolesId2ForStageWithId1)
                .teamRole(TeamRole.ANALYST)
                .count(6)
                .build();
        StageRoles stageRolesWithId3ForStageWithId1 = StageRoles.builder()
                .id(stageRolesId3ForStageWithId1)
                .teamRole(TeamRole.MANAGER)
                .count(7)
                .build();

        NewStageRolesDto newStageRolesDto1ForStageWithId1 = NewStageRolesDto.builder()
                .teamRole(TeamRole.DEVELOPER.toString())
                .count(5)
                .build();
        NewStageRolesDto newStageRolesDto2ForStageWithId1 = NewStageRolesDto.builder()
                .teamRole(TeamRole.ANALYST.toString())
                .count(6)
                .build();
        NewStageRolesDto newStageRolesDto3ForStageWithId1 = NewStageRolesDto.builder()
                .teamRole(TeamRole.MANAGER.toString())
                .count(7)
                .build();

        Task taskWithId1ForStageWithId1 = Task.builder()
                .id(taskId1ForStageWithId1)
                .build();
        Task taskWithId2ForStageWithId1 = Task.builder()
                .id(taskId2ForStageWithId1)
                .build();
        Task taskWithId3ForStageWithId1 = Task.builder()
                .id(taskId3ForStageWithId1)
                .build();

        TeamMember executorWithId1ForStageWithId1 = TeamMember.builder()
                .id(executorId1ForStageWithId1)
                .build();
        TeamMember executorWithId2ForStageWithId1 = TeamMember.builder()
                .id(executorId2ForStageWithId1)
                .build();
        TeamMember executorWithId3ForStageWithId1 = TeamMember.builder()
                .id(executorId3ForStageWithId1)
                .build();

        stageEntityWithId1 = Stage.builder()
                .stageId(stageId1)
                .stageName(stageNameForStageWithId1)
                .stageStatus(stageStatusForStageWithId1)
                .project(projectWithId1ForStageWithId1)
                .stageRoles(new ArrayList<>(List.of(
                        stageRolesWithId1ForStageWithId1,
                        stageRolesWithId2ForStageWithId1,
                        stageRolesWithId3ForStageWithId1)))
                .tasks(new ArrayList<>(List.of(
                        taskWithId1ForStageWithId1,
                        taskWithId2ForStageWithId1,
                        taskWithId3ForStageWithId1)))
                .executors(new ArrayList<>(List.of(
                        executorWithId1ForStageWithId1,
                        executorWithId2ForStageWithId1,
                        executorWithId3ForStageWithId1
                )))
                .build();

        stageDtoWithId1 = StageDto.builder()
                .stageId(stageId1)
                .stageName(stageNameForStageWithId1)
                .stageStatus(stageStatusForStageWithId1.toString())
                .projectId(projectId1ForStageWithId1)
                .stageRolesIds(new ArrayList<>(List.of(
                        stageRolesId1ForStageWithId1,
                        stageRolesId2ForStageWithId1,
                        stageRolesId3ForStageWithId1
                )))
                .tasksIds(new ArrayList<>(List.of(
                        taskId1ForStageWithId1,
                        taskId2ForStageWithId1,
                        taskId3ForStageWithId1
                )))
                .executorsIds(new ArrayList<>(List.of(
                        executorId1ForStageWithId1,
                        executorId2ForStageWithId1,
                        executorId3ForStageWithId1
                )))
                .build();

        newStageDtoWithId1 = NewStageDto.builder()
                .stageName(stageNameForStageWithId1)
                .stageStatus(stageStatusForStageWithId1.toString())
                .projectId(projectId1ForStageWithId1)
                .stageRoles(new ArrayList<>(List.of(
                        newStageRolesDto1ForStageWithId1,
                        newStageRolesDto2ForStageWithId1,
                        newStageRolesDto3ForStageWithId1
                )))
                .tasksIds(new ArrayList<>(List.of(
                        taskId1ForStageWithId1,
                        taskId2ForStageWithId1,
                        taskId3ForStageWithId1
                )))
                .executorsIds(new ArrayList<>(List.of(
                        executorId1ForStageWithId1,
                        executorId2ForStageWithId1,
                        executorId3ForStageWithId1
                )))
                .build();
    }

    //from StageEntity to StageDto
    @ParameterizedTest
    @ValueSource(longs = {0, 1, 3, 5, 30, 150, Long.MAX_VALUE})
    public void shouldMapStageEntityToStageDtoWithDifferentIds(long stageId) {
        stageDtoWithId1.setStageId(stageId);
        stageEntityWithId1.setStageId(stageId);

        StageDto expected = stageDtoWithId1;
        StageDto actual = stageMapper.toDto(stageEntityWithId1);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"name 1", "Name 2", "NAME 3"})
    public void shouldMapStageEntityToStageDtoWithDifferentStageNames(String stageName) {
        stageDtoWithId1.setStageName(stageName);
        stageEntityWithId1.setStageName(stageName);

        StageDto expected = stageDtoWithId1;
        StageDto actual = stageMapper.toDto(stageEntityWithId1);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @EnumSource(value = StageStatus.class, names = {"TODO", "IN_PROGRESS", "DONE", "CANCELLED"})
    public void shouldMapStageEntityToStageDtoWithDifferentStageStatus(StageStatus stageStatus) {
        stageDtoWithId1.setStageStatus(stageStatus.toString());
        stageEntityWithId1.setStageStatus(stageStatus);

        StageDto expected = stageDtoWithId1;
        StageDto actual = stageMapper.toDto(stageEntityWithId1);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 3, 5, 30, 150, Long.MAX_VALUE})
    public void shouldMapStageEntityToStageDtoWithDifferentProjectIds(long projectId) {
        stageDtoWithId1.setProjectId(projectId);
        var project = stageEntityWithId1.getProject();
        project.setId(projectId);

        StageDto expected = stageDtoWithId1;
        StageDto actual = stageMapper.toDto(stageEntityWithId1);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 3, 5, 30, 150, Long.MAX_VALUE})
    public void shouldMapStageEntityToStageDtoWithDifferentStageRolesIds(long stageRolesId) {
        stageDtoWithId1.setStageRolesIds(List.of(stageRolesId));
        var stageRoles = stageEntityWithId1.getStageRoles();
        stageRoles.clear();
        stageRoles.add(StageRoles.builder().id(stageRolesId).build());

        StageDto expected = stageDtoWithId1;
        StageDto actual = stageMapper.toDto(stageEntityWithId1);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 3, 5, 30, 150, Long.MAX_VALUE})
    public void shouldMapStageEntityToStageDtoWithDifferentTasksIds(long taskIds) {
        stageDtoWithId1.setTasksIds(List.of(taskIds));
        var tasks = stageEntityWithId1.getTasks();
        tasks.clear();
        tasks.add(Task.builder().id(taskIds).build());

        StageDto expected = stageDtoWithId1;
        StageDto actual = stageMapper.toDto(stageEntityWithId1);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 3, 5, 30, 150, Long.MAX_VALUE})
    public void shouldMapStageEntityToStageDtoWithDifferentExecutorsIds(long executorsIds) {
        stageDtoWithId1.setExecutorsIds(List.of(executorsIds));
        var executors = stageEntityWithId1.getExecutors();
        executors.clear();
        executors.add(TeamMember.builder().id(executorsIds).build());

        StageDto expected = stageDtoWithId1;
        StageDto actual = stageMapper.toDto(stageEntityWithId1);

        assertEquals(expected, actual);
    }

    //from NewStageDto to StageEntity
    @ParameterizedTest
    @ValueSource(strings = {"name 1", "Name 2", "NAME 3"})
    public void shouldMapNewStageDtoToStageEntityWithDifferentStageNames(String stageName) {
        newStageDtoWithId1.setStageName(stageName);
        stageEntityWithId1.setStageName(stageName);
        stageEntityWithId1.setStageId(null);
        stageEntityWithId1.setProject(null);
        stageEntityWithId1.setTasks(null);
        stageEntityWithId1.setExecutors(null);
        var stageRoles = stageEntityWithId1.getStageRoles();
        stageRoles.forEach(r -> r.setId(null));

        Stage expected = stageEntityWithId1;
        Stage actual = stageMapper.toEntity(newStageDtoWithId1);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @EnumSource(value = StageStatus.class, names = {"TODO", "IN_PROGRESS", "DONE", "CANCELLED"})
    public void shouldMapNewStageDtoToStageEntityWithDifferentStageStatus(StageStatus stageStatus) {
        newStageDtoWithId1.setStageStatus(stageStatus.toString());
        stageEntityWithId1.setStageStatus(stageStatus);
        stageEntityWithId1.setStageId(null);
        stageEntityWithId1.setProject(null);
        stageEntityWithId1.setTasks(null);
        stageEntityWithId1.setExecutors(null);
        var stageRoles = stageEntityWithId1.getStageRoles();
        stageRoles.forEach(r -> r.setId(null));

        Stage expected = stageEntityWithId1;
        Stage actual = stageMapper.toEntity(newStageDtoWithId1);

        assertEquals(expected, actual);
    }
}
