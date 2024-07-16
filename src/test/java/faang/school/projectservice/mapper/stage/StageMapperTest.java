package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StageMapperTest {

    @InjectMocks
    private final StageMapperImpl stageMapper = new StageMapperImpl();

    @Spy
    private final StageRolesMapper stageRolesMapper = new StageRolesMapperImpl();

    private Stage stage;
    private StageDto stageDto;

    @BeforeEach
    void setUp() {
        stage = Stage.builder()
                .stageId(1L)
                .stageName("Some name")
                .project(Project.builder()
                        .id(123L)
                        .build())
                .stageRoles(List.of(
                        StageRoles.builder()
                                .id(1L)
                                .stage(Stage.builder().stageId(1L).build())
                                .teamRole(TeamRole.DEVELOPER)
                                .count(2)
                                .build(),
                        StageRoles.builder()
                                .id(2L)
                                .stage(Stage.builder().stageId(2L).build())
                                .teamRole(TeamRole.DESIGNER)
                                .count(1)
                                .build()))
                .build();

        stageDto = StageDto.builder()
                .stageId(1L)
                .stageName("Some name")
                .stageRoles(List.of(
                        StageRolesDto.builder()
                                .id(1L)
                                .stageId(1L)
                                .teamRole(TeamRole.DEVELOPER)
                                .count(2)
                                .build(),
                        StageRolesDto.builder()
                                .id(2L)
                                .stageId(2L)
                                .teamRole(TeamRole.DESIGNER)
                                .count(1)
                                .build()
                ))
                .projectId(123L)
                .build();
    }

    @Test
    void testToStageEntity() {
        Stage stageMapped = stageMapper.toStageEntity(stageDto);

        assertEquals(stage.getStageId(), stageMapped.getStageId());
        assertEquals(stage.getStageName(), stageMapped.getStageName());
        assertEquals(stage.getProject().getId(), stageMapped.getProject().getId());
        assertEquals(stage.getStageRoles(), stageMapped.getStageRoles());
    }

    @Test
    void testToStageDto() {
        StageDto stageDtoMapped = stageMapper.toStageDto(stage);

        assertEquals(stageDto.getStageId(), stageDtoMapped.getStageId());
        assertEquals(stageDto.getStageName(), stageDtoMapped.getStageName());
        assertEquals(stageDto.getProjectId(), stageDtoMapped.getProjectId());
        assertEquals(stageDto.getStageRoles(), stageDtoMapped.getStageRoles());
    }
}