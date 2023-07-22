package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.StageInvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {
  @Mock
  private StageInvitationRepository stageInvitationRepository;

  @Mock
  private StageInvitationMapper stageInvitationMapper;

  @InjectMocks
  private StageInvitationService stageInvitationService;

  StageInvitationDto stageInvitationDto;

  @BeforeEach
  public void init() {
    stageInvitationDto = new StageInvitationDto();
    stageInvitationDto.setInvitedPersonId(1L);
    stageInvitationDto.setStageId(1L);
    stageInvitationDto.setAuthorId(1L);
  }

  @Test
  public void createStageInvitationTest() {
    stageInvitationService.create(stageInvitationDto);
    Mockito.verify(stageInvitationRepository, Mockito.times(1)).save(stageInvitationMapper.toEntity(stageInvitationDto));
  }
}
