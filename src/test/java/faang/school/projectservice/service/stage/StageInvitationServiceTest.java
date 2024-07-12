package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.mapper.stage.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StageInvitationServiceTest {
    @InjectMocks
    StageInvitationService stageInvitationService;

    @Mock
    StageInvitationDtoValidator stageInvitationDtoValidator;

    @Mock
    StageInvitationMapper stageInvitationMapper;

    @Mock
    StageInvitationRepository stageInvitationRepository;

    @Mock
    TeamMemberRepository teamMemberRepository;

    @Mock
    StageInvitationValidator stageInvitationValidator;

    private Long id;
    private StageInvitation stageInvitation;
    private StageInvitationDto stageInvitationDto;
    private StageInvitationDto stageInvitationDtoAfterMapping;
    private TeamMember invited;
    private Stage stage;
    private List<Stage> stages;

    @BeforeEach
    public void setUp() {
        id = 1L;

        stageInvitation = new StageInvitation();
        stageInvitationDto = new StageInvitationDto();
        stageInvitationDtoAfterMapping = new StageInvitationDto();
        invited = new TeamMember();
        stage = new Stage();

        stages = new ArrayList<>();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreate() {
        doNothing().when(stageInvitationDtoValidator).validateAll(stageInvitationDto);
        when(stageInvitationMapper.toEntity(stageInvitationDto, teamMemberRepository)).thenReturn(stageInvitation);
        when(stageInvitationRepository.save(stageInvitation)).thenReturn(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDtoAfterMapping);

        stageInvitationService.create(stageInvitationDto);

        verify(stageInvitationDtoValidator).validateAll(stageInvitationDto);
        verify(stageInvitationMapper).toEntity(stageInvitationDto, teamMemberRepository);
        verify(stageInvitationRepository).save(stageInvitation);
        verify(stageInvitationMapper).toDto(stageInvitation);

        assertEquals(stageInvitationDto, stageInvitationDtoAfterMapping);
    }

    @Test
    public void testAcceptStageInvitationCorrect() {
        invited.setStages(stages);
        stageInvitation.setInvited(invited);
        stageInvitation.setStage(stage);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        when(stageInvitationRepository.findById(id)).thenReturn(stageInvitation);
        doNothing().when(stageInvitationValidator).statusPendingCheck(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);

        stageInvitationService.acceptStageInvitation(id);

        verify(stageInvitationRepository).findById(id);
        verify(stageInvitationMapper).toDto(stageInvitation);
    }

    @Test
    public void testRejectStageInvitationCorrect() {
        invited.setStages(stages);
        stageInvitation.setInvited(invited);
        stageInvitation.setStage(stage);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        when(stageInvitationRepository.findById(id)).thenReturn(stageInvitation);
        doNothing().when(stageInvitationValidator).statusPendingCheck(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);

        stageInvitationService.rejectStageInvitation(id);

        verify(stageInvitationRepository).findById(id);
        verify(stageInvitationMapper).toDto(stageInvitation);
    }
}
