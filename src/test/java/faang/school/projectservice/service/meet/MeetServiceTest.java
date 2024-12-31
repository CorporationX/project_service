package faang.school.projectservice.service.meet;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetTitleDateFilter;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.meet.MeetMapperImpl;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MeetServiceTest {
    private static final String NOT_CREATOR_UPDATE_ERROR_MESSAGE = "Only creator can update meet";
    private static final String NOT_CREATOR_CHANGE_STATUS_ERROR_MESSAGE = "Only creator can change meet status";

    @Mock
    private MeetRepository meetRepository;
    @Mock
    private UserContext userContext;
    @Spy
    private MeetMapperImpl meetMapper;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private MeetService meetService;
    @Captor
    private ArgumentCaptor<Meet> meetArgumentCaptor;

    @Test
    void testCreateMeetCreated() {
        long userId = 1L;
        long projectId = 1L;
        Project project = new Project();
        MeetDto meetDto = MeetDto
                .builder()
                .title("title")
                .description("description")
                .status(MeetStatus.PENDING)
                .build();
        Meet meet = meetMapper.toEntity(meetDto);
        meet.setCreatorId(userId);
        meet.setProject(project);
        meet.setId(1L);

        when(userContext.getUserId()).thenReturn(userId);
        when(teamMemberService.validateUserIsProjectMember(userId, projectId)).thenReturn(new TeamMember());
        when(projectService.getById(projectId)).thenReturn(project);
        when(meetRepository.save(meetArgumentCaptor.capture())).thenReturn(meet);

        MeetDto createdMeetDto = meetService.createMeet(projectId, meetDto);

        Meet capturedMeet = meetArgumentCaptor.getValue();
        meet.setId(0L);
        assertEquals(meet, capturedMeet);
        meet.setId(1L);
        assertEquals(meetMapper.toDto(meet), createdMeetDto);
    }

    @Test
    void testUpdateMeetUpdated() {
        long userId = 1;
        Meet meet = getMeetWithCreator(userId);
        UpdateMeetDto updateMeetDto = new UpdateMeetDto("new title", "new description");

        when(userContext.getUserId()).thenReturn(userId);
        when(meetRepository.findById(meet.getId())).thenReturn(Optional.of(meet));
        when(teamMemberService.validateUserIsProjectMember(userId, meet.getProject().getId()))
                .thenReturn(new TeamMember());

        MeetDto result = meetService.updateMeet(meet.getId(), updateMeetDto);

        assertEquals(updateMeetDto.getTitle(), result.getTitle());
        assertEquals(updateMeetDto.getDescription(), result.getDescription());
    }

    @Test
    void testUpdateMeetWhenNotCreator() {
        long userId = 1;
        Meet meet = getMeetWithCreator(200);
        UpdateMeetDto updateMeetDto = new UpdateMeetDto("new title", "new description");

        when(userContext.getUserId()).thenReturn(userId);
        when(meetRepository.findById(meet.getId())).thenReturn(Optional.of(meet));
        when(teamMemberService.validateUserIsProjectMember(userId, meet.getProject().getId()))
                .thenReturn(new TeamMember());

        AccessDeniedException exception
                = assertThrows(AccessDeniedException.class, () -> meetService.updateMeet(meet.getId(), updateMeetDto));

        assertEquals(NOT_CREATOR_UPDATE_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void testChangeMeetStatusChanged() {
        long userId = 1;
        Meet meet = getMeetWithCreator(userId);
        MeetStatus meetStatus = MeetStatus.PENDING;

        when(userContext.getUserId()).thenReturn(userId);
        when(meetRepository.findById(meet.getId())).thenReturn(Optional.of(meet));
        when(teamMemberService.validateUserIsProjectMember(userId, meet.getProject().getId()))
                .thenReturn(new TeamMember());

        MeetDto result = meetService.changeMeetStatus(meet.getId(), meetStatus);

        assertEquals(meetStatus, result.getStatus());
        assertEquals(meetStatus, meet.getStatus());
    }

    @Test
    void testChangeMeetStatusWhenNotCreator() {
        long userId = 1;
        Meet meet = getMeetWithCreator(200);
        MeetStatus meetStatus = MeetStatus.PENDING;

        when(userContext.getUserId()).thenReturn(userId);
        when(meetRepository.findById(meet.getId())).thenReturn(Optional.of(meet));
        when(teamMemberService.validateUserIsProjectMember(userId, meet.getProject().getId()))
                .thenReturn(new TeamMember());

        AccessDeniedException exception
                = assertThrows(AccessDeniedException.class,
                () -> meetService.changeMeetStatus(meet.getId(), meetStatus));
        assertEquals(NOT_CREATOR_CHANGE_STATUS_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void testAddParticipant() {
        long userId = 1;
        long participantId = 2;
        Meet meet = getMeetWithCreator(userId);

        when(userContext.getUserId()).thenReturn(userId);
        when(meetRepository.findById(meet.getId())).thenReturn(Optional.of(meet));
        when(teamMemberService.validateUserIsProjectMember(userId, meet.getProject().getId()))
                .thenReturn(new TeamMember());

        meetService.addParticipant(meet.getId(), participantId);

        assertTrue(meet.getUserIds().contains(participantId));
    }

    @Test
    void testDeleteParticipant() {
        long userId = 1;
        long participantId = 2;
        Meet meet = getMeetWithCreator(userId);
        meet.getUserIds().add(participantId);

        when(userContext.getUserId()).thenReturn(userId);
        when(meetRepository.findById(meet.getId())).thenReturn(Optional.of(meet));
        when(teamMemberService.validateUserIsProjectMember(userId, meet.getProject().getId()))
                .thenReturn(new TeamMember());

        meetService.deleteParticipant(meet.getId(), participantId);

        assertFalse(meet.getUserIds().contains(participantId));
    }

    @Test
    void testGetForProjectFilteredByTitleAndDateRange() {
        long projectId = 1L;
        MeetTitleDateFilter meetTitleDateFilter =
                new MeetTitleDateFilter("%title%", LocalDateTime.now().minusDays(1L), LocalDateTime.now());
        Meet meet1 = getMeetWithCreator(1);
        Meet meet2 = getMeetWithCreator(2);
        Meet meet3 = getMeetWithCreator(3);
        List<Meet> meets = List.of(meet1, meet2, meet3);
        when(
                meetRepository.findAllForProjectFilterByTitlePatternAndDateRange(
                        meetTitleDateFilter.getTitle(),
                        meetTitleDateFilter.getMinDate(),
                        meetTitleDateFilter.getMaxDate(),
                        projectId
                )
        ).thenReturn(meets);

        meetService.getForProjectFilteredByTitleAndDateRange(projectId, meetTitleDateFilter);

        verify(meetRepository).findAllForProjectFilterByTitlePatternAndDateRange(
                meetTitleDateFilter.getTitle(),
                meetTitleDateFilter.getMinDate(),
                meetTitleDateFilter.getMaxDate(),
                projectId
        );
    }

    @Test
    void testGetAllProjectMeets() {
        long userId = 1;
        long projectId = 1;
        Meet meet1 = getMeetWithCreator(1);
        Meet meet2 = getMeetWithCreator(2);
        Meet meet3 = getMeetWithCreator(3);
        List<Meet> meets = List.of(meet1, meet2, meet3);
        when(userContext.getUserId()).thenReturn(userId);
        when(meetRepository.findAllByProject(projectId)).thenReturn(meets);
        List<MeetDto> expected = meets.stream().map(meetMapper::toDto).toList();

        List<MeetDto> actual = meetService.getAllProjectMeets(projectId);

        assertEquals(expected.size(), actual.size());
        expected.forEach(meet -> assertTrue(actual.contains(meet)));
        verify(teamMemberService).validateUserIsProjectMember(userId, projectId);
    }

    @Test
    void testGetById() {
        long userId = 1;
        Meet meet = getMeetWithCreator(userId);

        when(userContext.getUserId()).thenReturn(userId);
        when(meetRepository.findById(meet.getId())).thenReturn(Optional.of(meet));

        MeetDto result = meetService.getById(meet.getId());

        verify(teamMemberService).validateUserIsProjectMember(userId, meet.getProject().getId());
        assertEquals(meetMapper.toDto(meet), result);
    }

    private Meet getMeetWithCreator(long creatorId) {
        Meet meet = new Meet();
        meet.setId(222L);
        meet.setTitle("title");
        meet.setDescription("description");
        meet.setCreatorId(creatorId);
        meet.setProject(Project.builder().id(333L).build());
        meet.setUserIds(new ArrayList<>());
        return meet;
    }
}