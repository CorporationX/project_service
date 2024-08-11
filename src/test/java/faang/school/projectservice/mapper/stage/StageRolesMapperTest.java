package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StageRolesMapperTest {

    @InjectMocks
    private StageRolesMapperImpl mapper;

    private StageRolesDto stageRolesDto = StageRolesDto.builder()
            .teamRole(DEVELOPER.name())
            .count(1)
            .build();

    private StageRoles stageRoles = StageRoles.builder()
            .teamRole(DEVELOPER)
            .count(1)
            .build();

    @Test
    void toDto() {
        assertEquals(mapper.toDto(stageRoles), stageRolesDto);
    }

    @Test
    void toEntity() {
        assertEquals(mapper.toEntity(stageRolesDto), stageRoles);
    }
}