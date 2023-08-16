package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.filters.mappers.StageInvitationMapperImpl;
import faang.school.projectservice.filters.stageInvites.FilterStageInviteDto;
import faang.school.projectservice.filters.stageInvites.StageInviteFilter;
import faang.school.projectservice.filters.stageInvites.filtersForStagesInvitesDto.StageInviteAuthorPattern;
import faang.school.projectservice.filters.stageInvites.filtersForStagesInvitesDto.StageInviteDescriptionFilter;
import faang.school.projectservice.filters.stageInvites.filtersForStagesInvitesDto.StageInviteInvitedFilter;
import faang.school.projectservice.filters.stageInvites.filtersForStagesInvitesDto.StageInviteStageFilter;
import faang.school.projectservice.filters.stageInvites.filtersForStagesInvitesDto.StageInviteStatusFilter;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.stage.StageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StageInvitationServiceTest {
    @Mock
    private FilterStageInviteDto filterStageInviteDto;
    @Mock
    private StageInvitationRepository repository;
    @Mock
    private StageService stageService;
    @Spy
    private StageInvitationMapperImpl mapper;
    private List<StageInviteFilter> filters;
    @Mock
    private StageInvitationDto dto;
    @Mock
    private Stage stage;
    @Mock
    private TeamMember invited;
    @Mock
    private TeamMember author;
    private StageInvitationService service;
    private StageInvitation stageInvitation;
    private final Long STAGE_ID = 60L;
    private final Long AUTHOR_ID = 25L;
    private final Long INVITED_ID = 70L;
    private final Long STAGE_INVITE_ID = 10L;

    @BeforeEach
    void setUp() {
        filters = List.of(new StageInviteAuthorPattern(), new StageInviteInvitedFilter(),
                new StageInviteDescriptionFilter(), new StageInviteStageFilter(),
                new StageInviteStatusFilter());
        service = new StageInvitationService(repository, stageService, mapper, filters);
        filterStageInviteDto = FilterStageInviteDto.builder().stagePattern("stage")
                .descriptionPattern("desc").invitedPattern(INVITED_ID)
                .authorPattern(AUTHOR_ID).statusPattern(StageInvitationStatus.PENDING).build();
        author = TeamMember.builder().userId(AUTHOR_ID).roles(List.of(TeamRole.OWNER)).build();
        List<TeamMember> team = new ArrayList<>();
        team.add(author);
        stage = Stage.builder().stageId(STAGE_ID).stageName("stage").executors(team).build();
        invited = TeamMember.builder().userId(INVITED_ID).build();
        dto = StageInvitationDto.builder().id(STAGE_INVITE_ID).description("description")
                .stageId(STAGE_ID).invitedId(INVITED_ID).build();
        stageInvitation = mapper.dtoToEntity(dto);
        stageInvitation.setAuthor(author);
        stageInvitation.setStage(stage);
    }

    @Test
    void sendInviteSaveInviteIntoDBTest(){
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitation.setInvited(invited);
        Mockito.when(repository.save(stageInvitation)).thenReturn(stageInvitation);
        Mockito.when(repository.findById(dto.getId())).thenReturn(stageInvitation);
        Mockito.when(stageService.getEntityStageById(STAGE_ID)).thenReturn(stage);

        service.sendInvite(dto, 25L);

        assertEquals(repository.findById(dto.getId()).getInvited().getUserId(), dto.getInvitedId());
        assertEquals(repository.findById(dto.getId()).getStage().getStageId(), dto.getStageId());
        assertEquals(repository.findById(dto.getId()).getDescription(), dto.getDescription());
        assertEquals(repository.findById(dto.getId()).getId(), dto.getId());
        assertEquals(stageInvitation.getInvited(), invited);
        Mockito.verify(repository, Mockito.times(1))
                .save(stageInvitation);
    }

    @Test
    void acceptInviteChangeDBDataTest(){
        final int EXPECTED_POSITION_IN_LIST_OF_EXECUTORS_OF_INVITED = 1;
        Mockito.when(repository.findById(dto.getId())).thenReturn(stageInvitation);

        service.acceptInvite(dto, 10L);

        assertEquals(StageInvitationStatus.ACCEPTED, repository.findById(stageInvitation.getId()).getStatus());
        assertEquals(repository.findById(stageInvitation.getId()).getStage().getExecutors()
                .get(EXPECTED_POSITION_IN_LIST_OF_EXECUTORS_OF_INVITED), invited);
    }

    @Test
    void rejectInviteChangeDBDataTest(){
        final int EXPECTED_SIZE_OF_EXECUTORS_LIST = 1;
        Mockito.when(repository.findById(dto.getId())).thenReturn(stageInvitation);

        service.rejectInvite(dto, 10L);

        assertEquals(StageInvitationStatus.REJECTED, repository.findById(stageInvitation.getId()).getStatus());
        assertEquals(repository.findById(stageInvitation.getId()).getStage().getExecutors().size(),
                EXPECTED_SIZE_OF_EXECUTORS_LIST);
    }

    @Test
    void getFilteredInvitesReturnValidListTest(){
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        StageInvitation invalidStage = StageInvitation.builder().id(5L).description("invalid")
                .invited(invited).author(author).stage(stage).status(StageInvitationStatus.REJECTED).build();
        Mockito.when(repository.findAll()).thenReturn(List.of(stageInvitation, invalidStage));

        assertEquals(List.of(mapper.entityToDto(stageInvitation)).size(),
                service.getFilteredInvites(invited.getUserId(), filterStageInviteDto).size());
    }
}