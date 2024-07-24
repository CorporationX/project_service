package faang.school.projectservice.valitator.initiative;

import faang.school.projectservice.dto.client.initiative.WriteInitiativeDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WriteInitiativeDtoValidatorTest {

    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private WriteInitiativeDtoValidator writeInitiativeDtoValidator;

    @Test
    void testValidateSuccess() {
        WriteInitiativeDto writeInitiativeDto = getWriteInitiativeDto();
        doReturn(getProject(InitiativeStatus.CLOSED)).when(projectService).findById(writeInitiativeDto.getProjectId());
        doReturn(getTeamMember(TeamRole.OWNER)).when(teamMemberService).findById(writeInitiativeDto.getCuratorId());

        assertDoesNotThrow(() -> writeInitiativeDtoValidator.validate(writeInitiativeDto));

        verify(teamMemberService, Mockito.times(1)).findById(writeInitiativeDto.getCuratorId());
        verify(projectService, Mockito.times(1)).findById(writeInitiativeDto.getProjectId());
    }

    @Test
    void sholdThrowExceptionIfAbsentRelevantRole() {
        WriteInitiativeDto writeInitiativeDto = getWriteInitiativeDto();
        doReturn(getProject(InitiativeStatus.CLOSED)).when(projectService).findById(writeInitiativeDto.getProjectId());
        doReturn(getTeamMember(TeamRole.ANALYST, TeamRole.MANAGER)).when(teamMemberService).findById(writeInitiativeDto.getCuratorId());

        assertAll(() -> {
            var exception = assertThrows(IllegalArgumentException.class, () -> writeInitiativeDtoValidator.validate(writeInitiativeDto));
            assertThat(exception.getMessage()).isEqualTo("No relevant Role");
        });
    }

    private WriteInitiativeDto getWriteInitiativeDto() {
        return new WriteInitiativeDto(
                3L,
                "222",
                "des",
                List.of(),
                InitiativeStatus.DONE,
                1L);
    }

    private Project getProject(InitiativeStatus initiativeStatus) {
        Project project = new Project();
        project.setInitiatives(List.of(Initiative.builder()
                .status(initiativeStatus)
                .build())
        );
        return project;
    }

    private TeamMember getTeamMember(TeamRole... owner) {
        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(owner));
        return teamMember;
    }
}