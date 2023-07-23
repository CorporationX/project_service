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
    @Spy
    private StageRolesMapperImpl stageRolesMapper;

    private Stage stage;
    private StageDto stageDto;
    StageRoles stageRoles1;
    StageRoles stageRoles2;

    StageRolesDto stageRolesDto1;
    StageRolesDto stageRolesDto2;

    @BeforeEach
    void setUp() {
        stageRoles1 = StageRoles.builder().id(1L).teamRole(TeamRole.OWNER).count(1).build();
        stageRoles2 = StageRoles.builder().id(2L).teamRole(TeamRole.MANAGER).count(2).build();

        stageRolesDto1 = StageRolesDto.builder().id(1L).teamRole("OWNER").count(1).build();
        stageRolesDto2 = StageRolesDto.builder().id(2L).teamRole("MANAGER").count(2).build();

        stage = Stage.builder()
                .stageId(1L)
                .stageName("stageName")
                .project(Project.builder().id(1L).build())
                .status(StageStatus.CREATED)
                .stageRoles(List.of(stageRoles1, stageRoles2))
                .build();

        stageDto = StageDto.builder()
                .stageId(1L)
                .stageName("stageName")
                .projectId(1L)
                .status("CREATED")
                .stageRolesDto(List.of(stageRolesDto1, stageRolesDto2))
                .build();
    }

    @Test
    void toDto() {
        StageRolesDto actualRolesDto1 = stageRolesMapper.toDto(stageRoles1);
        StageRolesDto actualRolesDto2 = stageRolesMapper.toDto(stageRoles2);
        StageDto actualDto = stageMapper.toDto(stage);
        assertEquals(stageDto, actualDto);
        assertAll(() -> {
            assertEquals(stageDto.getStageId(), actualDto.getStageId());
            assertEquals(stageDto.getStageName(), actualDto.getStageName());
            assertEquals(stageDto.getProjectId(), actualDto.getProjectId());
            assertEquals(stageDto.getStatus(), actualDto.getStatus());
            assertEquals(stageRolesDto1, actualRolesDto1);
            assertEquals(stageRolesDto2, actualRolesDto2);
        });
    }

    @Test
    void toEntity() {
        StageRoles actualRoles1 = stageRolesMapper.toEntity(stageRolesDto1);
        StageRoles actualRoles2 = stageRolesMapper.toEntity(stageRolesDto2);
        Stage actualStage = stageMapper.toEntity(stageDto);
        assertEquals(stage, actualStage);
        assertAll(() -> {
            assertEquals(stage.getStageId(), actualStage.getStageId());
            assertEquals(stage.getStageName(), actualStage.getStageName());
            assertEquals(stage.getProject().getId(), actualStage.getProject().getId());
            assertEquals(stage.getStatus(), actualStage.getStatus());
            assertEquals(stageRoles1, actualRoles1);
            assertEquals(stageRoles2, actualRoles2);
        });
    }
}