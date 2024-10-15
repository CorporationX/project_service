package faang.school.projectservice;

import faang.school.projectservice.model.dto.StageInvitationDto;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.Stage;
import faang.school.projectservice.model.entity.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.impl.StageInvitationServiceImpl;
import faang.school.projectservice.mapper.StageInvitationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class SendInvitationTest {

    @Mock
    private StageInvitationRepository invitationRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private StageInvitationMapper mapper;

    @InjectMocks
    private StageInvitationServiceImpl invitationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendInvitation_shouldInvokeRequiredMethods() {
        Long authorId = 1L;
        Long inviteeId = 2L;
        Long stageId = 3L;

        StageInvitationDto invitationDto = new StageInvitationDto();
        invitationDto.setAuthorId(authorId);
        invitationDto.setInviteeId(inviteeId);
        invitationDto.setStageId(stageId);

        when(teamMemberRepository.findById(authorId)).thenReturn(mock(TeamMember.class));
        when(teamMemberRepository.findById(inviteeId)).thenReturn(mock(TeamMember.class));
        when(stageRepository.getById(stageId)).thenReturn(mock(Stage.class));

        when(invitationRepository.save(any(StageInvitation.class))).thenReturn(mock(StageInvitation.class));

        when(mapper.toDto(any(StageInvitation.class))).thenReturn(mock(StageInvitationDto.class));

        invitationService.sendInvitation(invitationDto);

        verify(teamMemberRepository).findById(authorId);
        verify(teamMemberRepository).findById(inviteeId);
        verify(stageRepository).getById(stageId);
        verify(invitationRepository).save(any(StageInvitation.class));
        verify(mapper).toDto(any(StageInvitation.class));
    }
}