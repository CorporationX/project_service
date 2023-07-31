package faang.school.projectservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.DataValidateInviteException;
import faang.school.projectservice.filter.*;
import faang.school.projectservice.mapper.StageInvitationMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class StageInvitationServiceTest {
    private StageInvitationService stageInvitationService;
    @Spy
    private StageInvitationMapperImpl stageInvitationMapper;
    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    private StageInvitationFilterDto stageInvitationFilterDto;

    private List<StageInvitationFilter> filters;
    private StageInvitation stageInvitation;

    @Mock
    private StageInvitationDto stageInvitationDto;

    @Mock
    private Stage stage;
    @Mock
    private TeamMember invited;

    private TeamMember author;

    @BeforeEach
    void setUp() {
        stageInvitationFilterDto = StageInvitationFilterDto.builder()
                .statusPattern(StageInvitationStatus.PENDING)
                .stagePattern("Stage")
                .authorPattern(1L)
                .invitedPattern(2L)
                .build();

        author = TeamMember.builder()
                .userId(1L)
                .roles(List.of(TeamRole.OWNER, TeamRole.MANAGER, TeamRole.DEVELOPER))
                .build();

        List<TeamMember> executors = new ArrayList<>();
        executors.add(author);

//        stage = Stage.builder()
//                .stageName("Stage")
//                .executors(executors)
//                .build();
//
//        invited = TeamMember.builder()
//                .userId(2L)
//                .build();

//        stageInvitationDto = StageInvitationDto.builder()
//                .id(5L)
//                .stage(stage)
//                .author(author)
//                .invited(invited)
//                .build();

        stageInvitation = StageInvitation.builder()
                .id(6L)
                .status(StageInvitationStatus.PENDING)
                .stage(stage)
                .author(TeamMember.builder().id(1L).build())
                .invited(TeamMember.builder().id(2L).build())
                .build();

//        stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
//        stageInvitation.setAuthor(author);

        List<StageInvitationFilter> filters = List.of(
                new StageInvitationAuthorFilter(),
                new StageInvitationInvitedFilter(),
                new StageInvitationStageFilter(),
                new StageInvitationStatusFilter()
        );
        stageInvitationService = new StageInvitationService(stageInvitationRepository, stageInvitationMapper, filters, teamMemberRepository);

        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(stageInvitation);
    }

    @Test
    void testSendInvitation_UserAlreadyExecutor() {
        List<TeamMember> executors = new ArrayList<>();
        executors.add(invited);
        stage.setExecutors(executors);

        assertThrows(DataValidateInviteException.class, () -> stageInvitationService.sendInvitation(stageInvitationDto));
    }

    @Test
    void testSendInvitation_UserHasOwnerRole_ExceptionThrown() {
        Mockito.when(stageInvitationDto.getInvited()).thenReturn(invited);
        Mockito.when(invited.getRoles()).thenReturn(List.of(TeamRole.OWNER));

        assertThrows(DataValidateInviteException.class, () -> {
            stageInvitationService.sendInvitation(stageInvitationDto);
        });
    }

    @Test
    void testAcceptInvitation_UserIsNotExecutor_Success() {
        when(stageInvitationRepository.findById(anyLong())).thenReturn(stageInvitation);

        List<TeamMember> executors = new ArrayList<>();
        when(stage.getExecutors()).thenReturn(executors);

        StageInvitationDto result = stageInvitationService.acceptInvitation(stageInvitationDto);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.ACCEPTED, result.getStatus());
        verify(stageInvitationRepository, times(1)).save(Mockito.any());
    }

    @Test
    void testAcceptInvitation_UserIsExecutor_ExceptionThrown() {
        when(stageInvitationRepository.findById(anyLong())).thenReturn(stageInvitation);

        List<TeamMember> executors = new ArrayList<>();
        executors.add(invited);
        when(stage.getExecutors()).thenReturn(executors);

        assertThrows(DataValidateInviteException.class, () -> {
            stageInvitationService.acceptInvitation(stageInvitationDto);
        });
    }

    @Test
    void testRejectInvitation_Success() {
        when(stageInvitationRepository.findById(anyLong())).thenReturn(stageInvitation);

        StageInvitationDto result = stageInvitationService.rejectInvitation(stageInvitationDto);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.REJECTED, result.getStatus());
        verify(stageInvitationRepository, times(1)).save(Mockito.any());
    }
}