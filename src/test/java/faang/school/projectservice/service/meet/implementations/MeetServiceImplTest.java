package faang.school.projectservice.service.meet.implementations;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetCreateDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.MeetUpdateDto;
import faang.school.projectservice.exception.EntityAlreadyExistException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.UserNotFoundException;
import faang.school.projectservice.mapper.meet.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.specification.MeetSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetServiceImplTest {
    @InjectMocks
    private MeetServiceImpl meetService;
    @Mock
    private MeetRepository meetRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private MeetMapper meetMapper;
    @Mock
    private MeetSpecification meetSpecification;
    private long userId;
    private long projectId;
    private long meetId;
    private Project project;
    private Meet meet;
    private MeetCreateDto meetCreateDto;
    private MeetResponseDto meetResponseDto;
    private MeetUpdateDto meetUpdateDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        projectId = 1L;
        meetId = 1L;

        project = Project.builder()
                .id(projectId)
                .name("project name")
                .description("project description")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .ownerId(userId)
                .meets(List.of())
                .build();
        meetCreateDto = MeetCreateDto.builder()
                .title("meet title")
                .description("meet description")
                .startsAt(LocalDateTime.parse("2025-04-01T10:00:00"))
                .projectId(projectId)
                .build();
        meetUpdateDto = MeetUpdateDto.builder()
                .title("some meet title")
                .description("some meet description")
                .startsAt(LocalDateTime.parse("2025-04-05T12:00:00"))
                .projectId(projectId)
                .id(meetId)
                .build();
        meet = Meet.builder()
                .id(meetId)
                .title(meetCreateDto.getTitle())
                .description(meetCreateDto.getDescription())
                .startsAt(meetCreateDto.getStartsAt())
                .project(project)
                .creatorId(userId)
                .build();
        meetResponseDto = MeetResponseDto.builder()
                .id(meetId)
                .title(meetCreateDto.getTitle())
                .description(meetCreateDto.getDescription())
                .projectId(meetCreateDto.getProjectId())
                .startsAt(meetCreateDto.getStartsAt())
                .creatorId(userId)
                .build();
    }

    @Test
    void testCreateMeetWhenSuccessful() {
        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(meetMapper.toEntity(meetCreateDto, userId)).thenReturn(meet);
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);
        when(meetMapper.toDto(meet)).thenReturn(meetResponseDto);

        MeetResponseDto result = meetService.createMeet(meetCreateDto);

        verify(meetRepository, times(1)).save(meet);
        assertNotNull(result);
        assertEquals(meetCreateDto.getTitle(), result.getTitle());
        assertEquals(meetCreateDto.getProjectId(), result.getProjectId());
        assertEquals(userId, result.getCreatorId());

        verify(userServiceClient).getUser(userId);
        verify(projectRepository).findById(projectId);
        verify(meetRepository).save(any(Meet.class));
    }

    @Test
    void testCreateMeetWhenUserNotFound() {
        when(userContext.getUserId()).thenReturn(userId);
        doThrow(UserNotFoundException.class).when(userServiceClient).getUser(userId);

        assertThrows(UserNotFoundException.class, () -> meetService.createMeet(meetCreateDto),
                "User with id " + userId + " not found");
    }

    @Test
    void testCreateMeetWhenProjectNotFound() {
        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meetService.createMeet(meetCreateDto),
                "Project not found: projectId: " + projectId);
    }

    @Test
    void testCreateMeetWhenMeetAlreadyExists() {
        project.setMeets(List.of(meet));
        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThrows(EntityAlreadyExistException.class, () -> meetService.createMeet(meetCreateDto),
                "Meet with title " + meetCreateDto.getTitle() + " already exists: meetId: " + meet.getId());
    }

    @Test
    void testUpdateMeetWhenSuccessful() {
        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.findById(meetUpdateDto.getProjectId())).thenReturn(Optional.of(project));
        when(meetRepository.findById(meetUpdateDto.getId())).thenReturn(Optional.of(meet));
        when(meetRepository.save(meet)).thenReturn(meet);
        when(meetMapper.toDto(meet)).thenReturn(meetResponseDto);

        MeetResponseDto result = meetService.updateMeet(meetUpdateDto);

        assertNotNull(result);

        verify(meetRepository, times(1)).save(meet);
        verify(meetMapper, times(1)).updateMeetFromDto(meetUpdateDto, meet);
    }

    @Test
    void testDeleteMeet() {
        when(userContext.getUserId()).thenReturn(userId);
        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));

        meetService.deleteMeet(meetId);

        verify(meetRepository, times(1)).delete(meet);
    }

    @Test
    void testGetMeetSuccessful() {
        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetResponseDto);

        meetService.getMeet(meetId);

        assertNotNull(meetResponseDto);
        assertEquals(meetId, meetResponseDto.getId());
        verify(meetRepository, times(1)).findById(meetId);
    }

    @Test
    void testGetMeetWhenNotFound() {
        when(meetRepository.findById(meetId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meetService.getMeet(meetId),
                "Meet not found: meetId: " + meetId);
    }

    @Test
    void testGetFilteredMeetsByProjectIdSuccessful() {
        Meet firstMeet = Meet.builder()
                .id(1L).title("First meet").description("First meet description")
                .startsAt(LocalDateTime.parse("2025-04-01T10:00:00")).project(project).creatorId(userId)
                .build();
        Meet secondMeet = Meet.builder()
                .id(2L).title("Second meet").description("Second meet description")
                .startsAt(LocalDateTime.parse("2025-04-02T10:00:00")).project(project).creatorId(userId)
                .build();
        MeetResponseDto firstDto = MeetResponseDto.builder()
                .id(firstMeet.getId()).title(firstMeet.getTitle()).description(firstMeet.getDescription())
                .projectId(projectId).startsAt(firstMeet.getStartsAt()).creatorId(userId)
                .build();
        MeetResponseDto secondDto = MeetResponseDto.builder()
                .id(secondMeet.getId()).title(secondMeet.getTitle()).description(secondMeet.getDescription())
                .projectId(projectId).startsAt(secondMeet.getStartsAt()).creatorId(userId)
                .build();
        List<Meet> meets = List.of(firstMeet, secondMeet);
        List<MeetResponseDto> expectedDtos = List.of(firstDto, secondDto);

        MeetFilterDto filterDto = MeetFilterDto.builder().build();
        Specification<Meet> spec = (root, query, cb) -> null;

        when(meetSpecification.filterBy(projectId, filterDto.getTitle(),
                filterDto.getStartDate(),
                filterDto.getEndDate()))
                .thenReturn(spec);
        when(meetRepository.findAll(spec)).thenReturn(meets);
        when(meetMapper.toDto(meets)).thenReturn(expectedDtos);

        List<MeetResponseDto> result = meetService.getFilteredMeetsByProjectId(projectId, filterDto);

        verify(meetRepository, times(1)).findAll(spec);
    }
}