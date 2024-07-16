package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;


class StageRolesMapperTest {

    private final StageRolesMapper stageRolesMapper = Mappers.getMapper(StageRolesMapper.class);

    private StageRoles stageRoles;
    private StageRolesDto stageRolesDto;

    @BeforeEach
    void setUp() {
        stageRoles = StageRoles.builder()
                .id(1L)
                .teamRole(TeamRole.DEVELOPER)
                .count(2)
                .stage(Stage.builder()
                        .stageId(1L)
                        .build())
                .build();

        stageRolesDto = StageRolesDto.builder()
                .id(1L)
                .teamRole(TeamRole.DEVELOPER)
                .count(2)
                .stageId(1L)
                .build();
    }

    @Test
    void testToStageRoles() {
        StageRoles stageRolesMapped = stageRolesMapper.toStageRoles(stageRolesDto);

        assertEquals(stageRoles.getId(), stageRolesMapped.getId());
        assertEquals(stageRoles.getCount(), stageRolesMapped.getCount());
        assertEquals(stageRoles.getStage(), stageRolesMapped.getStage());
        assertEquals(stageRoles.getTeamRole(), stageRolesMapped.getTeamRole());
    }

    @Test
    void testToStageRolesDto() {
        StageRolesDto stageRolesDtoMapped = stageRolesMapper.toStageRolesDto(stageRoles);

        assertEquals(stageRolesDto.getId(), stageRolesDtoMapped.getId());
        assertEquals(stageRolesDto.getCount(), stageRolesDtoMapped.getCount());
        assertEquals(stageRolesDto.getStageId(), stageRolesDtoMapped.getStageId());
        assertEquals(stageRolesDto.getTeamRole(), stageRolesDtoMapped.getTeamRole());
    }
}