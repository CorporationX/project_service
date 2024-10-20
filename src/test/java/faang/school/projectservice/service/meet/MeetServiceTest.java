package faang.school.projectservice.service.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.exception.CredentialsNotFoundException;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.GoogleCalendarTokenRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.MeetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetServiceTest {
    @Mock
    private MeetRepository meetRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MeetMapper meetMapper;

    @Mock
    private GoogleCalendarTokenRepository googleCalendarTokenRepository;

    @InjectMocks
    private MeetService meetService;

    private Meet meet;
    private MeetDto meetDto;
    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);

        meet = new Meet();
        meet.setId(1L);
        meet.setTitle("Team Sync Meeting");
        meet.setDescription("Monthly team sync meeting.");
        meet.setProject(project);
        meet.setCreatorId(1L);
        meet.setStatus(MeetStatus.PENDING);
        meet.setCreatedAt(LocalDateTime.now());

        meetDto = new MeetDto();
        meetDto.setId(1L);
        meetDto.setTitle("Team Sync Meeting");
        meetDto.setDescription("Monthly team sync meeting.");
        meetDto.setProjectId(1L);
        meetDto.setCreatorId(1L);
    }

    @Test
    public void testCreateMeeting_WithoutGoogleCredentials() {
        when(projectRepository.getByIdOrThrow(1L)).thenReturn(project);
        when(meetMapper.toEntity(meetDto)).thenReturn(meet);
        when(meetRepository.save(meet)).thenReturn(meet);
        when(googleCalendarTokenRepository.findByProjectId(1L)).thenReturn(Optional.empty());

        assertThrows(CredentialsNotFoundException.class, () -> {
            meetService.createMeeting(meetDto);
        });

        verify(meetRepository, times(1)).save(meet);
        verify(projectRepository, times(1)).getByIdOrThrow(1L);
        verify(googleCalendarTokenRepository, times(1)).findByProjectId(1L);
    }

    @Test
    public void testUpdateMeeting() {
        when(meetRepository.findById(1L)).thenReturn(Optional.of(meet));
        when(meetMapper.toEntity(meetDto)).thenReturn(meet);
        when(meetRepository.save(meet)).thenReturn(meet);
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        MeetDto updatedMeeting = meetService.updateMeeting(1L, meetDto);

        assertNotNull(updatedMeeting);
        verify(meetRepository, times(1)).save(meet);
    }

    @Test
    public void testUpdateMeeting_NotFound() {
        when(meetRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> meetService.updateMeeting(1L, meetDto));
    }

    @Test
    public void testDeleteMeeting() {
        when(meetRepository.findById(1L)).thenReturn(Optional.of(meet));

        meetService.deleteMeeting(1L);

        verify(meetRepository, times(1)).save(meet);
        assertEquals(MeetStatus.CANCELLED, meet.getStatus());
    }

    @Test
    public void testDeleteMeeting_NotFound() {
        when(meetRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> meetService.deleteMeeting(1L));
    }

    @Test
    public void testGetMeetingById() {
        when(meetRepository.findById(1L)).thenReturn(Optional.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        MeetDto foundMeeting = meetService.getMeetingById(1L);

        assertNotNull(foundMeeting);
        assertEquals(meetDto.getId(), foundMeeting.getId());
    }

    @Test
    public void testGetMeetingById_NotFound() {
        when(meetRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> meetService.getMeetingById(1L));
    }
}
