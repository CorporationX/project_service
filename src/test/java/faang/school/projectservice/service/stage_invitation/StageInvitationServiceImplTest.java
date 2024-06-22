package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationCreateDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.publisher.InviteSentPublisher;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validation.stage_invitation.StageInvitationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageInvitationServiceImplTest {

    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @Mock
    private StageInvitationMapper stageInvitationMapper;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private StageInvitationValidator stageInvitationValidator;
    @Mock
    private StageInvitationFilterService stageInvitationFilterService;
    @Mock
    private InviteSentPublisher inviteSentPublisher;

    @InjectMocks
    private StageInvitationServiceImpl stageInvitationService;

    private final long stageId = 1L;
    private final long authorId = 2L;
    private StageInvitationCreateDto stageInvitationCreateDto;
    private StageInvitationDto stageInvitationDto;
    private StageInvitation stageInvitation;
    private TeamMember author;
    private Stage stage;

    @BeforeEach
    void setUp() {
        
        long invitedId = 3L;

        stageInvitationCreateDto = StageInvitationCreateDto.builder()
                .stageId(stageId)
                .authorId(authorId)
                .invitedId(invitedId)
                .build();

        stageInvitationDto = StageInvitationDto.builder()
                .id(4L)
                .stageId(stageId)
                .authorId(authorId)
                .invitedId(invitedId)
                .build();

        author = TeamMember.builder().id(authorId).build();

        stage = Stage.builder()
                .stageId(stageId)
                .project(Project.builder().id(5L).build())
                .executors(new ArrayList<>())
                .build();

        stageInvitation = StageInvitation.builder()
                .id(4L)
                .author(author)
                .invited(TeamMember.builder().id(invitedId).build())
                .stage(stage)
                .build();
    }

    @Test
    void sendInvitation() {
        when(stageInvitationMapper.toEntity(stageInvitationCreateDto)).thenReturn(stageInvitation);
        when(stageInvitationRepository.save(stageInvitation)).thenReturn(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);
        when(stageRepository.getById(stageId)).thenReturn(stage);

        StageInvitationDto actual = stageInvitationService.sendInvitation(stageInvitationCreateDto);
        assertEquals(stageInvitationDto, actual);

        InOrder inOrder = inOrder(stageInvitationMapper, stageRepository, inviteSentPublisher, stageInvitationValidator, stageInvitationRepository);
        inOrder.verify(stageInvitationMapper).toEntity(stageInvitationCreateDto);
        inOrder.verify(stageInvitationValidator).validateExistences(stageInvitationCreateDto);
        inOrder.verify(stageRepository).getById(stageId);
        inOrder.verify(inviteSentPublisher).publish(any());
        inOrder.verify(stageInvitationRepository).save(stageInvitation);
        inOrder.verify(stageInvitationMapper).toDto(stageInvitation);
    }

    @Test
    void acceptInvitation() {
        when(teamMemberRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(stageInvitationRepository.findById(stageId)).thenReturn(Optional.of(stageInvitation));
        when(stageInvitationRepository.save(stageInvitation)).thenReturn(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);

        StageInvitationDto actual = stageInvitationService.acceptInvitation(authorId, stageId);
        assertEquals(stageInvitationDto, actual);
        assertEquals(StageInvitationStatus.ACCEPTED, stageInvitation.getStatus());
        assertTrue(stageInvitation.getStage().getExecutors().contains(author));

        InOrder inOrder = inOrder(teamMemberRepository, stageInvitationValidator, stageInvitationRepository, stageInvitationMapper);
        inOrder.verify(teamMemberRepository).findById(authorId);
        inOrder.verify(stageInvitationRepository).findById(stageId);
        inOrder.verify(stageInvitationValidator).validateUserInvitePermission(author, stageInvitation);
        inOrder.verify(stageInvitationRepository).save(stageInvitation);
        inOrder.verify(stageInvitationMapper).toDto(stageInvitation);
    }

    @Test
    void rejectInvitation() {
        when(teamMemberRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(stageInvitationRepository.findById(stageId)).thenReturn(Optional.of(stageInvitation));
        when(stageInvitationRepository.save(stageInvitation)).thenReturn(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);

        StageInvitationDto actual = stageInvitationService.rejectInvitation(authorId, stageId, "TEST");
        assertEquals(stageInvitationDto, actual);
        assertEquals(StageInvitationStatus.REJECTED, stageInvitation.getStatus());
        assertEquals("TEST", stageInvitation.getDescription());

        InOrder inOrder = inOrder(teamMemberRepository, stageInvitationValidator, stageInvitationRepository, stageInvitationMapper);
        inOrder.verify(teamMemberRepository).findById(authorId);
        inOrder.verify(stageInvitationRepository).findById(stageId);
        inOrder.verify(stageInvitationValidator).validateUserInvitePermission(author, stageInvitation);
        inOrder.verify(stageInvitationRepository).save(stageInvitation);
        inOrder.verify(stageInvitationMapper).toDto(stageInvitation);
    }

    @Test
    void getInvitationsWithFilters() {
        StageInvitationFilterDto filterDto = StageInvitationFilterDto.builder().build();
        when(stageInvitationRepository.findAll()).thenReturn(List.of(stageInvitation));
        when(stageInvitationFilterService.applyAll(any(), eq(filterDto))).thenReturn(Stream.of(stageInvitation));
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);

        List<StageInvitationDto> actual = stageInvitationService.getInvitationsWithFilters(filterDto);
        assertEquals(stageInvitationDto, actual.get(0));

        InOrder inOrder = inOrder(stageInvitationRepository, stageInvitationFilterService, stageInvitationMapper);
        inOrder.verify(stageInvitationRepository).findAll();
        inOrder.verify(stageInvitationFilterService).applyAll(any(), eq(filterDto));
        inOrder.verify(stageInvitationMapper).toDto(stageInvitation);
    }
}