package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.stage_invitation.StageInvitationAuthorIdFilter;
import faang.school.projectservice.filter.stage_invitation.StageInvitationFilter;
import faang.school.projectservice.filter.stage_invitation.StageInvitationStatusFilter;
import faang.school.projectservice.mapper.stage_invitation.StageInvitationMapperImpl;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.validator.StageInvitationValidator;
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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {
    @Mock
    private StageInvitationRepository repository;
    @Mock
    private StageInvitationValidator validator;
    @Mock
    private List<StageInvitationFilter> filters;
    @Spy
    private StageInvitationMapperImpl mapper;
    @InjectMocks
    private StageInvitationService service;

    @Test
    public void testSuccessCreate() {
        StageInvitationDto invitationDto = StageInvitationDto.builder()
                .stageId(1L)
                .invitedId(2L)
                .authorId(1L)
                .build();
        service.create(invitationDto);
        StageInvitation stageInvitation = mapper.toEntity(invitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        verify(repository, times(1)).save(stageInvitation);
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
        verify(repository, times(1)).save(invitation);
    }

    @Test
    public void testReject() {
        StageInvitation stageInvitation = StageInvitation.builder().build();
        StageInvitationDto invitationDto = StageInvitationDto.builder()
                .description("message")
                .status(StageInvitationStatus.REJECTED)
                .build();

        when(repository.findById(1L)).thenReturn(stageInvitation);
        when(repository.save(stageInvitation)).thenReturn(stageInvitation);
        StageInvitationDto result = service.reject(1L, "message");
        Assertions.assertEquals(invitationDto.getStatus(), result.getStatus());
        Assertions.assertEquals(invitationDto.getDescription(), result.getDescription());
    }

    @Test
    public void testAllInviteByFilter() {
        long userId = 1L;
        List<StageInvitationFilter> filters = List.of(new StageInvitationAuthorIdFilter(),
                new StageInvitationStatusFilter());
        List<StageInvitation> stageInvitations = List.of(
                StageInvitation.builder()
                        .stage(Stage.builder().stageId(1L).build())
                        .author(TeamMember.builder().id(1L).build())
                        .invited(TeamMember.builder().id(1L).build())
                        .build(),
                StageInvitation.builder()
                        .stage(Stage.builder().stageId(2L).build())
                        .author(TeamMember.builder().id(1L).build())
                        .invited(TeamMember.builder().id(3L).build())
                        .build(),
                StageInvitation.builder()
                        .stage(Stage.builder().stageId(4L).build())
                        .author(TeamMember.builder().id(3L).build())
                        .invited(TeamMember.builder().id(1L).build())
                        .build()
        );
        StageInvitationFilterDto filter = StageInvitationFilterDto.builder()
                .authorId(1L)
                .build();

        StageInvitationService testService = new StageInvitationService(validator, mapper, repository, filters);
        when(repository.findAll()).thenReturn(stageInvitations);
        Assertions.assertEquals(1, testService.findAllInviteByFilter(filter, userId).size());
    }
}
