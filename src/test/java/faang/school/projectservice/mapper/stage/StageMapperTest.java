package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.projectservice.model.TeamRole.ANALYST;
import static faang.school.projectservice.model.TeamRole.DESIGNER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StageMapperTest {

    @Spy
    private StageRolesMapper stageRolesMapper = new StageRolesMapperImpl();
    @InjectMocks
    private StageMapper stageMapper = new StageMapperImpl();

    @Test
    void testToEntity() {
        // Arrange
        List<StageRolesDto> stageRolesDtos = List.of(
                StageRolesDto.builder()
                        .id(10L).teamRole(ANALYST)
                        .count(3)
                        .stageId(3L).build(),
                StageRolesDto.builder()
                        .id(20L)
                        .teamRole(DESIGNER)
                        .count(2)
                        .stageId(3L).build());
        StageDto stageDto = StageDto.builder()
                .stageId(3L)
                .stageName("Explore")
                .projectId(1L)
                .stageRolesDto(stageRolesDtos).build();

        // Act
        Stage stage = stageMapper.toEntity(stageDto);

        // Assert
        assertAll(
                () -> assertEquals(stage.getStageId(), stageDto.getStageId()),
                () -> assertEquals(stage.getStageName(), stageDto.getStageName()),
                () -> assertEquals(stage.getProject().getId(), stageDto.getProjectId()),
                () -> assertEquals(stage.getStageRoles().get(0).getId(), stageDto.getStageRolesDto().get(0).getId())
        );
    }

    @Test
    void testToDto() {
        // Arrange
        List<StageRoles> stageRoles = List.of(
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
        Stage stage = Stage.builder()
                .stageId(3L)
                .stageName("Explore")
                .project(Project.builder().id(1L).build())
                .stageRoles(stageRoles).build();


        // Act
        StageDto stageDto = stageMapper.toDto(stage);

        // Assert
        assertAll(
                ()-> assertEquals(stageDto.getStageId(),stage.getStageId()),
                ()-> assertEquals(stageDto.getStageName(),stage.getStageName()),
                ()-> assertEquals(stageDto.getProjectId(),stage.getProject().getId()),
                ()-> assertEquals(stageDto.getStageRolesDto().get(0).getId(),stage.getStageRoles().get(0).getId())
        );
    }
}