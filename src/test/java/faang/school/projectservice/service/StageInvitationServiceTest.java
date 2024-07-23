package faang.school.projectservice.service;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.StageInvitationFilter;
import faang.school.projectservice.filter.StageInvitationStatusFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {

    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private StageInvitationMapper stageInvitationMapper;
    @InjectMocks
    private StageInvitationService stageInvitationService;

    private StageInvitationDto stageInvitationDto;
    private StageInvitation stageInvitation;
    private StageInvitation stageInvitationSecond;
    private TeamMember teamMember;
    private List<StageInvitation> stageInvitationList;
    @Mock
    private StageInvitationFilter stageInvitationStatusFilter;
    @Mock
    private StageInvitationFilter stageInvitationTeamMemberFilter;
    @Mock
    private StageInvitationFilterDto stageInvitationFilterDto;
    @Mock
    private List<StageInvitationFilter> stageInvitationsFilter;
    private List<StageInvitation> stageInvintationFiltered;

    @BeforeEach
    public void init() {
        TeamMember author = new TeamMember();
        author.setId(1L);
        author.setUserId(1L);
        TeamMember invited = new TeamMember();
        invited.setId(1L);
        invited.setUserId(1L);
        Stage stage = new Stage();

        stageInvitation = new StageInvitation(1L, "desc", StageInvitationStatus.PENDING, stage, author, invited);
        stageInvitationSecond = new StageInvitation(2L, "desc2", StageInvitationStatus.REJECTED, stage, author, invited);

        stageInvitationDto = new StageInvitationDto(1L, "desc", StageInvitationStatus.PENDING, 1L, 1L, 1L);

        teamMember = new TeamMember();
        teamMember.setId(1L);

        stageInvitationList = List.of(stageInvitation, stageInvitationSecond);
        stageInvintationFiltered = List.of(stageInvitation);
        stageInvitationStatusFilter = new StageInvitationStatusFilter();
        stageInvitationFilterDto = new StageInvitationFilterDto();
        stageInvitationFilterDto.setStatus(StageInvitationStatus.PENDING);

        stageInvitationStatusFilter = Mockito.mock(StageInvitationFilter.class);
        stageInvitationTeamMemberFilter = Mockito.mock(StageInvitationFilter.class);
    }

    @Test
    @DisplayName("testEndAnInvitation")
    public void sendAnInvitation() {
        when(stageInvitationMapper.toEntity(any(StageInvitationDto.class))).thenReturn(stageInvitation);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(stageInvitation);

        stageInvitationService.sendAnInvitation(stageInvitationDto);

        verify(stageInvitationMapper, times(1)).toDto(any(StageInvitation.class));
        assertEquals(StageInvitationStatus.PENDING, stageInvitation.getStatus());
    }

    @Test
    @DisplayName("testAcceptInvitation")
    public void acceptInvitation() {
        when(stageInvitationRepository.findById(stageInvitationDto.getId())).thenReturn(stageInvitation);
        when(teamMemberRepository.findById(stageInvitation.getInvited().getId())).thenReturn(teamMember);
        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(stageInvitationDto);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(stageInvitation);

        stageInvitationService.acceptInvatation(stageInvitationDto, 1L);

        verify(stageInvitationMapper, times(1)).toDto(any(StageInvitation.class));
        assertEquals(StageInvitationStatus.ACCEPTED, stageInvitation.getStatus());
    }

    @Test
    @DisplayName("testDeclineTheInvitation")
    public void declineTheInvitation() {
        when(stageInvitationRepository.findById(stageInvitationDto.getId())).thenReturn(stageInvitation);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(stageInvitation);
        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(stageInvitationDto);

        stageInvitationService.declineTheInvitation(stageInvitationDto);

        verify(stageInvitationMapper, times(1)).toDto(any(StageInvitation.class));
        assertEquals(StageInvitationStatus.REJECTED, stageInvitation.getStatus());
        assertEquals(stageInvitationDto.getDescription(), stageInvitation.getDescription());
    }

    @Test
    public void testStageInvitationFilterNull() {
        StageInvitationFilterDto stageInvitationFilterDto = null;

        try {
            if (stageInvitationFilterDto == null) {
                throw new IllegalArgumentException("filter is null");
            }
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("filter is null", e.getMessage());
        }
    }

    @Test
    @DisplayName("testGetStageInvitationForUser")
    public void getStageInvitationForUser() {
        when(stageInvitationRepository.findAll()).thenReturn(stageInvitationList);
        when(stageInvitationStatusFilter.isApplicable(any())).thenReturn(true);
        when(stageInvitationStatusFilter.apply(stageInvitationList.stream(), stageInvitationFilterDto)).thenReturn(stageInvintationFiltered.stream());
//        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(stageInvitationDto);

        List<StageInvitationDto> stageInvitationDtoResult = stageInvitationService.getStageInvitationForUser(stageInvitationFilterDto, 1L);

        verify(stageInvitationMapper, times(1)).toDto(any(StageInvitation.class));

        assertEquals(1, stageInvitationDtoResult.size());
        assertTrue(stageInvitationDtoResult.size() > 0);
    }
}