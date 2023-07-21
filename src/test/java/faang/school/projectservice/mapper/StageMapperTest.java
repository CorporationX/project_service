package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage.StageStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(MockitoExtension.class)
class StageMapperTest {
    @Spy
    private StageMapperImpl stageMapper;
    private StageRolesMapper stageRolesMapper;

    Stage stage;
    StageDto stageDto;

    @BeforeEach
    void setUp() {
        stage = Stage.builder()
                .stageId(1L)
                .stageName("stageName")
                .project(Project.builder().id(1L).build())
                .status(StageStatus.CREATED)
                .stageRoles(List.of(StageRoles.builder().id(1L).teamRole(TeamRole.OWNER).count(1).stage(Stage.builder().stageId(1L).build()).build()))
                .build();

        stageDto = StageDto.builder()
                .stageId(1L)
                .stageName("stageName")
                .projectId(1L)
                .status("CREATED")
                .stageRolesDto(List.of(StageRolesDto.builder().id(2L).role("OWNER").count(1).stageId(1L).build()))
                .build();
    }

    @Test
    void toDto() {
        StageDto actualDto = stageMapper.toDto(stage);
        assertAll(() -> {
            assertEquals(stageDto.getStageId(), actualDto.getStageId());
            assertEquals(stageDto.getStageName(), actualDto.getStageName());
            assertEquals(stageDto.getProjectId(), actualDto.getProjectId());
            assertEquals(stageDto.getStatus(), actualDto.getStatus());
        });
    }

    @Test
    void toEntity() {
        Stage actualStage = stageMapper.toEntity(stageDto);
        assertEquals(stage, actualStage);
    }

}