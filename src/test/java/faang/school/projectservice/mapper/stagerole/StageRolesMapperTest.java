package faang.school.projectservice.mapper.stagerole;

import faang.school.projectservice.dto.stagerole.NewStageRolesDto;
import faang.school.projectservice.dto.stagerole.StageRolesDto;
import faang.school.projectservice.mapper.StageRolesMapperImpl;
import faang.school.projectservice.model.StageStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class StageRolesMapperTest {
    @Spy
    private StageRolesMapperImpl mapper;

    private long stageRolesId1;
    private TeamRole teamRoleOfStageRolesWithId1;
    private int countOfStageRolesWithId1;
    private StageRoles stageRolesEntityWithId1;
    private StageRolesDto stageRolesDtoWithId1;
    private NewStageRolesDto newStageRolesDtoWithId1;
    private long stageId1;

    @BeforeEach
    public void init() {


        stageId1 = 1;
        String stageNameOfStageWithId1 = "stageNameOfStageWithId1";
        StageStatus stageStatusOfStageWithId1 = StageStatus.TODO;
        Stage stageEntityWithId1 = Stage.builder()
                .stageId(stageId1)
                .stageName(stageNameOfStageWithId1)
                .stageStatus(stageStatusOfStageWithId1)
                .build();

        stageRolesId1 = 1;
        teamRoleOfStageRolesWithId1 = TeamRole.DEVELOPER;
        countOfStageRolesWithId1 = 4;
        stageRolesEntityWithId1 = StageRoles.builder()
                .id(stageRolesId1)
                .teamRole(teamRoleOfStageRolesWithId1)
                .count(countOfStageRolesWithId1)
                .stage(stageEntityWithId1)
                .build();

        stageRolesDtoWithId1 = StageRolesDto.builder()
                .id(stageRolesId1)
                .teamRole(teamRoleOfStageRolesWithId1.toString())
                .count(countOfStageRolesWithId1)
                .stageId(stageEntityWithId1.getStageId())
                .build();

        newStageRolesDtoWithId1 = NewStageRolesDto.builder()
                .teamRole(teamRoleOfStageRolesWithId1.toString())
                .count(countOfStageRolesWithId1)
                .build();
    }

    // to StageRolesDto from StageEntity
    @ParameterizedTest
    @EnumSource(value = TeamRole.class,
            names = {"OWNER", "MANAGER", "DEVELOPER", "DESIGNER", "TESTER", "ANALYST", "INTERN"})
    public void shouldMapStageRolesEntityToStageRolesDtoWithDifferentTeamRoles(TeamRole teamRole) {
        StageRolesDto expected = StageRolesDto.builder()
                .id(stageRolesId1)
                .teamRole(teamRole.toString())
                .count(countOfStageRolesWithId1)
                .stageId(stageId1)
                .build();

        stageRolesEntityWithId1.setTeamRole(teamRole);
        StageRolesDto actual = mapper.toDto(stageRolesEntityWithId1);

        assertEquals(expected, actual);
    }


    @ParameterizedTest
    @ValueSource(longs = {1, 3, 5, 30, 15, Long.MAX_VALUE})
    public void shouldMapStageRolesEntityToStageRolesDtoWithDifferentIds(long stageRolesId) {
        StageRolesDto expected = StageRolesDto.builder()
                .id(stageRolesId)
                .teamRole(teamRoleOfStageRolesWithId1.toString())
                .count(countOfStageRolesWithId1)
                .stageId(stageId1)
                .build();

        stageRolesEntityWithId1.setId(stageRolesId);
        StageRolesDto actual = mapper.toDto(stageRolesEntityWithId1);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 30, 120, Integer.MAX_VALUE})
    public void shouldMapStageRolesEntityToStageRolesDtoWithDifferentCounts(int count) {
        StageRolesDto expected = StageRolesDto.builder()
                .id(stageRolesId1)
                .teamRole(teamRoleOfStageRolesWithId1.toString())
                .count(count)
                .stageId(stageId1)
                .build();

        stageRolesEntityWithId1.setCount(count);
        StageRolesDto actual = mapper.toDto(stageRolesEntityWithId1);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 3, 5, 30, 15, Long.MAX_VALUE})
    public void shouldMapStageRolesEntityToStageRolesDtoWithDifferentStageIds(long stageId) {
        StageRolesDto expected = StageRolesDto.builder()
                .id(stageRolesId1)
                .teamRole(teamRoleOfStageRolesWithId1.toString())
                .count(countOfStageRolesWithId1)
                .stageId(stageId)
                .build();

        var stage = stageRolesEntityWithId1.getStage();
        stage.setStageId(stageId);
        StageRolesDto actual = mapper.toDto(stageRolesEntityWithId1);

        assertEquals(expected, actual);
    }

    // to StageEntity from StageRolesDto
    @ParameterizedTest
    @EnumSource(value = TeamRole.class,
            names = {"OWNER", "MANAGER", "DEVELOPER", "DESIGNER", "TESTER", "ANALYST", "INTERN"})
    public void shouldMapStageRolesDtoToStageRolesEntityWithDifferentTeamRoles(TeamRole teamRole) {
        StageRoles expected = StageRoles.builder()
                .id(stageRolesId1)
                .teamRole(teamRole)
                .stage(Stage.builder().stageId(stageId1).build())
                .count(countOfStageRolesWithId1)
                .build();

        stageRolesDtoWithId1.setTeamRole(teamRole.toString());
        StageRoles actual = mapper.toEntity(stageRolesDtoWithId1);

        assertEquals(expected, actual);
    }


    @ParameterizedTest
    @ValueSource(longs = {1, 3, 5, 30, 15, Long.MAX_VALUE})
    public void shouldMapStageRolesDtoToStageRolesEntityWithDifferentIds(long stageRolesId) {
        StageRoles expected = StageRoles.builder()
                .id(stageRolesId)
                .stage(Stage.builder().stageId(stageId1).build())
                .teamRole(teamRoleOfStageRolesWithId1)
                .count(countOfStageRolesWithId1)
                .build();

        stageRolesDtoWithId1.setId(stageRolesId);
        StageRoles actual = mapper.toEntity(stageRolesDtoWithId1);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 30, 120, Integer.MAX_VALUE})
    public void shouldMapStageRolesDtoToStageRolesEntityWithDifferentCounts(int count) {
        StageRoles expected = StageRoles.builder()
                .id(stageRolesId1)
                .stage(Stage.builder().stageId(stageId1).build())
                .teamRole(teamRoleOfStageRolesWithId1)
                .count(count)
                .build();

        stageRolesDtoWithId1.setCount(count);
        StageRoles actual = mapper.toEntity(stageRolesDtoWithId1);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 3, 5, 30, 15, Long.MAX_VALUE})
    public void shouldMapStageRolesDtoToStageRolesEntityWithStageNull(long stageId) {
        StageRoles expected = StageRoles.builder()
                .id(stageRolesId1)
                .stage(Stage.builder().stageId(stageId).build())
                .teamRole(teamRoleOfStageRolesWithId1)
                .count(countOfStageRolesWithId1)
                .build();

        stageRolesDtoWithId1.setStageId(stageId);
        StageRoles actual = mapper.toEntity(stageRolesDtoWithId1);

        assertEquals(expected, actual);
    }

    // to StageEntity from NewStageRolesDto
    @ParameterizedTest
    @EnumSource(value = TeamRole.class,
            names = {"OWNER", "MANAGER", "DEVELOPER", "DESIGNER", "TESTER", "ANALYST", "INTERN"})
    public void shouldMapNewStageRolesDtoToStageRolesEntityWithDifferentTeamRoles(TeamRole teamRole) {
        StageRoles expected = StageRoles.builder()
                .id(null)
                .teamRole(teamRole)
                .count(countOfStageRolesWithId1)
                .stage(null)
                .build();

        newStageRolesDtoWithId1.setTeamRole(teamRole.toString());
        StageRoles actual = mapper.toEntity(newStageRolesDtoWithId1);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 30, 120, Integer.MAX_VALUE})
    public void shouldMapNewStageRolesDtoToStageRolesEntityWithDifferentCounts(int count) {
        StageRoles expected = StageRoles.builder()
                .id(null)
                .teamRole(teamRoleOfStageRolesWithId1)
                .count(count)
                .stage(null)
                .build();

        newStageRolesDtoWithId1.setCount(count);
        StageRoles actual = mapper.toEntity(newStageRolesDtoWithId1);

        assertEquals(expected, actual);
    }
}