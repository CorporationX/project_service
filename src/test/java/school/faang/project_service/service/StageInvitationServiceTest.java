package school.faang.project_service.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.stageInvitation.StageInvitationAcceptDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationCreateDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDeclineDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.filter.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.StageInvitationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {
    private final static long TEAM_MEMBER_ID = 1;
    private final static long USER_CONTEXT_ID = 1;
    private final static long STAGE_ID = 1;
    private final static String DESCRIPTION = "description";
    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @Mock
    private StageInvitationMapper stageInvitationMapper;
    @Mock
    private UserContext userContext;
    @Mock
    private StageInvitationFilter stageInvitationFilter;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private StageRepository stageRepository;
    @InjectMocks
    private StageInvitationServiceImpl stageInvitationService;

    @Captor
    private ArgumentCaptor<StageInvitation> invitationCaptor;

    @Test
    public void sendInvitation_notValidUserId_shouldThrowForbiddenException() {
        StageInvitationCreateDto dto = preparationCreateDto(TEAM_MEMBER_ID + TEAM_MEMBER_ID, TEAM_MEMBER_ID);
        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID);

        Assertions.assertThrows(ForbiddenException.class,
                () -> stageInvitationService.sendInvitation(dto));
    }

    @Test
    public void sendInvitation_existsByAuthorAndInvitedAndStage_shouldThrowEntityNotFoundException() {
        StageInvitationCreateDto dto = preparationCreateDto(TEAM_MEMBER_ID, TEAM_MEMBER_ID);
        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> stageInvitationService.sendInvitation(dto));
    }

    @Test
    public void sendInvitation_existsByAuthorAndInvitedAndStage_shouldThrowDataValidationException() {
        Long authorId = TEAM_MEMBER_ID;
        Long invitedId = TEAM_MEMBER_ID + 1;

        TeamMember author = new TeamMember();
        author.setId(authorId);
        TeamMember invited = new TeamMember();
        invited.setId(invitedId);
        Stage stage = new Stage();
        stage.setStageId(STAGE_ID);

        StageInvitationCreateDto dto = new StageInvitationCreateDto(STAGE_ID, authorId, invitedId, " ");

        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID);
        when(teamMemberRepository.findByUserId(authorId)).thenReturn(author);
        when(teamMemberRepository.findByUserId(invitedId)).thenReturn(invited);
        when(stageRepository.findById(STAGE_ID)).thenReturn(Optional.of(stage));

        when(stageInvitationRepository.existsByAuthorAndInvitedAndStage(author, invited, stage))
                .thenReturn(true);

        Assertions.assertThrows(DataValidationException.class,
                () -> stageInvitationService.sendInvitation(dto));
    }

    @Test
    public void sendInvitation_setStatus_shouldStatusSetPending() {
        Long authorId = TEAM_MEMBER_ID;
        Long invitedId = TEAM_MEMBER_ID + TEAM_MEMBER_ID;
        Long stageId = STAGE_ID;

        StageInvitationCreateDto dto = new StageInvitationCreateDto(stageId, authorId, invitedId, " ");
        TeamMember author = new TeamMember();
        author.setId(authorId);
        author.setUserId(1L);

        TeamMember invited = new TeamMember();
        invited.setId(invitedId);
        invited.setUserId(2L);

        Stage stage = new Stage();
        stage.setStageId(stageId);

        StageInvitationDto responseDto = new StageInvitationDto(
                "1",
                StageInvitationStatus.PENDING,
                stageId,
                invitedId
        );

        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID);

        when(teamMemberRepository.findByUserId(authorId)).thenReturn(author);
        when(teamMemberRepository.findByUserId(invitedId)).thenReturn(invited);

        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(responseDto);
        when(stageInvitationRepository.existsByAuthorAndInvitedAndStage(
                any(TeamMember.class),
                any(TeamMember.class),
                any(Stage.class))).thenReturn(false);

        StageInvitationDto result = stageInvitationService.sendInvitation(dto);

        assertEquals(StageInvitationStatus.PENDING, result.status());

        verify(stageInvitationRepository).save(any(StageInvitation.class));
    }

    @Test
    public void sendInvitation_responseMethod_shouldResponseDto() {
        Long authorId = TEAM_MEMBER_ID;
        Long invitedId = TEAM_MEMBER_ID + TEAM_MEMBER_ID;
        Long stageId = STAGE_ID + STAGE_ID + STAGE_ID;

        TeamMember author = new TeamMember();
        author.setId(authorId);
        author.setUserId(1L);

        TeamMember invited = new TeamMember();
        invited.setId(invitedId);
        invited.setUserId(2L);

        Stage stage = new Stage();
        stage.setStageId(stageId);

        StageInvitationCreateDto dto = new StageInvitationCreateDto(stageId, authorId, invitedId, " ");

        StageInvitationDto expectedResponseDto = new StageInvitationDto(
                "1",
                StageInvitationStatus.PENDING,
                stageId,
                invitedId
        );

        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID);

        when(teamMemberRepository.findByUserId(authorId)).thenReturn(author);
        when(teamMemberRepository.findByUserId(invitedId)).thenReturn(invited);

        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

        when(stageInvitationRepository.existsByAuthorAndInvitedAndStage(any(TeamMember.class), any(TeamMember.class), any(Stage.class)))
                .thenReturn(false);

        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(expectedResponseDto);

        StageInvitationDto responseDto = stageInvitationService.sendInvitation(dto);

        assertNotNull(responseDto, "Response DTO should not be null");
        assertEquals(StageInvitationStatus.PENDING, responseDto.status());
        assertEquals(invitedId, responseDto.invitedId());
        assertEquals(stageId, responseDto.stageId());

        verify(stageInvitationRepository).save(invitationCaptor.capture());
        StageInvitation responseInvitation = invitationCaptor.getValue();

        assertNotNull(responseInvitation);
        assertEquals(author, responseInvitation.getAuthor());
        assertEquals(invited, responseInvitation.getInvited());
        assertEquals(stage, responseInvitation.getStage());
        assertEquals(StageInvitationStatus.PENDING, responseInvitation.getStatus());
    }

    @Test
    public void acceptInvitation_notValidUserId_shouldThrowForbiddenException() {
        StageInvitationAcceptDto dto =
                new StageInvitationAcceptDto(TEAM_MEMBER_ID, TEAM_MEMBER_ID);

        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID + USER_CONTEXT_ID);

        Assertions.assertThrows(ForbiddenException.class,
                () -> stageInvitationService.acceptInvitation(dto));
    }

    @Test
    public void acceptInvitation_stageInvitationByIdOrThrow_shouldThrowEntityNotFoundException() {
        StageInvitationAcceptDto dto =
                new StageInvitationAcceptDto(TEAM_MEMBER_ID, TEAM_MEMBER_ID);

        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> stageInvitationService.acceptInvitation(dto));
    }

    @Test
    public void acceptInvitation_existsByAuthorAndInvitedAndStage_shouldThrowEntityNotFoundException() {
        StageInvitationAcceptDto dto =
                new StageInvitationAcceptDto(TEAM_MEMBER_ID, TEAM_MEMBER_ID);
        StageInvitation stageInvitation = new StageInvitation();

        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID);
        when(stageInvitationRepository.findById(TEAM_MEMBER_ID)).thenReturn(Optional.of(stageInvitation));
        when(stageInvitationRepository.existsByAuthorAndInvitedAndStage(any(), any(), any())).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> stageInvitationService.acceptInvitation(dto));
    }

    @Test
    public void acceptInvitation_setStatus_shouldStatusAccepted() {
        StageInvitationAcceptDto dto = new StageInvitationAcceptDto(TEAM_MEMBER_ID, TEAM_MEMBER_ID);

        StageInvitation stageInvitation = mock(StageInvitation.class);
        Stage stage = new Stage();
        stage.setExecutors(new ArrayList<>());

        when(stageInvitation.getStage()).thenReturn(stage);
        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID);
        when(stageInvitationRepository.findById(TEAM_MEMBER_ID)).thenReturn(Optional.of(stageInvitation));
        when(stageInvitationRepository.existsByAuthorAndInvitedAndStage(any(), any(), any())).thenReturn(true);

        stageInvitationService.acceptInvitation(dto);

        verify(stageInvitation).setStatus(StageInvitationStatus.ACCEPTED);
    }

    @Test
    public void acceptInvitation_addExecutor_shouldExecutorHasAdded() {
        StageInvitationAcceptDto dto = new StageInvitationAcceptDto(TEAM_MEMBER_ID, TEAM_MEMBER_ID);

        StageInvitation stageInvitation = new StageInvitation();
        Stage stage = mock(Stage.class);

        List<TeamMember> executors = new ArrayList<>();

        stageInvitation.setStage(stage);

        TeamMember invited = new TeamMember();
        invited.setId(TEAM_MEMBER_ID);
        stageInvitation.setInvited(invited);

        when(stage.getExecutors()).thenReturn(executors);
        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID);
        when(stageInvitationRepository.findById(TEAM_MEMBER_ID)).thenReturn(Optional.of(stageInvitation));
        when(stageInvitationRepository.existsByAuthorAndInvitedAndStage(any(), any(), any())).thenReturn(true);

        stageInvitationService.acceptInvitation(dto);

        Assertions.assertTrue(executors.contains(invited));
        Assertions.assertEquals(1, executors.size());
    }

    @Test
    public void declineInvitation_stageInvitationByIdOrThrow_shouldThrowEntityNotFoundException() {
        StageInvitationDeclineDto dto = new StageInvitationDeclineDto(TEAM_MEMBER_ID, DESCRIPTION);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> stageInvitationService.declineInvitation(dto));
    }

    @Test
    public void declineInvitation_notValidUserId_shouldThrowForbiddenException() {
        StageInvitationDeclineDto dto = new StageInvitationDeclineDto(TEAM_MEMBER_ID, DESCRIPTION);
        StageInvitation stageInvitation = new StageInvitation();
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(TEAM_MEMBER_ID);
        stageInvitation.setInvited(teamMember);

        when(stageInvitationRepository.findById(TEAM_MEMBER_ID)).thenReturn(Optional.of(stageInvitation));
        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID + USER_CONTEXT_ID);

        Assertions.assertThrows(ForbiddenException.class,
                () -> stageInvitationService.declineInvitation(dto));
    }

    @Test
    @DisplayName("setStatusAndDescription")
    public void declineInvitation_setStatusAndDescription_shouldSetStatusRejectedAndDescription() {
        StageInvitationDeclineDto dto = new StageInvitationDeclineDto(TEAM_MEMBER_ID, DESCRIPTION);
        StageInvitation stageInvitation = mock(StageInvitation.class);
        TeamMember teamMember = mock(TeamMember.class);

        when(stageInvitation.getInvited()).thenReturn(teamMember);
        when(teamMember.getUserId()).thenReturn(USER_CONTEXT_ID);
        when(stageInvitationRepository.findById(TEAM_MEMBER_ID)).thenReturn(Optional.of(stageInvitation));
        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID);

        stageInvitationService.declineInvitation(dto);

        verify(stageInvitation).setStatus(StageInvitationStatus.REJECTED);
        verify(stageInvitation).setDescription(DESCRIPTION);
    }

    @Test
    public void viewAllInvitationsByFilter_notValidateUserId_shouldThrowForbiddenException() {
        StageInvitationFilterDto dto = new StageInvitationFilterDto(TEAM_MEMBER_ID, StageInvitationStatus.ACCEPTED, STAGE_ID);
        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID + USER_CONTEXT_ID);
        Assertions.assertThrows(ForbiddenException.class,
                () -> stageInvitationService.viewAllInvitationsByFilter(dto));
    }

    @Test
    public void viewAllInvitationsByFilter_responseDto_shouldResponseDtoList() {
        StageInvitationFilterDto dto = new StageInvitationFilterDto(TEAM_MEMBER_ID, StageInvitationStatus.ACCEPTED, STAGE_ID);
        List<StageInvitation> list = new ArrayList<>();
        Specification<StageInvitation> specification = (root, query, criteriaBuilder) -> null;
        when(userContext.getUserId()).thenReturn(USER_CONTEXT_ID);
        when(stageInvitationFilter.specificationStageInvitationByTeamMemberId(TEAM_MEMBER_ID)).thenReturn(specification);


        stageInvitationService.viewAllInvitationsByFilter(dto);

        verify(stageInvitationMapper).toInvitationListDto(list);
    }

    public StageInvitationCreateDto preparationCreateDto(long authorId, long invitedId) {
        return new StageInvitationCreateDto(STAGE_ID, authorId, invitedId, "");
    }
}