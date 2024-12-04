package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage.invitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapperImpl;
import faang.school.projectservice.model.team.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {

    private StageInvitationService stageInvitationService;
    private StageInvitationRepository stageInvitationRepository;
    private StageService stageService;
    private TeamMemberService teamMemberService;
    private StageInvitationMapperImpl stageInvitationMapper;

    private StageInvitationDto stageInvitationDto;
    private StageInvitation newStageInvitation;
    private StageInvitation existingStageInvitation;
    private Stage stage;
    private TeamMember author;
    private TeamMember invited;
    private long stageInvitationId;
    private long stageId;
    private long authorId;
    private long invitedId;

    @BeforeEach
    void setup() {
        stageInvitationId = 1L;
        stageId = 2L;
        authorId = 3L;
        invitedId = 4L;

        stage = Stage.builder()
                .stageId(stageId)
                .build();

        author = TeamMember.builder()
                .id(authorId)
                .build();

        invited = TeamMember.builder()
                .id(invitedId)
                .build();

        newStageInvitation = StageInvitation.builder()
                .id(stageInvitationId)
                .build();

        existingStageInvitation = StageInvitation.builder()
                .id(stageInvitationId)
                .stage(stage)
                .author(author)
                .invited(invited)
                .status(StageInvitationStatus.PENDING)
                .build();

        stageInvitationDto =
                new StageInvitationDto(stageInvitationId, stageId, authorId, invitedId);

        stageInvitationRepository = Mockito.mock(StageInvitationRepository.class);
        stageService = Mockito.mock(StageService.class);
        teamMemberService = Mockito.mock(TeamMemberService.class);
        stageInvitationMapper = Mockito.mock(StageInvitationMapperImpl.class);
        StageInvitationFilter filter = Mockito.mock(StageInvitationFilter.class);
        List<StageInvitationFilter> filters = List.of(filter);

        stageInvitationService = new StageInvitationService(stageInvitationRepository,
                stageService,
                teamMemberService,
                stageInvitationMapper,
                filters);
    }

    @Test
    public void testSendStageInvitation() {
        // arrange
        when(stageInvitationMapper.toEntity(stageInvitationDto)).thenReturn(newStageInvitation);
        when(stageService.getStage(stageId)).thenReturn(stage);
        when(teamMemberService.getTeamMember(authorId)).thenReturn(author);
        when(teamMemberService.getTeamMember(invitedId)).thenReturn(invited);

        // act
        stageInvitationService.sendInvitation(stageInvitationDto);

        // assert
        verify(stageInvitationRepository).save(newStageInvitation);
    }

    @Test
    public void testAcceptStageInvitation() {
        // arrange
        when(stageInvitationRepository.findById(stageInvitationId)).
                thenReturn(existingStageInvitation);

        // act
        stageInvitationService.acceptInvitation(stageInvitationId);

        // assert
        assertEquals(StageInvitationStatus.ACCEPTED, existingStageInvitation.getStatus());
    }

    @Test
    public void testRejectStageInvitation() {
        // arrange
        when(stageInvitationRepository.findById(stageInvitationId)).
                thenReturn(existingStageInvitation);
        String rejectionReason = "Some reason";

        // act
        stageInvitationService.rejectInvitation(stageInvitationId, rejectionReason);

        // assert
        assertEquals(StageInvitationStatus.REJECTED, existingStageInvitation.getStatus());
    }

    @Test
    public void testGetFilteredStageInvitations() {
        // arrange
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto(stageId, authorId);
        long invitedId = 5L;

        // act
        stageInvitationService.getAllFilteredInvitations(invitedId, filterDto);

        // assert
        verify(stageInvitationMapper).toDto(Mockito.anyList());
    }
}
