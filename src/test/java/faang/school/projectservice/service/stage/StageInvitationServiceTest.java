package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDto;
import faang.school.projectservice.filter.stage.StageInvitationFilter;
import faang.school.projectservice.mapper.stage.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.stage.StageInvitationDtoValidator;
import faang.school.projectservice.validator.stage.StageInvitationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StageInvitationServiceTest {
    @InjectMocks
    private StageInvitationService stageInvitationService;

    @Mock
    private StageInvitationDtoValidator stageInvitationDtoValidator;

    @Mock
    private StageInvitationMapper stageInvitationMapper;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private StageInvitationValidator stageInvitationValidator;

    @Mock
    private List<StageInvitationFilter> stageInvitationFiltersMock;

    private Long id;
    private String rejectionReason;
    private StageInvitation stageInvitation;
    private StageInvitationDto stageInvitationDto;
    private TeamMember invited;
    private Stage stage;
    private List<Stage> stages;
    private List<StageInvitationFilter> stageInvitationFilters;
    private StageInvitationFilter stageInvitationAuthorIdFilter;
    private StageInvitationFilter stageInvitationStatusFilter;
    private StageInvitationFilterDto filter;
    private Stream<StageInvitation> stageInvitationStream;

    @BeforeEach
    public void setUp() {
        id = 1L;
        rejectionReason = "test reason";

        stageInvitation = new StageInvitation();
        stageInvitationDto = new StageInvitationDto();
        invited = new TeamMember();
        stage = new Stage();
        stageInvitationAuthorIdFilter = Mockito.mock(StageInvitationFilter.class);
        stageInvitationStatusFilter = Mockito.mock(StageInvitationFilter.class);
        filter = new StageInvitationFilterDto();

        stages = new ArrayList<>();
        stageInvitationFilters = new ArrayList<>();

        invited = TeamMember
                .builder()
                .id(id)
                .stages(stages)
                .build();

        stageInvitation = StageInvitation
                .builder()
                .status(StageInvitationStatus.PENDING)
                .stage(stage)
                .invited(invited)
                .build();

        stageInvitationFilters.add(stageInvitationAuthorIdFilter);
        stageInvitationFilters.add(stageInvitationStatusFilter);
        stageInvitationStream = Stream.<StageInvitation>builder().add(stageInvitation).build();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Testing that all mocks are called + return test")
    public void testCreate() {
        doNothing().when(stageInvitationDtoValidator).validateAll(stageInvitationDto);
        when(stageInvitationMapper.toEntity(stageInvitationDto, teamMemberRepository)).thenReturn(stageInvitation);
        when(stageInvitationRepository.save(stageInvitation)).thenReturn(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);

        StageInvitationDto result = stageInvitationService.create(stageInvitationDto);

        verify(stageInvitationDtoValidator).validateAll(stageInvitationDto);
        verify(stageInvitationMapper).toEntity(stageInvitationDto, teamMemberRepository);
        verify(stageInvitationRepository).save(stageInvitation);
        verify(stageInvitationMapper).toDto(stageInvitation);

        assertEquals(result, stageInvitationDto);
    }

    @Test
    @DisplayName("Testing that all mocks are called + return test")
    public void testAcceptStageInvitationCorrect() {
        when(stageInvitationRepository.findById(id)).thenReturn(stageInvitation);
        doNothing().when(stageInvitationValidator).statusPendingCheck(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(stageInvitation);

        StageInvitationDto result = stageInvitationService.acceptStageInvitation(id);

        verify(stageInvitationRepository).findById(id);
        verify(stageInvitationMapper).toDto(stageInvitation);

        assertEquals(result, stageInvitationDto);
    }

    @Test
    @DisplayName("Testing that all mocks are called + return test")
    public void testRejectStageInvitationCorrect() {
        when(stageInvitationRepository.findById(id)).thenReturn(stageInvitation);
        doNothing().when(stageInvitationValidator).statusPendingCheck(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(stageInvitation);

        StageInvitationDto result = stageInvitationService.rejectStageInvitation(id, rejectionReason);

        verify(stageInvitationRepository).findById(id);
        verify(stageInvitationMapper).toDto(stageInvitation);

        assertEquals(result, stageInvitationDto);
    }

    @Test
    @DisplayName("Testing that all mocks are called + return test")
    public void testGetMemberStageInvitations() {
        when(stageInvitationRepository.findAll()).thenReturn(List.of(stageInvitation));
        when(stageInvitationFiltersMock.stream()).thenReturn(stageInvitationFilters.stream());
        when(stageInvitationAuthorIdFilter.isApplicable(filter)).thenReturn(true);
        when(stageInvitationStatusFilter.isApplicable(filter)).thenReturn(true);
        when(stageInvitationAuthorIdFilter.apply(stageInvitationStream, filter)).thenReturn(stageInvitationStream);
        when(stageInvitationStatusFilter.apply(stageInvitationStream, filter)).thenReturn(stageInvitationStream);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);

        List<StageInvitationDto> result = stageInvitationService.getMemberStageInvitations(id, filter);

        verify(stageInvitationRepository).findAll();
        verify(stageInvitationFiltersMock).stream();
        verify(stageInvitationAuthorIdFilter).isApplicable(filter);
        verify(stageInvitationStatusFilter).isApplicable(filter);
        verify(stageInvitationAuthorIdFilter).apply(any(Stream.class), eq(filter));
        verify(stageInvitationStatusFilter).apply(any(Stream.class), eq(filter));
        verify(stageInvitationMapper).toDto(any(StageInvitation.class));

        assertEquals(List.of(stageInvitationDto), result);
    }
}
