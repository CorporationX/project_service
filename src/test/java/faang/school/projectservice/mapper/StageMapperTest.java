package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage_roles.StageRolesDto;
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

    private StageRoles stageRoles1;
    private StageRoles stageRoles2;

    private StageRolesDto stageRolesDto1;
    private StageRolesDto stageRolesDto2;

    @BeforeEach
    void setUp() {
        stageRoles1 = StageRoles.builder().teamRole(TeamRole.OWNER).count(1).build();
        stageRoles2 = StageRoles.builder().teamRole(TeamRole.MANAGER).count(2).build();

        stageRolesDto1 = StageRolesDto.builder().teamRole(TeamRole.OWNER).count(1).build();
        stageRolesDto2 = StageRolesDto.builder().teamRole(TeamRole.MANAGER).count(2).build();

        stage = Stage.builder()
                .stageName("stageName")
                .project(Project.builder().id(1L).build())
                .status(StageStatus.CREATED)
                .stageRoles(List.of(stageRoles1, stageRoles2))
                .build();

        stageDto = StageDto.builder()
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
            assertEquals(stage.getStageName(), actualStage.getStageName());
            assertEquals(stage.getProject().getId(), actualStage.getProject().getId());
            assertEquals(stage.getStatus(), actualStage.getStatus());
            assertEquals(stageRoles1, actualRoles1);
            assertEquals(stageRoles2, actualRoles2);
        });
    }
}