package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.StageInvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {
  @Mock
  private StageInvitationRepository stageInvitationRepository;

  @Mock
  private StageInvitationMapper stageInvitationMapper;

  @InjectMocks
  private StageInvitationService stageInvitationService;

  StageInvitationDto stageInvitationDto;

  private List<StageInvitation> getMockedStageEvents() {
    StageInvitation jsEvent = new StageInvitation();
    jsEvent.setId(1L);

    StageInvitation javaEvent = new StageInvitation();
    jsEvent.setId(2L);

    StageInvitation pythonEvent = new StageInvitation();
    jsEvent.setId(3L);

    StageInvitation rubyEvent = new StageInvitation();
    jsEvent.setId(4L);

    TeamMember jsMember = new TeamMember();
    jsMember.setUserId(1L);

    TeamMember javaMember = new TeamMember();
    javaMember.setUserId(1L);

    TeamMember pythonMember = new TeamMember();
    pythonMember.setUserId(2L);

    TeamMember rubyMember = new TeamMember();
    rubyMember.setUserId(1L);

    jsEvent.setInvited(jsMember);
    javaEvent.setInvited(javaMember);
    pythonEvent.setInvited(pythonMember);
    rubyEvent.setInvited(rubyMember);

    return List.of(jsEvent, javaEvent, pythonEvent, rubyEvent);
  }

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

  @Test
  public void getAllByInvitedUserId() {
    Mockito.when(stageInvitationRepository.findAll()).thenReturn(getMockedStageEvents());

    List<StageInvitationDto> invitations = stageInvitationService.getAllByInvitedUserId(1L);

    assertEquals(3, invitations.size());
  }
}
