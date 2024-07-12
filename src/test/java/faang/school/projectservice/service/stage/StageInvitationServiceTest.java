package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.dto.client.StageInvitationFilterDto;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    List<StageInvitationFilter> stageInvitationFiltersMock;

    private Long id;
    private String rejectionReason;
    private StageInvitation stageInvitation;
    private StageInvitationDto stageInvitationDto;
    private StageInvitationDto stageInvitationDtoAfterMapping;
    private TeamMember invited;
    private Stage stage;
    private List<Stage> stages;
    private List<StageInvitationFilter> stageInvitationFilters;
    private StageInvitationFilter stageInvitationAuthorIdFilter;
    private StageInvitationFilter stageInvitationStatusFilter;
    private StageInvitationFilterDto filter;

    @BeforeEach
    public void setUp() {
        id = 1L;
        rejectionReason = "test reason";

        stageInvitation = new StageInvitation();
        stageInvitationDto = new StageInvitationDto();
        stageInvitationDtoAfterMapping = new StageInvitationDto();
        invited = new TeamMember();
        stage = new Stage();
        stageInvitationAuthorIdFilter = Mockito.mock(StageInvitationFilter.class);
        stageInvitationStatusFilter = Mockito.mock(StageInvitationFilter.class);
        filter = new StageInvitationFilterDto();

        stages = new ArrayList<>();
        stageInvitationFilters = new ArrayList<>();

        stageInvitationFilters.add(stageInvitationAuthorIdFilter);
        stageInvitationFilters.add(stageInvitationStatusFilter);

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

        stageInvitationService.rejectStageInvitation(id, rejectionReason);

        verify(stageInvitationRepository).findById(id);
        verify(stageInvitationMapper).toDto(stageInvitation);
    }

    @Test
    public void testGetMemberStageInvitations() {
        invited.setId(id);
        stageInvitation.setInvited(invited);
        when(stageInvitationRepository.findAll()).thenReturn(List.of(stageInvitation));
        when(stageInvitationFiltersMock.stream()).thenReturn(stageInvitationFilters.stream());
        when(stageInvitationAuthorIdFilter.isApplicable(filter)).thenReturn(true);
        when(stageInvitationStatusFilter.isApplicable(filter)).thenReturn(true);
        when(stageInvitationAuthorIdFilter.apply(any(), any())).thenReturn(Stream.<StageInvitation>builder().add(stageInvitation).build());
        when(stageInvitationStatusFilter.apply(any(), any())).thenReturn(Stream.<StageInvitation>builder().add(stageInvitation).build());
        when(stageInvitationMapper.toDto(any())).thenReturn(stageInvitationDto);

        List<StageInvitationDto> result = stageInvitationService.getMemberStageInvitations(id, filter);

        verify(stageInvitationRepository).findAll();
        verify(stageInvitationFiltersMock).stream();
        verify(stageInvitationAuthorIdFilter).isApplicable(filter);
        verify(stageInvitationStatusFilter).isApplicable(filter);
        verify(stageInvitationAuthorIdFilter).apply(any(), any());
        verify(stageInvitationStatusFilter).apply(any(), any());
        verify(stageInvitationMapper, Mockito.times(2)).toDto(any());
        assertEquals(List.of(stageInvitationDto, stageInvitationDto), result);
    }
}
