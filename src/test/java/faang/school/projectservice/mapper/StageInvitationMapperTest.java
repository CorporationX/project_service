package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StageInvitationMapperTest {
    private final StageInvitationMapper stageInvitationMapper = Mappers.getMapper(StageInvitationMapper.class);

    @Test
    @DisplayName("ConvertStageInvitationToDto")
    public void convertStageInvitationFromDto() {
        TeamMember author = new TeamMember();
        TeamMember invited = new TeamMember();
        Stage stage = new Stage();
        StageInvitation stageInvitation = new StageInvitation(1L, "desc", StageInvitationStatus.ACCEPTED, stage, author, invited);
        StageInvitationDto stageInvitationDto = stageInvitationMapper.toDto(stageInvitation);

        assertThat(stageInvitationDto).isNotNull();
        assertThat(Mockito.any(StageInvitationDto.class)).isEqualTo(stageInvitationDto);
    }
}