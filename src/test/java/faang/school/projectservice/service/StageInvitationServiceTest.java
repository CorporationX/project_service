package faang.school.projectservice.service;

import static org.junit.jupiter.api.Assertions.*;
import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.filters.mappers.StageInvitationMapper;
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
    @Spy
    private StageInvitationMapper mapper = new StageInvitationMapperImpl();
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

    @BeforeEach
    void setUp() {
        filters = List.of(new StageInviteAuthorPattern(), new StageInviteInvitedFilter(),
                new StageInviteDescriptionFilter(), new StageInviteStageFilter(),
                new StageInviteStatusFilter());
        service = new StageInvitationService(repository, mapper, filters);
        filterStageInviteDto = FilterStageInviteDto.builder().stagePattern("stage")
                .descriptionPattern("desc").invitedPattern(70L)
                .authorPattern(25L).statusPattern(StageInvitationStatus.PENDING).build();
        author = TeamMember.builder().userId(25L).roles(List.of(TeamRole.OWNER)).build();
        List<TeamMember> team = new ArrayList<>();
        team.add(author);
        stage = Stage.builder().stageName("stage").executors(team).build();
        invited = TeamMember.builder().userId(70L).build();
        dto = StageInvitationDto.builder().id(10L).description("description").stage(stage).invited(invited).build();
        stageInvitation = mapper.dtoToEntity(dto);
        stageInvitation.setAuthor(author);
    }

    @Test
    void sendInviteSaveInviteIntoDBTest(){
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        Mockito.when(repository.save(stageInvitation)).thenReturn(stageInvitation);
        Mockito.when(repository.findById(dto.getId())).thenReturn(stageInvitation);

        service.sendInvite(dto);

        assertEquals(repository.findById(dto.getId()).getInvited(), dto.getInvited());
        assertEquals(repository.findById(dto.getId()).getStage(), dto.getStage());
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

        service.acceptInvite(dto);

        assertEquals(StageInvitationStatus.ACCEPTED, repository.findById(stageInvitation.getId()).getStatus());
        assertEquals(repository.findById(stageInvitation.getId()).getStage().getExecutors()
                .get(EXPECTED_POSITION_IN_LIST_OF_EXECUTORS_OF_INVITED), invited);
    }

    @Test
    void rejectInviteChangeDBDataTest(){
        final int EXPECTED_SIZE_OF_EXECUTORS_LIST = 1;
        Mockito.when(repository.findById(dto.getId())).thenReturn(stageInvitation);

        service.rejectInvite(dto);

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