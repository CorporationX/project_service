package faang.school.projectservice.util.service;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.exception.ValidateStageInvitationException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.StageInvitationService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {

    @Mock
    private StageInvitationMapper stageInvitationMapper;
    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @InjectMocks
    private StageInvitationService stageInvitationService;

    private StageInvitation invitation;
    private StageInvitationDto stageInvitationDto;

    @Test
    void testCreate() {
        stageInvitationDto = new StageInvitationDto();
        stageInvitationService.create(stageInvitationDto);
        Mockito.verify(stageInvitationRepository, Mockito.times(1)).save(stageInvitationMapper.toEntity(stageInvitationDto));
    }

    @Test
    void testAcceptFail() {
        invitation = null;
        when(stageInvitationRepository.findById(5L)).thenReturn(invitation);
        Assert.assertThrows(ValidateStageInvitationException.class, () -> stageInvitationService.accept(4L, 5L));
    }

    @Test
    void testAccept(){
        invitation = new StageInvitation();
        invitation.setId(1L);
        TeamMember teamMember = new TeamMember();
        teamMember.setId(2L);
        invitation.setInvited(teamMember);
        when(stageInvitationRepository.findById(2L)).thenReturn(invitation);
        stageInvitationService.accept(1L, 2L);
        Mockito.verify(stageInvitationRepository, Mockito.times(1)).save(invitation);
    }

    @Test
    void testRejectFail() {
        invitation = null;
        when(stageInvitationRepository.findById(Mockito.anyLong())).thenReturn(invitation);
        Assert.assertThrows(ValidateStageInvitationException.class, () -> stageInvitationService.reject(4L, 5L, "anytext"));
    }

    @Test
    void testGetAllFail() {

    }

    @Test
    void testGetAll() {

    }

}
