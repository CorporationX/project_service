package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.filter.stage.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.validator.StageInvitationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private List<StageInvitationFilter> stageInvitationFilters;

    @Mock
    private StageInvitationValidator stageInvitationValidator;

    @Spy
    private StageInvitationMapper stageInvitationMapper;

    @InjectMocks
    private StageInvitationService stageInvitationService;

    private Long id;
    private String rejectReason;
    private Stage stage;
    private List<Stage> stages;
    private TeamMember invited;
    private StageInvitationDto stageInvitationDto;
    private StageInvitation stageInvitation;
    private StageInvitationFilter filter;
    private StageInvitationFilterDto filterDto;

    @BeforeEach
    public void setUp() {
        id = 1L;
        stage = Stage.builder()
                .stageId(1L)
                .stageName("NameStage")
                .build();

        stages = new ArrayList<>();
        stages.add(stage);

        invited = TeamMember.builder()
                .id(2L)
                .stages(stages)
                .build();

        stageInvitationDto = StageInvitationDto.builder()
                .id(id)
                .authorId(1L)
                .invitedId(2L)
                .stageId(11L)
                .description("description")
                .build();

        stageInvitation = StageInvitation.builder()
                .id(id)
                .invited(invited)
                .stage(stage)
                .status(StageInvitationStatus.PENDING)
                .build();

        rejectReason = "Test reject reason";

        filter = mock(StageInvitationFilter.class);
        filterDto = mock(StageInvitationFilterDto.class);
    }

    @Test
    public void testCreateStageInvitationSuccess() {
        doNothing().when(stageInvitationValidator).validateEqualsId(anyLong(), anyLong());
        doNothing().when(stageInvitationValidator).validateInvitedMemberTeam(anyLong(), anyLong());

        when(stageInvitationMapper
                .toEntity(stageInvitationDto, teamMemberRepository, stageRepository))
                .thenReturn(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);
        when(stageInvitationRepository.save(stageInvitation)).thenReturn(stageInvitation);

        StageInvitationDto result = stageInvitationService.createStageInvitation(stageInvitationDto);

        verify(stageInvitationRepository).save(stageInvitation);
        verify(stageInvitationValidator).validateEqualsId(1L, 2L);
        verify(stageInvitationValidator).validateInvitedMemberTeam(1L, 2L);

        assertNotNull(result);
        assertEquals(stageInvitationDto.getAuthorId(), result.getAuthorId());
        assertEquals(stageInvitationDto.getInvitedId(), result.getInvitedId());
    }

    @Test
    public void testCreateStageInvitationSaveFails() {
        doNothing().when(stageInvitationValidator).validateEqualsId(anyLong(), anyLong());
        doNothing().when(stageInvitationValidator).validateInvitedMemberTeam(anyLong(), anyLong());
        when(stageInvitationMapper
                .toEntity(stageInvitationDto, teamMemberRepository, stageRepository))
                .thenReturn(stageInvitation);
        when(stageInvitationRepository.save(stageInvitation)).thenThrow(BusinessException.class);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            stageInvitationService.createStageInvitation(stageInvitationDto);
        });

        assertNull(exception.getMessage());
    }

    @Test
    public void testAcceptStageInvitationSuccess() {
        when(stageInvitationRepository.getReferenceById(id)).thenReturn(stageInvitation);
        doNothing().when(stageInvitationValidator).validateStatusPendingCheck(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(stageInvitation);

        StageInvitationDto result = stageInvitationService.acceptStageInvitation(id);

        verify(stageInvitationRepository).getReferenceById(id);
        verify(stageInvitationMapper).toDto(stageInvitation);

        assertNotNull(result);
        assertEquals(result, stageInvitationDto);
    }

    @Test
    public void testAcceptStageInvitationFails() {
        when(stageInvitationRepository.getReferenceById(id)).thenReturn(stageInvitation);
        doNothing().when(stageInvitationValidator).validateStatusPendingCheck(stageInvitation);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenThrow(BusinessException.class);

        assertThrows(BusinessException.class, () -> {
            stageInvitationService.acceptStageInvitation(id);
        });
    }

    @Test
    public void testRejectStageInvitationSuccess() {
        when(stageInvitationRepository.getReferenceById(id)).thenReturn(stageInvitation);
        doNothing().when(stageInvitationValidator).validateStatusPendingCheck(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(stageInvitation);

        StageInvitationDto result = stageInvitationService.rejectStageInvitation(id, rejectReason);

        verify(stageInvitationRepository).getReferenceById(id);
        verify(stageInvitationMapper).toDto(stageInvitation);

        assertNotNull(result);
        assertEquals(result, stageInvitationDto);
    }

    @Test
    public void testGetAllInvitationsForOneParticipantWithApplyFilter() {
        when(stageInvitationRepository.findAll()).thenReturn(List.of(stageInvitation));
        when(stageInvitationFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isApplicable(filterDto)).thenReturn(true);
        when(filter.apply(any(), eq(filterDto))).thenReturn(Stream.of(stageInvitation));
        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(stageInvitationDto);

        List<StageInvitationDto> result = stageInvitationService.getAllInvitationsForOneParticipant(1L, filterDto);

        verify(filter, times(1)).apply(any(), eq(filterDto));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetAllInvitationsForOneParticipantWithNoApplyFilter() {
        when(stageInvitationFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isApplicable(filterDto)).thenReturn(false);
        when(stageInvitationRepository.findAll()).thenReturn(List.of(stageInvitation));

        List<StageInvitationDto> result = stageInvitationService.getAllInvitationsForOneParticipant(1L, filterDto);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetAllInvitationsForOneParticipantWithMatchInvitation() {
        when(stageInvitationFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isApplicable(filterDto)).thenReturn(true);
        when(filter.apply(any(), eq(filterDto))).thenReturn(Stream.of(stageInvitation));
        when(stageInvitationRepository.findAll()).thenReturn(List.of(stageInvitation));
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);

        List<StageInvitationDto> result = stageInvitationService.getAllInvitationsForOneParticipant(1L, filterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(stageInvitationDto, result.get(0));
    }

    @Test
    void testGetAllInvitationsForOneParticipantWithNoMatchInvitation() {
        when(stageInvitationRepository.findAll()).thenReturn(List.of(stageInvitation));

        List<StageInvitationDto> result = stageInvitationService.getAllInvitationsForOneParticipant(1L, filterDto);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
