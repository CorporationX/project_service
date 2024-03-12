package faang.school.projectservice.util.service;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.service.TeamMemberService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {

    @Mock
    private StageInvitationMapper stageInvitationMapper;
    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @Mock
    private TeamMemberService teamMemberService;
    @InjectMocks
    private StageInvitationService stageInvitationService;
    @Captor
    private ArgumentCaptor<List<StageInvitation>> captor;

    private StageInvitation invitation;
    private StageInvitationDto stageInvitationDto;

    @Test
    void testCreate() {
        stageInvitationDto = new StageInvitationDto();
        stageInvitationService.create(stageInvitationDto);
        Mockito.verify(stageInvitationRepository, times(1)).save(stageInvitationMapper.toEntity(stageInvitationDto));
    }

    @Test
    void testAcceptFail() {
        invitation = null;
        when(stageInvitationRepository.findById(5L)).thenReturn(invitation);
        Assert.assertThrows(NullPointerException.class, () -> stageInvitationService.accept(4L, 5L));
    }

    @Test
    void testAccept() {
        invitation = new StageInvitation();
        invitation.setId(1L);
        TeamMember teamMember = new TeamMember();
        teamMember.setId(2L);
        invitation.setInvited(teamMember);
        when(stageInvitationRepository.findById(2L)).thenReturn(invitation);
        when(teamMemberService.getTeamMember(anyLong())).thenReturn(any(TeamMember.class));
        stageInvitationService.accept(1L, 2L);
        Mockito.verify(stageInvitationRepository, times(1)).save(invitation);
    }

    @Test
    void testRejectFail() {
        invitation = null;
        when(stageInvitationRepository.findById(Mockito.anyLong())).thenReturn(invitation);
        Assert.assertThrows(NullPointerException.class, () -> stageInvitationService.reject(4L, 5L, "anytext"));
    }

    @Test
    void testGetAll() {
        StageInvitation firstStageInvitation = new StageInvitation();
        firstStageInvitation.setId(1L);

        StageInvitation secondStageInvitation = new StageInvitation();
        secondStageInvitation.setId(2L);

        TeamMember firstMember = new TeamMember();
        firstMember.setUserId(1L);

        TeamMember secondMember = new TeamMember();
        secondMember.setUserId(1L);

        firstStageInvitation.setInvited(firstMember);
        secondStageInvitation.setInvited(secondMember);

        List testList = List.of(firstStageInvitation, secondStageInvitation);

        when(stageInvitationRepository.findAll()).thenReturn(testList);

        List<StageInvitationDto> invitations = stageInvitationService.getAll(1L, null);
        Mockito.verify(stageInvitationMapper, times(1)).toDtoList(captor.capture());

        Assert.assertEquals(2, captor.getValue().size());
    }

}
