package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StageRolesMapperTest {

    @Spy
    private faang.school.projectservice.mapper.StageRolesMapperImpl stageRolesMapper;

    private StageRoles stageRoles;

    private StageRolesDto stageRolesDto;

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