package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.filter.meet.MeetEndFilter;
import faang.school.projectservice.filter.meet.MeetFilter;
import faang.school.projectservice.filter.meet.MeetStartFilter;
import faang.school.projectservice.filter.meet.MeetTitleFilter;
import faang.school.projectservice.jpa.MeetJpaRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MeetMapperImpl;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.meet.Meet;
import faang.school.projectservice.model.meet.MeetStatus;
import faang.school.projectservice.validator.TeamValidator;
import faang.school.projectservice.validator.meet.MeetValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetServiceTest {

    @Mock
    private UserContext userContext;

    @Mock
    private MeetValidator meetValidator;

    @Mock
    private TeamValidator teamValidator;

    @Mock
    private MeetJpaRepository meetJpaRepository;

    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;

    private MeetService meetService;

    @Captor
    private ArgumentCaptor<Meet> meetArgumentCaptor;

    private long userId;
    private long teamId;
    private long meetId;
    private Team team;
    private Meet meet;
    private MeetDto meetDto;
    private MeetDto updateMeetDto;
    private MeetFilterDto meetFilterDto;
    private Meet appropriateMeet;
    private MeetMapperImpl meetMapperImpl;
    private List<TeamMember> teamMemberList;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        teamId = 2L;
        meetId = 3L;
        Meet notAppropriateTitleMeet = Meet.builder()
                .title("not")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now()).build();
        Meet notAppropriateStartDateMeet = Meet.builder()
                .title("Title")
                .startDate(LocalDateTime.now().minusDays(4))
                .endDate(LocalDateTime.now()).build();
        Meet notAppropriateEndDateMeet = Meet.builder()
                .title("Title")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(4)).build();
        appropriateMeet = Meet.builder()
                .title("Title")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now()).build();
        List<Meet> teamMeets = new ArrayList<>(List.of(
                notAppropriateTitleMeet, notAppropriateStartDateMeet,
                notAppropriateEndDateMeet, appropriateMeet));

        team = Team.builder()
                .id(teamId)
                .meets(teamMeets)
                .build();
        meet = Meet.builder()
                .title("title")
                .team(team)
                .status(MeetStatus.SCHEDULED)
                .createdBy(userId)
                .build();
        meetDto = MeetDto.builder()
                .id(meetId)
                .title("title")
                .teamId(teamId)
                .status(MeetStatus.SCHEDULED)
                .createdBy(userId)
                .build();
        updateMeetDto = MeetDto.builder()
                .title("new title")
                .build();
        meetFilterDto = MeetFilterDto.builder()
                .titlePattern("title")
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();
        List<MeetFilter> meetFiltersImpl = Arrays.asList(
                new MeetStartFilter(),
                new MeetEndFilter(),
                new MeetTitleFilter()
        );
        teamMemberList = List.of(TeamMember.builder()
                .userId(userId)
                .team(team)
                .build());
        meetMapperImpl = new MeetMapperImpl();
        meetService = MeetService.builder()
                .userContext(userContext)
                .meetMapper(meetMapperImpl)
                .meetValidator(meetValidator)
                .teamValidator(teamValidator)
                .meetJpaRepository(meetJpaRepository)
                .teamMemberJpaRepository(teamMemberJpaRepository)
                .meetFilters(meetFiltersImpl)
                .build();
    }

    @Test
    @DisplayName("testing createMethod method")
    void testCreateMeet() {
        when(userContext.getUserId()).thenReturn(userId);
        when(teamValidator.verifyTeamExistence(teamId)).thenReturn(team);
        when(meetJpaRepository.save(meet)).thenReturn(meet);
        meetService.createMeet(meetDto);
        verify(userContext, times(1)).getUserId();
        verify(teamValidator, times(1)).verifyTeamExistence(teamId);
        verify(teamValidator, times(1)).verifyUserExistenceInTeam(userId, team);
        verify(meetJpaRepository, times(1)).save(meet);
    }

    @Test
    @DisplayName("testing updateMeet method")
    void testUpdateMeet() {
        when(userContext.getUserId()).thenReturn(userId);
        when(meetValidator.verifyMeetExistence(meetId)).thenReturn(meet);
        when(meetJpaRepository.save(meet)).thenReturn(meet);
        meetService.updateMeet(meetId, updateMeetDto);
        verify(userContext, times(1)).getUserId();
        verify(meetValidator, times(1)).verifyMeetExistence(meetId);
        verify(meetValidator, times(1)).verifyUserIsCreatorOfMeet(userId, meet);
        verify(meetJpaRepository, times(1)).save(meetArgumentCaptor.capture());
    }

    @Test
    @DisplayName("testing deleteMeet method")
    void testDeleteMeet() {
        when(userContext.getUserId()).thenReturn(userId);
        when(meetValidator.verifyMeetExistence(meetId)).thenReturn(meet);
        meetService.deleteMeet(meetId);
        verify(userContext, times(1)).getUserId();
        verify(meetValidator, times(1)).verifyUserIsCreatorOfMeet(userId, meet);
        verify(meetJpaRepository, times(1)).delete(meet);
    }

    @Nested
    @DisplayName("Method: getFilteredMeetsOfTeam")
    class testGetFilteredMeetsOfTeam {

        @Test
        @DisplayName("testing getFilteredMeetsOfTeam methods execution")
        void testGetFilteredMeetsOfTeamMethodsExecution() {
            when(userContext.getUserId()).thenReturn(userId);
            when(teamValidator.verifyTeamExistence(teamId)).thenReturn(team);
            meetService.getFilteredMeetsOfTeam(teamId, meetFilterDto);
            verify(userContext, times(1)).getUserId();
            verify(teamValidator, times(1)).verifyTeamExistence(teamId);
            verify(teamValidator, times(1)).verifyUserExistenceInTeam(userId, team);
        }

        @Test
        @DisplayName("testing getFilteredMeetsOfTeam filters")
        void testGetFilteredMeetsOfTeamFilters() {
            when(userContext.getUserId()).thenReturn(userId);
            when(teamValidator.verifyTeamExistence(teamId)).thenReturn(team);
            List<MeetDto> filteredMeetsOfTeam = meetService.getFilteredMeetsOfTeam(teamId, meetFilterDto);
            verify(userContext, times(1)).getUserId();
            verify(teamValidator, times(1)).verifyTeamExistence(teamId);
            verify(teamValidator, times(1)).verifyUserExistenceInTeam(userId, team);
            assertEquals(1, filteredMeetsOfTeam.size());
            assertEquals(meetMapperImpl.toDto(appropriateMeet), filteredMeetsOfTeam.get(0));
        }
    }

    @Test
    @DisplayName("testing getFilteredMeetsOfTeam method")
    void testGetAllMeetsOfUser() {
        when(userContext.getUserId()).thenReturn(userId);
        when(teamMemberJpaRepository.findByUserId(userId)).thenReturn(teamMemberList);
        List<MeetDto> allMeetsOfUser = meetService.getAllMeetsOfUser();
        verify(userContext, times(1)).getUserId();
        assertEquals(team.getMeets().size(), allMeetsOfUser.size());
    }

    @Test
    @DisplayName("testing getMethodById method")
    void testGetMeetById() {
        meet.setId(meetId);
        when(userContext.getUserId()).thenReturn(userId);
        when(meetValidator.verifyMeetExistence(meetId)).thenReturn(meet);
        MeetDto meetDtoById = meetService.getMeetById(meetId);
        verify(userContext, times(1)).getUserId();
        verify(meetValidator, times(1)).verifyMeetExistence(meetId);
        verify(teamValidator, times(1)).verifyUserExistenceInTeam(userId, meet.getTeam());
        assertEquals(meetDto, meetDtoById);
    }
}