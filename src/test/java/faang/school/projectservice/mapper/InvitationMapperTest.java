package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class InvitationMapperTest {

    @Spy
    private InvitationMapperImpl skillMapper;

    private StageInvitationDto stageInvitationDto;
    private StageInvitation stageInvitation;

    @BeforeEach
    public void setUp(){
        TeamMember teamMember = TeamMember.builder().id(2L).userId(1L).build();
        Stage stage = Stage.builder().stageId(1L).stageName("Name").executors(List.of(teamMember)).build();
        stageInvitationDto = StageInvitationDto.builder().id(1L).stage(stage).author(teamMember)
                .invited(teamMember).status(StageInvitationStatus.REJECTED).explanation("text").build();

        stageInvitation = StageInvitation.builder().id(1L).description("text").status(StageInvitationStatus.REJECTED)
                .stage(stage).author(teamMember).invited(teamMember).build();
    }

    @Test
    public void testToEntityMapper() {
        assertEquals(stageInvitationDto.getId(), stageInvitation.getId());
        assertEquals(stageInvitationDto.getStage(), stageInvitation.getStage());
        assertEquals(stageInvitationDto.getAuthor(), stageInvitation.getAuthor());
        assertEquals(stageInvitationDto.getInvited(), stageInvitation.getInvited());
        assertEquals(stageInvitationDto.getStatus(), stageInvitation.getStatus());
    }

    @Test
    public void testToDtoMapper() {
        assertEquals(stageInvitation.getId(), stageInvitationDto.getId());
        assertEquals(stageInvitation.getStage(), stageInvitationDto.getStage());
        assertEquals(stageInvitation.getAuthor(), stageInvitationDto.getAuthor());
        assertEquals(stageInvitation.getInvited(), stageInvitationDto.getInvited());
        assertEquals(stageInvitation.getStatus(), stageInvitationDto.getStatus());
    }
}
