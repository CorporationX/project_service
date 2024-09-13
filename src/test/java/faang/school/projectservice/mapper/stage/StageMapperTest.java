package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.mapper.team.TeamMemberMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
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
    StageMapperImpl stageMapper;
    @Spy
    StageRolesMapperImpl stageRolesMapper;
    @Spy
    TeamMemberMapperImpl teamMemberMapper;

    StageDto stageDto = StageDto.builder()
            .stageName("name").projectId(1L)
                .tasks(List.of(1L, 2L))
            .stageRoles(List.of(
            StageRolesDto.builder().teamRole(TeamRole.DEVELOPER.name()).count(2).build(),
                        StageRolesDto.builder().teamRole(TeamRole.MANAGER.name()).count(1).build()
                        ))
                                .executors(List.of(
            TeamMemberDto.builder().id(1L).userId(1L).roles(
            List.of(TeamRole.DEVELOPER, TeamRole.MANAGER)
                        ).build(),
                        TeamMemberDto.builder().id(2L).userId(2L).roles(
            List.of(TeamRole.DEVELOPER)
                        ).build()
                ))
                        .build();
    Stage stage = Stage.builder()
            .stageName("name")
                .project(Project.builder().id(1L).build())
            .tasks(List.of(
            Task.builder().id(1L).build(),
                        Task.builder().id(2L).build()
                ))
                        .stageRoles(List.of(
            StageRoles.builder().teamRole(TeamRole.DEVELOPER).count(2).build(),
                                StageRoles.builder().teamRole(TeamRole.MANAGER).count(1).build()
                        ))
                                .executors(List.of(
            TeamMember.builder().id(1L).userId(1L).roles(
            List.of(TeamRole.DEVELOPER, TeamRole.MANAGER)
                                ).build(),
                                TeamMember.builder().id(2L).userId(2L).roles(
            List.of(TeamRole.DEVELOPER)
                                ).build()
                ))
                        .build();

    @Test
    void toDto() {
        assertEquals(stageMapper.toDto(stage), stageDto);
    }

    @Test
    void toEntity() {
        assertEquals(stageMapper.toEntity(stageDto), stage);
    }
}