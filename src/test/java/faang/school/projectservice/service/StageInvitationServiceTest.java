package faang.school.projectservice.service;

import faang.school.projectservice.dto.invitation.DtoStage;
import faang.school.projectservice.dto.invitation.DtoStageInvitation;
import faang.school.projectservice.dto.invitation.DtoStageInvitationFilter;
import faang.school.projectservice.dto.invitation.DtoStatus;
import faang.school.projectservice.filter.stageInvitation.FilterAuthor;
import faang.school.projectservice.filter.stageInvitation.FilterStatus;
import faang.school.projectservice.mapper.invitationMaper.StageInvitationMapper;
import faang.school.projectservice.mapper.invitationMaper.StageMapper;
import faang.school.projectservice.mapper.invitationMaper.TeamMemberMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {
    private final StageInvitationMapper stageInvitationMapper = StageInvitationMapper.INSTANCE;
    private final TeamMemberMapper memberMapper = TeamMemberMapper.INSTANCE;
    private final StageMapper stageMapper = StageMapper.INSTANCE;
    @Mock
    private StageInvitationRepository invitationRepository;
    @InjectMocks
    private StageInvitationService invitationService;
    DtoStage stage1;
    DtoStage stage2;
    DtoStageInvitation invitation1;
    DtoStageInvitation invitation2;
    DtoStageInvitation invitation3;
    DtoStageInvitation invitation4;

    @BeforeEach
    void setUp() {
        stage1 = new DtoStage(1L);
        stage2 = new DtoStage(2L);
        invitation1 = new DtoStageInvitation("hello", 1L, 1L, stage1);
        invitation2 = new DtoStageInvitation("world", 1L, 1L, stage2);
        invitation3 = new DtoStageInvitation("russia", 2L, 1L, stage1);
        invitation4 = new DtoStageInvitation("super", 1L, 1L, stage2);
    }

    @Test
    void invitationHasBeenSent() {
        when(invitationRepository.existsByAuthorAndInvitedAndStage(
                memberMapper.toTeamMember(2L), memberMapper.toTeamMember(1L), stageMapper.toStage(stage1)
        )).thenReturn(true);
        when(invitationRepository.save(stageInvitationMapper.toStageInvitation(invitation3))).thenReturn(stageInvitationMapper.toStageInvitation(invitation3));
        DtoStageInvitation expected = invitationService.invitationHasBeenSent(invitation3);
        assertEquals(expected, invitation3);
    }

    @Test
    void acceptDeclineInvitation() {
        when(invitationRepository.findById(1L)).thenReturn(stageInvitationMapper.toStageInvitation(invitation1));
        invitation1.setStatus(StageInvitationStatus.ACCEPTED);
        DtoStatus expected = invitationService.acceptDeclineInvitation("ACCEPTED", 1L);
        assertEquals(expected.getStatus(), invitation1.getStatus());
        invitation1.setStatus(StageInvitationStatus.REJECTED);
        DtoStatus expected2 = invitationService.acceptDeclineInvitation("REJECTED", 1L);
        assertEquals(expected2.getStatus(), invitation1.getStatus());
    }

//    @Test
//    void getAllStageInvitation() {
//        invitation2.setStatus(StageInvitationStatus.ACCEPTED);
//        invitation4.setStatus(StageInvitationStatus.ACCEPTED);
//        List<StageInvitation> stageInvitations = List.of(stageInvitationMapper.toStageInvitation(invitation1), stageInvitationMapper.toStageInvitation(invitation2)
//                , stageInvitationMapper.toStageInvitation(invitation3), stageInvitationMapper.toStageInvitation(invitation4));
//        List<DtoStageInvitation> actual = List.of(invitation1, invitation3);
//        invitationService = new StageInvitationService(invitationRepository, List.of(new FilterStatus(), new FilterAuthor()));
//        when(invitationRepository.findAll()).thenReturn(stageInvitations);
//        List<DtoStageInvitation> expected = invitationService.getAllStageInvitation(1L, new DtoStageInvitationFilter(StageInvitationStatus.PENDING, null));
//        assertEquals(expected, actual);
//    }
}
