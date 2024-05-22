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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class InvitationMapperTest {

    @Spy
    private InvitationMapperImpl invitationMapper;

    private StageInvitationDto stageInvitationDto;
    private StageInvitation stageInvitation;

    @BeforeEach
    public void setUp(){
        TeamMember teamMember = TeamMember.builder().id(2L).userId(1L).build();
        Stage stage = Stage.builder().stageId(1L).stageName("Name").executors(List.of(teamMember)).build();
        stageInvitationDto = StageInvitationDto.builder().id(1L).stageId(1L).authorId(1L)
                .invitedId(1L).status(StageInvitationStatus.REJECTED).explanation("text").build();

        stageInvitation = StageInvitation.builder().id(1L).description("text").status(StageInvitationStatus.REJECTED)
                .stage(stage).author(teamMember).invited(teamMember).build();
    }

    @Test
    public void testToEntityMapper() {
        StageInvitation entity = invitationMapper.toEntity(stageInvitationDto);

        assertThat(entity.getId()).isEqualTo(stageInvitationDto.getId());
        assertThat(entity.getStage().getStageId()).isEqualTo(stageInvitationDto.getStageId());
        assertThat(entity.getAuthor().getId()).isEqualTo(stageInvitationDto.getAuthorId());
        assertThat(entity.getInvited().getId()).isEqualTo(stageInvitationDto.getInvitedId());
        assertThat(entity.getStatus()).isEqualTo(stageInvitationDto.getStatus());
        assertThat(entity.getDescription()).isEqualTo(stageInvitationDto.getExplanation());
    }

    @Test
    public void testToDtoMapper() {
        StageInvitationDto dto = invitationMapper.toDto(stageInvitation);

        assertThat(dto.getId()).isEqualTo(stageInvitation.getId());
        assertThat(dto.getStageId()).isEqualTo(stageInvitation.getStage().getStageId());
        assertThat(dto.getAuthorId()).isEqualTo(stageInvitation.getAuthor().getId());
        assertThat(dto.getInvitedId()).isEqualTo(stageInvitation.getInvited().getId());
        assertThat(dto.getStatus()).isEqualTo(stageInvitation.getStatus());
        assertThat(dto.getExplanation()).isEqualTo(stageInvitation.getDescription());
    }
}