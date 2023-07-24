package faang.school.projectservice.controller.stage;

import faang.school.projectservice.controller.StageInvitationController;
import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.StageInvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class StageInvitationControllerTest {
  @Mock
  private StageInvitationService stageInvitationService;

  @InjectMocks
  private StageInvitationController stageInvitationController;

  StageInvitationDto stageInvitationDto;

  @BeforeEach
  public void init() {
    stageInvitationDto = new StageInvitationDto();
    stageInvitationDto.setInvitedPersonId(1L);
    stageInvitationDto.setStageId(1L);
    stageInvitationDto.setAuthorId(1L);
  }

  @Test
  public void createSuccessTest() {
    stageInvitationController.create(stageInvitationDto);
    Mockito.verify(stageInvitationService, Mockito.times(1)).create(stageInvitationDto);
  }

  @Test
  public void createStageIdValidationFailed() {
    stageInvitationDto.setStageId(null);
    assertThrows(DataValidationException.class, () -> {
      stageInvitationController.create(stageInvitationDto);
    });
  }

  @Test
  public void createAuthorIdValidationFailed() {
    stageInvitationDto.setAuthorId(null);
    assertThrows(DataValidationException.class, () -> {
      stageInvitationController.create(stageInvitationDto);
    });
  }
  @Test
  public void createInvitedPersonValidationFailed() {
    stageInvitationDto.setInvitedPersonId(null);
    assertThrows(DataValidationException.class, () -> {
      stageInvitationController.create(stageInvitationDto);
    });
  }

  @Test
  public void getAllByInvitedUserIdSuccess() {
    stageInvitationController.getAllByInvitedUserId(1L);
    Mockito.verify(stageInvitationService, Mockito.times(1)).getAllByInvitedUserId(1L);
  }
  @Test
  public void getAllByInvitedUserIdUserValidationFailed() {
    assertThrows(DataValidationException.class, () -> {
      stageInvitationController.getAllByInvitedUserId(null);
    });
  }
}
