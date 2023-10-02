package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.stage_invitation.StageInvitationAuthorIdFilter;
import faang.school.projectservice.filter.stage_invitation.StageInvitationFilter;
import faang.school.projectservice.filter.stage_invitation.StageInvitationStageIdFilter;
import faang.school.projectservice.mapper.stage_invitation.StageInvitationMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.publisher.InviteSentEventPublisher;
import faang.school.projectservice.publisher.event.InviteSentEvent;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {
    @Mock
    private StageInvitationRepository repository;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private TeamMemberRepository TMRepository;
    @Mock
    private InviteSentEventPublisher publisher;
    @Spy
    private StageInvitationMapperImpl mapper;
    @InjectMocks
    private StageInvitationService service;

    private StageInvitationDto validInvitationDto;
    private StageInvitationDto invalidInvitationDto;
    private StageInvitation validStageInvitation;

    @Test
    public void testSuccessCreate() {
        validInvitationDto = StageInvitationDto.builder()
                .stageId(1L)
                .invitedId(2L)
                .authorId(1L)
                .build();
        InviteSentEvent event = InviteSentEvent.builder()
                .authorId(1L)
                .invitedId(2L)
                .projectId(1L)
                .build();
        Mockito.when(stageRepository.getById(validInvitationDto.getStageId()))
                .thenReturn(Stage.builder()
                        .stageId(1L)
                        .executors(List.of(TeamMember.builder().id(1L).build()))
                        .project(Project.builder().id(1L).build())
                        .build());

        service.create(validInvitationDto);
        StageInvitation stageInvitation = mapper.toEntity(validInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        Mockito.verify(repository, Mockito.times(1)).save(stageInvitation);
        Mockito.verify(publisher, Mockito.times(1)).publish(event);
    }

    @Test
    public void testCreateAuthorNotFound() {
        invalidInvitationDto = StageInvitationDto.builder()
                .stageId(1L)
                .invitedId(2L)
                .authorId(1L)
                .build();
        Mockito.when(stageRepository.getById(invalidInvitationDto.getStageId())).thenReturn(Stage.builder().stageId(1L).executors(List.of()).build());
        Assertions.assertThrows(DataValidationException.class, () -> service.create(invalidInvitationDto));
    }

    @Test
    public void testAccept() {
        Stage stage = Stage.builder()
                .executors(new ArrayList<>())
                .build();
        TeamMember member = TeamMember.builder().build();
        StageInvitation invitation = StageInvitation.builder()
                .status(StageInvitationStatus.PENDING)
                .stage(stage)
                .invited(member)
                .build();

        Mockito.when(repository.findById(1L)).thenReturn(invitation);
        service.accept(1L);
        Assertions.assertTrue(stage.getExecutors().contains(member));
        Assertions.assertEquals(StageInvitationStatus.ACCEPTED, invitation.getStatus());
    }

    @Test
    public void testAcceptInvitedIsExecutor() {
        TeamMember member = TeamMember.builder().build();
        Stage stage = Stage.builder()
                .executors(new ArrayList<>(List.of(member)))
                .build();
        StageInvitation invitation = StageInvitation.builder()
                .status(StageInvitationStatus.PENDING)
                .stage(stage)
                .invited(member)
                .build();
        Mockito.when(repository.findById(1L)).thenReturn(invitation);
        service.accept(1L);
        Assertions.assertTrue(stage.getExecutors().contains(member));
        Assertions.assertEquals(StageInvitationStatus.ACCEPTED, invitation.getStatus());
        Assertions.assertEquals(1, stage.getExecutors().size());
    }

    @Test
    public void testReject() {
        validStageInvitation = StageInvitation.builder().build();
        validInvitationDto = StageInvitationDto.builder()
                .description("message")
                .status(StageInvitationStatus.REJECTED)
                .build();

        Mockito.when(repository.findById(1L)).thenReturn(validStageInvitation);
        Mockito.when(repository.save(validStageInvitation)).thenReturn(validStageInvitation);
        StageInvitationDto result = service.reject(1L, "message");
        Assertions.assertEquals(validInvitationDto.getStatus(), result.getStatus());
        Assertions.assertEquals(validInvitationDto.getDescription(), result.getDescription());
    }

    @Test
    public void testGetStageInvitationsWithFilters() {
        List<StageInvitationFilter> filters = List.of(new StageInvitationStageIdFilter(),
                new StageInvitationAuthorIdFilter()
        );
        var testService = new StageInvitationService(repository, stageRepository, TMRepository, mapper, filters, publisher);
        List<StageInvitation> stageInvitations = List.of(
                StageInvitation.builder()
                        .stage(Stage.builder().stageId(1L).build())
                        .author(TeamMember.builder().id(1L).build())
                        .build(),
                StageInvitation.builder()
                        .stage(Stage.builder().stageId(2L).build())
                        .author(TeamMember.builder().id(1L).build())
                        .build(),
                StageInvitation.builder()
                        .stage(Stage.builder().stageId(1L).build())
                        .author(TeamMember.builder().id(3L).build())
                        .build()
        );
        var filter = StageInvitationFilterDto.builder()
                .stageId(1L)
                .authorId(1L)
                .build();
        Mockito.when(repository.findAll()).thenReturn(stageInvitations);
        Assertions.assertEquals(1, testService.getStageInvitationsWithFilters(filter).size());
    }
}
