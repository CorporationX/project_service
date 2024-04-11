package faang.school.projectservice.stageinviation;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.mapper.StageInvitationMapperImpl;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class StageInvitationMapperTests {
    @InjectMocks
    private StageInvitationMapperImpl stageInvitationMapper;

    @Test
    void testStageInvitationMapperToDTO() {
        StageInvitation invitation = new StageInvitation();
        invitation.setId(1L);
        invitation.setDescription("123");
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        StageInvitationDto invitationDto = new StageInvitationDto();
        invitationDto.setId(1L);
        invitationDto.setDescription("123");
        invitationDto.setStatus(StageInvitationStatus.ACCEPTED);
        StageInvitationDto actualInvitationDto = stageInvitationMapper.toDto(invitation);

        assertEquals(invitationDto, actualInvitationDto);
    }


    @Test
    void testStageInvitationMapperToEntity() {
        StageInvitation invitation = new StageInvitation();
        invitation.setId(1L);
        invitation.setDescription("123");
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        StageInvitationDto invitationDto = new StageInvitationDto();
        invitationDto.setId(1L);
        invitationDto.setDescription("123");
        invitationDto.setStatus(StageInvitationStatus.ACCEPTED);
        StageInvitation actualInvitation = stageInvitationMapper.toEntity(invitationDto);

        assertEquals(invitation, actualInvitation);
    }
}
