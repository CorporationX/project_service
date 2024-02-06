package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.Test;

import java.util.List;

import static faang.school.projectservice.model.TeamRole.ANALYST;
import static faang.school.projectservice.model.TeamRole.DESIGNER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StageRolesMapperTest {

    private StageRolesMapper stageRolesMapper = new StageRolesMapperImpl();

    @Test
    void testToEntity() {
        // Arrange
        StageRolesDto stageRolesDto = StageRolesDto.builder()
                .id(2L)
                .teamRole(ANALYST)
                .stageId(3L)
                .count(4).build();

        // Act
        StageRoles stageRoles = stageRolesMapper.toEntity(stageRolesDto);

        // Assert
        assertAll(
                () -> assertEquals(stageRoles.getId(), stageRolesDto.getId()),
                () -> assertEquals(stageRoles.getStage().getStageId(), stageRolesDto.getStageId()),
                () -> assertEquals(stageRoles.getCount(), stageRolesDto.getCount()),
                () -> assertEquals(stageRoles.getTeamRole(), stageRolesDto.getTeamRole()));
    }

    @Test
    void testToDto() {
        // Arrange
        StageRoles stageRoles = StageRoles.builder()
                .id(2L)
                .teamRole(ANALYST)
                .stage(Stage.builder().stageId(3L).build())
                .count(4).build();

        // Act
        StageRolesDto stageRolesDto = stageRolesMapper.toDto(stageRoles);

        // Assert
        assertAll(
                () -> assertEquals(stageRoles.getId(), stageRolesDto.getId()),
                () -> assertEquals(stageRoles.getStage().getStageId(), stageRolesDto.getStageId()),
                () -> assertEquals(stageRoles.getCount(), stageRolesDto.getCount()),
                () -> assertEquals(stageRoles.getTeamRole(), stageRolesDto.getTeamRole()));

    }

    @Test
    void testToList() {
        // Arrange
        List<StageRolesDto> rolesDto = List.of(
                StageRolesDto.builder()
                        .id(10L).teamRole(ANALYST)
                        .count(3)
                        .stageId(3L).build(),
                StageRolesDto.builder()
                        .id(20L)
                        .teamRole(DESIGNER)
                        .count(2)
                        .stageId(3L).build());

        // Act
        List<StageRoles> roles = stageRolesMapper.toList(rolesDto);

        // Assert
        assertAll(
                () -> assertEquals(roles.size(), rolesDto.size()),
                () -> assertEquals(roles.get(0).getId(), rolesDto.get(0).getId()),
                () -> assertEquals(roles.get(0).getTeamRole(), rolesDto.get(0).getTeamRole()),
                () -> assertEquals(roles.get(0).getCount(), rolesDto.get(0).getCount()),
                () -> assertEquals(roles.get(0).getStage().getStageId(), rolesDto.get(0).getStageId())

        );

    }

    @Test
    void testToListDto() {
        // Arrange
        List<StageRoles> roles = List.of(
                StageRoles.builder()
                        .id(10L)
                        .teamRole(ANALYST)
                        .count(3)
                        .stage(Stage.builder().stageId(3L).build()).build(),
                StageRoles.builder()
                        .id(20L)
                        .teamRole(DESIGNER)
                        .count(2)
                        .stage(Stage.builder().stageId(3L).build()).build());

        // Act
        List<StageRolesDto> rolesDto = stageRolesMapper.toListDTO(roles);

        // Assert
        assertAll(
                () -> assertEquals(roles.size(), rolesDto.size()),
                () -> assertEquals(roles.get(0).getId(), rolesDto.get(0).getId()),
                () -> assertEquals(roles.get(0).getTeamRole(), rolesDto.get(0).getTeamRole()),
                () -> assertEquals(roles.get(0).getCount(), rolesDto.get(0).getCount()),
                () -> assertEquals(roles.get(0).getStage().getStageId(), rolesDto.get(0).getStageId())

        );
    }
}