package faang.school.projectservice.service;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.stageInvitation.StageInvitationAuthorFilter;
import faang.school.projectservice.filter.stageInvitation.StageInvitationFilter;
import faang.school.projectservice.filter.stageInvitation.StageInvitationStageFilter;
import faang.school.projectservice.filter.stageInvitation.StageInvitationStatusFilter;
import faang.school.projectservice.mapper.StageInvitationMapperImpl;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validate.StageInvitationValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
class StageInvitationServiceTest {
    @InjectMocks
    private StageInvitationService stageInvitationService;
    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @Spy
    private StageInvitationMapperImpl stageInvitationMapper;
    @Mock
    private StageInvitationValidator stageInvitationValidator;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    StageInvitation invitation;
    StageInvitation stageInvitation;
    StageInvitationDto invitationDto;
    List<StageInvitation> list;
    StageInvitationFilterDto filterDto;
    StageInvitation invitation1;
    StageInvitation invitation2;
    StageInvitation invitation3;
    StageInvitation invitation4;
    StageInvitation invitation5;

    @BeforeEach
    void setUp() {
        invitation = StageInvitation
                .builder()
                .id(1L)
                .description("description")
                .status(StageInvitationStatus.PENDING)
                .stage(Stage.builder().stageId(2L).build())
                .author(TeamMember.builder().id(3L).build())
                .invited(TeamMember.builder().id(4L).build())
                .build();

        stageInvitation = StageInvitation
                .builder()
                .id(1L)
                .description("description")
                .status(StageInvitationStatus.PENDING)
                .stage(Stage.builder().stageId(2L).build())
                .author(TeamMember.builder().id(3L).build())
                .invited(TeamMember.builder().id(4L).build())
                .build();


        invitationDto = StageInvitationDto
                .builder()
                .id(1L)
                .description("description")
                .status(StageInvitationStatus.PENDING)
                .stageId(2L)
                .authorId(3L)
                .invitedId(4L)
                .build();

        filterDto = StageInvitationFilterDto
                .builder()
                .authorIdPattern(1L)
                .stageIdPattern(1L)
                .statusPattern(StageInvitationStatus.ACCEPTED)
                .build();

        invitation1 = StageInvitation
                .builder()
                .author(TeamMember.builder().userId(1L).build())
                .invited(TeamMember.builder().userId(2L).build())
                .stage(Stage.builder().stageId(1L).build())
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        invitation2 = StageInvitation
                .builder()
                .author(TeamMember.builder().userId(1L).build())
                .invited(TeamMember.builder().userId(2L).build())
                .stage(Stage.builder().stageId(1L).build())
                .status(StageInvitationStatus.REJECTED)
                .build();

        invitation3 = StageInvitation
                .builder()
                .author(TeamMember.builder().userId(1L).build())
                .invited(TeamMember.builder().userId(2L).build())
                .stage(Stage.builder().stageId(1L).build())
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        invitation4 = StageInvitation
                .builder()
                .author(TeamMember.builder().userId(1L).build())
                .invited(TeamMember.builder().userId(2L).build())
                .stage(Stage.builder().stageId(2L).build())
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        invitation5 = StageInvitation
                .builder()
                .author(TeamMember.builder().userId(2L).build())
                .invited(TeamMember.builder().userId(2L).build())
                .stage(Stage.builder().stageId(1L).build())
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        list = List.of(
                invitation1,
                invitation2,
                invitation3,
                invitation4,
                invitation5);
    }

    @Test
    void testSuccessCreate() {
        Mockito.when(stageInvitationRepository.save(invitation))
                .thenReturn(invitation);
        StageInvitationDto dto = stageInvitationService.create(invitationDto);
        Assertions.assertEquals(invitationDto, dto);
    }

    @Test
    void testCreateStageInvitation() {
        StageInvitation build = StageInvitation.builder().build();
        stageInvitationService.createStageInvitation(build);
        Mockito.verify(stageInvitationRepository, Mockito.times(1)).save(build);
    }

    @Test
    void testAccept() {
        TeamMember teamMember = TeamMember.builder().id(1L).build();
        invitation.setStage(Stage.builder().stageId(1L).executors(new ArrayList<>()).build());
        stageInvitation.setStage(Stage.builder().stageId(1L).executors(new ArrayList<>(List.of(teamMember))).build());
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);

        Mockito.when(stageInvitationRepository.findById(1L))
                .thenReturn(invitation);
        Mockito.when(teamMemberRepository.findById(invitation.getInvited().getId()))
                .thenReturn(teamMember);

        StageInvitationDto accept = stageInvitationService.accept(1L);
        Assertions.assertEquals(StageInvitationStatus.ACCEPTED, accept.getStatus());
    }

    @Test
    void testReject() {
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitation.setDescription("descript");

        Mockito.when(stageInvitationRepository.findById(1L))
                .thenReturn(invitation);

        StageInvitationDto actual = stageInvitationService.reject(1L, "descript");
        Assertions.assertEquals(StageInvitationStatus.REJECTED, actual.getStatus());
        Assertions.assertEquals("descript", actual.getDescription());
    }

    @Test
    void getStageInvitationFilter() {
        List<StageInvitationFilter> filters = List.of(
                new StageInvitationAuthorFilter(),
                new StageInvitationStageFilter(),
                new StageInvitationStatusFilter());
        stageInvitationService = new StageInvitationService(
                stageInvitationRepository,
                stageInvitationMapper,
                stageInvitationValidator,
                teamMemberRepository,
                filters);

        Mockito.when(stageInvitationRepository.findAll())
                .thenReturn(list);

        List<StageInvitationDto> actual = stageInvitationService
                .getStageInvitationFilter(filterDto, 2L);
        List<StageInvitationDto> expected = List.of(
                stageInvitationMapper.toDto(invitation1),
                stageInvitationMapper.toDto(invitation3));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getStageInvitationIsBlankFilter() {
        List<StageInvitationFilter> filters = List.of();
        stageInvitationService = new StageInvitationService(
                stageInvitationRepository,
                stageInvitationMapper,
                stageInvitationValidator,
                teamMemberRepository,
                filters);

        Mockito.when(stageInvitationRepository.findAll())
                .thenReturn(list);

        List<StageInvitationDto> actual = stageInvitationService
                .getStageInvitationFilter(filterDto, 2L);
        List<StageInvitationDto> expected = stageInvitationMapper.toDtoList(list);

        Assertions.assertEquals(expected, actual);
    }
}