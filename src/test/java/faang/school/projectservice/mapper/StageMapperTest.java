package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StageMapperTest {

    @Spy
    private StageRolesMapperImpl stageRolesMapper;

    @Spy
    private StageMapperImpl stageMapper;

    private Stage stage;

    private StageDto stageDto;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stageMapper, "stageRolesMapper", stageRolesMapper);

        stageDto = StageDto.builder()
                .stageId(1L)
                .stageName("stageName")
                .projectId(1L)
                .stageRoles(List.of(
                        StageRolesDto.builder()
                                .id(1L)
                                .teamRole(TeamRole.DEVELOPER)
                                .count(3)
                                .build(),
                        StageRolesDto.builder()
                                .id(2L)
                                .teamRole(TeamRole.DESIGNER)
                                .count(3)
                                .build(),
                        StageRolesDto.builder()
                                .id(3L)
                                .teamRole(TeamRole.TESTER)
                                .count(3)
                                .build()
                ))
                .build();

        stage = Stage.builder()
                .stageId(1L)
                .stageName("stageName")
                .project(Project.builder().id(1L).build())
                .stageRoles(List.of(
                        StageRoles.builder()
                                .id(1L)
                                .teamRole(TeamRole.DEVELOPER)
                                .count(3)
                                .build(),
                        StageRoles.builder()
                                .id(2L)
                                .teamRole(TeamRole.DESIGNER)
                                .count(3)
                                .build(),
                        StageRoles.builder()
                                .id(3L)
                                .teamRole(TeamRole.TESTER)
                                .count(3)
                                .build()
                ))
                .build();
    }

    @Test
    void toDto() {
        StageDto actual = stageMapper.toDto(stage);
        assertEquals(stageDto, actual);
    }

    @Test
    void toEntity() {
        Stage actual = stageMapper.toEntity(stageDto);
        assertEquals(stage, actual);
    }
}