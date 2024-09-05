package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.PageRequestDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.mapper.MeetMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.meet.Meet;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.meet.MeetService;
import faang.school.projectservice.service.meet.filter.EndDateFilter;
import faang.school.projectservice.service.meet.filter.MeetFilter;
import faang.school.projectservice.service.meet.filter.StartDateFilter;
import faang.school.projectservice.service.meet.filter.TitleFilter;
import faang.school.projectservice.service.team.TeamService;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.meet.MeetValidator;
import faang.school.projectservice.validator.team.TeamValidator;
import faang.school.projectservice.validator.user.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetServiceTest {

    @Mock
    private TeamService teamService;

    @Mock
    private ProjectRepository projectRepository;

    private MeetMapper meetMapper;

    @Mock
    private MeetRepository meetRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private UserValidator userValidator;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private TeamValidator teamValidator;

    @Mock
    private MeetValidator meetValidator;

    private Long userId;
    private Long teamId;
    private Long projectId;
    private Long meetId;
    private Meet meet;
    private MeetDto meetDto;
    private Team team;
    private TeamDto teamDto;
    private Project project;
    private MeetFilterDto meetFilterDto;

    private Page meetPage;

    private MeetService meetService;

    @BeforeEach
    public void init() {
        userId = 1L;
        teamId = 1L;
        projectId = 1L;
        meetId = 1L;

        meetFilterDto = MeetFilterDto.builder()
                .titlePattern("title")
                .startDatePattern(LocalDateTime.now().minusDays(3))
                .endDatePattern(LocalDateTime.now())
                .build();

        meet = Meet.builder()
                .id(meetId)
                .title("title")
                .description("description")
                .createdBy(userId)
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().minusDays(1))
                .build();

        meetDto = MeetDto.builder()
                .id(meetId)
                .title("title")
                .description("description")
                .createdBy(userId)
                .projectId(projectId)
                .teamId(teamId)
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().minusDays(1))
                .build();

        team = Team.builder()
                .id(teamId)
                .teamMeets(List.of(meet))
                .build();

        teamDto = TeamDto.builder()
                .id(teamId)
                .teamMeetsId(List.of(meetId))
                .build();

        project = Project.builder().id(1L).build();
        team = Team.builder().id(teamId).build();

        List<MeetFilter> filters = List.of(new TitleFilter(), new StartDateFilter(), new EndDateFilter());

        meetMapper = new MeetMapperImpl();


        meetService = MeetService.builder()
                .teamService(teamService)
                .projectRepository(projectRepository)
                .meetRepository(meetRepository)
                .meetMapper(meetMapper)
                .meetFilters(filters)
                .userContext(userContext)
                .userValidator(userValidator)
                .projectValidator(projectValidator)
                .teamValidator(teamValidator)
                .meetValidator(meetValidator)
                .build();

        meetPage = new PageImpl<>(List.of(meet));
    }

    @DisplayName("Test successfully creating meet")
    @Test
    public void testCreateMeet() {
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).isUserActive(userId);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(teamService.getById(teamId)).thenReturn(teamDto);
        doNothing().when(teamValidator).doesTeamHaveAnUser(any(TeamDto.class), any(Long.class));
        when(meetRepository.save(meet)).thenReturn(meet);

        meetService.create(meetDto);

        verify(userValidator, times(1)).isUserActive(userContext.getUserId());
        verify(projectRepository, times(1)).getProjectById(meetDto.getProjectId());
        verify(teamService, times(1)).getById(meetDto.getTeamId());
        verify(teamValidator, times(1)).doesTeamHaveAnUser(teamDto, userContext.getUserId());
        verify(meetRepository, times(1)).save(meet);
    }

    @DisplayName("Test successfully updating meet")
    @Test
    public void testUpdateMeet() {
        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(meetValidator).userIsMeetCreator(userId, userId);
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);

        MeetDto updatedMeetDto = meetService.update(meetDto);

        assertNotNull(updatedMeetDto);
        assertEquals(meetDto.getTitle(), updatedMeetDto.getTitle());
        assertEquals(meetDto.getDescription(), updatedMeetDto.getDescription());
        assertEquals(meetDto.getStatus(), updatedMeetDto.getStatus());

        verify(meetRepository, times(1)).findById(meetId);
        verify(meetValidator, times(1)).userIsMeetCreator(userId, userId);
        verify(meetRepository, times(1)).save(meet);
    }

    @DisplayName("Test successfully delete by id")
    @Test
    public void testDelete() {
        when(userContext.getUserId()).thenReturn(userId);
        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));
        doNothing().when(meetValidator).userIsMeetCreator(userId, userId);
        doNothing().when(meetRepository).deleteById(meetId);

        meetService.delete(meetId);

        verify(userContext, times(1)).getUserId();
        verify(meetValidator, times(1)).userIsMeetCreator(userId, userId);
        verify(meetRepository, times(1)).deleteById(meetId);
    }

    @DisplayName("Test successfully getting meet by id")
    @Test
    public void testGetById() {
        when(meetRepository.findById(meetId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            meetService.getById(meetId);
        });

        assertEquals(String.format("Meet %s id doesn't exist", meetId), exception.getMessage());

        verify(meetRepository, times(1)).findById(meetId);
    }

    @DisplayName("Test successfully getting project meets by filter")
    @Test
    public void testGetProjectMeetsByFilter() {
        when(meetRepository.getByProjectId(projectId)).thenReturn(List.of(meet));

        List<MeetDto> result = meetService.getProjectMeetsByFilter(projectId, meetFilterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(projectValidator, times(1)).existsProject(projectId);
        verify(meetRepository, times(1)).getByProjectId(projectId);
        assertEquals(1, result.size());
    }

    @DisplayName("Test successfully getting meets by page")
    @Test
    public void testGetAllPageable() {
        PageRequestDto pageRequestDto = PageRequestDto.builder()
                .page(0)
                .size(10)
                .sortDirection("desc")
                .sortBy("createdAt")
                .build();

        Sort sort = Sort.by(pageRequestDto.getSortBy()).descending();
        Pageable pageable = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);

        when(meetRepository.findAll(pageable)).thenReturn(meetPage);

        Page<MeetDto> result = meetService.getAllPageable(pageRequestDto);

        assertNotNull(result);
        assertEquals(List.of(meet).size(), result.getContent().size());
        assertEquals(List.of(meet).get(0).getId(), result.getContent().get(0).getId());

        verify(meetRepository, times(1)).findAll(pageable);
    }
}
