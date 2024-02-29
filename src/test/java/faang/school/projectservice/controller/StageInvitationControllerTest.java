package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationControllerTest {
    @Mock
    private StageInvitationService stageInvitationService;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private StageInvitationController stageInvitationController;

    @Test
    public void testSuccessCreate() {
        StageInvitationDto validInvitationDto = StageInvitationDto.builder()
                .stageId(1L)
                .invitedId(2L)
                .stageId(1L)
                .build();
        stageInvitationController.create(validInvitationDto);
        verify(stageInvitationService, times(1)).create(validInvitationDto);
    }

    @Test
    public void testAccept() {
        long stageInvitationId = 1L;
        stageInvitationController.accept(stageInvitationId);
        verify(stageInvitationService, times(1)).accept(stageInvitationId);
    }

    @Test
    public void testReject() {
        long stageInvitationId = 1L;
        String message = "Reject!";
        stageInvitationController.reject(stageInvitationId, message);
        verify(stageInvitationService, times(1)).reject(stageInvitationId, message);
    }

    @Test
    public void testFilter() {
        StageInvitationFilterDto filterDto = StageInvitationFilterDto.builder().build();
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(1L);
        stageInvitationController.findAllInviteByFilter(filterDto);
        verify(stageInvitationService, times(1)).findAllInviteByFilter(filterDto, userId);
    }
}


