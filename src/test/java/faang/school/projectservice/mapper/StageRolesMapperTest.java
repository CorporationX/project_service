package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StageRolesMapperTest {

    @Spy
    private StageRolesMapperImpl stageRolesMapper;

    private StageRoles stageRoles;

    private StageRolesDto stageRolesDto;

    private List<StageRoles> stageRolesList;

    private List<StageRolesDto> stageRolesDtosList;

    @BeforeEach
    void setUp() {
        stageRoles = StageRoles.builder()
                .id(1L)
                .teamRole(TeamRole.DEVELOPER)
                .count(3)
                .build();

        stageRolesDto = StageRolesDto.builder()
                .id(1L)
                .teamRole(TeamRole.DEVELOPER)
                .count(3)
                .build();
        stageRolesList = List.of(stageRoles);
        stageRolesDtosList = List.of(stageRolesDto);
    }

    @Test
    void toEntity() {
        StageRoles actual = stageRolesMapper.toEntity(stageRolesDto);
        assertEquals(stageRoles, actual);
    }

    @Test
    void toDto() {
        StageRolesDto actual = stageRolesMapper.toDto(stageRoles);
        assertEquals(stageRolesDto, actual);
    }
}