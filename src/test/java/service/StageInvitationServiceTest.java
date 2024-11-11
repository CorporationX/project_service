package service;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import faang.school.projectservice.service.stage_invitation.filter.StageInvitationFilter;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Data
@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {

    @InjectMocks
    private StageInvitationService service;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private StageInvitationMapper stageInvitationMapper;

    @Mock
    private StageInvitationValidator stageInvitationValidate;

    @Mock
    private List<StageInvitationFilter> invitationFilters;

    private StageInvitationDto invitationDto;
    private StageInvitation invitation;

    @BeforeEach
    public void setUp() {
        invitationDto = new StageInvitationDto();
        invitation = new StageInvitation();
    }

    @Test
    public void testCreateInvitation() {
        when(stageInvitationMapper.toEntity(invitationDto)).thenReturn(invitation);
        when(stageInvitationMapper.toDto(invitation)).thenReturn(invitationDto);

        service.createInvitation(invitationDto);

        verify(stageInvitationValidate).validateInvitation(invitationDto);
        verify(stageInvitationRepository).save(invitation);
        verify(stageInvitationMapper).toDto(invitation);
    }

    @Test
    public void testAcceptInvitation() {
        when(stageInvitationMapper.toEntity(invitationDto)).thenReturn(invitation);
        when(stageInvitationMapper.toDto(invitation)).thenReturn(invitationDto);
        invitation.setStage(new Stage());
        List<TeamMember> executors = new ArrayList<>();
        executors.add(new TeamMember());
        executors.add(new TeamMember());
        invitation.getStage().setExecutors(executors);

        StageInvitationDto result = service.acceptInvitation(invitationDto);

        assertEquals(invitationDto, result);
        assertEquals(StageInvitationStatus.ACCEPTED, invitation.getStatus());
    }

    @Test
    public void testRejectInvitation() {
        when(stageInvitationMapper.toEntity(invitationDto)).thenReturn(invitation);
        when(stageInvitationMapper.toDto(invitation)).thenReturn(invitationDto);

        service.rejectInvitation(invitationDto);

        verify(stageInvitationValidate).validateDescription(invitationDto);
        assertEquals(StageInvitationStatus.REJECTED, invitation.getStatus());
    }

    @Test
    public void testViewAllInvitation() {
        when(stageInvitationMapper.toEntity(invitationDto)).thenReturn(invitation);
        invitation.setInvited(new TeamMember());
        invitation.getInvited().setUserId(1L);
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto();

        List<StageInvitationDto> result = service.viewAllInvitation(invitationDto, filterDto);

        assertNotNull(result);
        verify(stageInvitationRepository).findAll();
    }
}
