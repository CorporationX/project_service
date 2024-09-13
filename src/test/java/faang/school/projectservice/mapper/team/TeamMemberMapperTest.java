package faang.school.projectservice.mapper.team;

import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.mapper.stage.StageRolesMapperImpl;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TeamMemberMapperTest {
    @InjectMocks
    private TeamMemberMapperImpl mapper;

    TeamMember teamMember = TeamMember.builder().id(1L).userId(1L).roles(
            List.of(TeamRole.DEVELOPER, TeamRole.MANAGER)
        ).build();

    TeamMemberDto teamMemberDto = TeamMemberDto.builder().id(1L).userId(1L).roles(
            List.of(TeamRole.DEVELOPER, TeamRole.MANAGER)
                        ).build();

    @Test
    void toDto() {
        assertEquals(mapper.toDto(teamMember), teamMemberDto);
    }

    @Test
    void toEntity() {
        assertEquals(mapper.toEntity(teamMemberDto), teamMember);
    }
}