package faang.school.projectservice.stage_invitation;

import faang.school.projectservice.controller.stage_invitation.StageInvitationController;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StageInvitationControllerTest {
    @Mock
    private StageInvitationService service;
    @InjectMocks
    private StageInvitationController controller;
    private StageInvitationDto validInvitationDto;
    private StageInvitationDto invalidInvitationDto;

    @Test
    public void testSuccessCreate() {
        validInvitationDto = StageInvitationDto.builder()
                .stageId(1L)
                .invitedId(2L)
                .authorId(1L)
                .build();
        controller.create(validInvitationDto);
        Mockito.verify(service, Mockito.times(1)).create(validInvitationDto);
    }

    @Test
    public void testCreateInvitationIsNull(){
        Assertions.assertThrows(DataValidationException.class, () -> controller.create(invalidInvitationDto));
    }
    @Test
    public void testCreateStageIdIsNull(){
        invalidInvitationDto = StageInvitationDto.builder()
                .invitedId(2L)
                .authorId(1L)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> controller.create(invalidInvitationDto));
    }
    @Test
    public void testCreateAuthorIdIsNull(){
        invalidInvitationDto = StageInvitationDto.builder()
                .stageId(1L)
                .invitedId(2L)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> controller.create(invalidInvitationDto));
    }
    @Test
    public void testCreateInvitedIdIsNull(){
        invalidInvitationDto = StageInvitationDto.builder()
                .stageId(1L)
                .authorId(1L)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> controller.create(invalidInvitationDto));
    }

    @Test
    public void testCreateInvitedIsAuthor(){
        invalidInvitationDto = StageInvitationDto.builder()
                .stageId(1L)
                .invitedId(1L)
                .authorId(1L)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> controller.create(invalidInvitationDto));
    }
}
