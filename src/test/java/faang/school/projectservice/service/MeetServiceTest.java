package faang.school.projectservice.service;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.mapper.MeetMapperImpl;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetServiceTest {
    @Mock
    private MeetRepository meetRepository;
    @Mock
    private UserValidator userValidator;
    @Mock
    private AuditorAwareImpl auditorAware;
    @Spy
    private MeetMapperImpl meetMapper;
    @Mock
    private ProjectService projectService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private Calendar calendar;
    @InjectMocks
    private MeetService meetService;

    private CreateMeetDto createMeetDto;
    private UpdateMeetDto updateMeetDto;
    private Meet meet;

    @BeforeEach
    void setUp() {
        createMeetDto = new CreateMeetDto();
        createMeetDto.setProjectId(1L);

        updateMeetDto = new UpdateMeetDto();
        updateMeetDto.setId(1L);

        meet = new Meet();
        meet.setId(1L);
        meet.setStatus(MeetStatus.PENDING);
        meet.setStartsAt(LocalDateTime.now());
        meet.setProject(new Project());
    }

    @Test
    void createMeet_ShouldReturnMeetResponseDto() {
        // Given
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(1L));
        when(projectService.getProjectById(createMeetDto.getProjectId())).thenReturn(new Project());
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);

        // When
        MeetResponseDto responseDto = meetService.createMeet(createMeetDto);

        // Then
        assertEquals(MeetStatus.PENDING, responseDto.getStatus());
        verify(meetRepository, times(1)).save(any(Meet.class));
    }

    @Test
    void findByFilter_ShouldReturnListOfMeetResponseDtoWhenValidFilter() {
        // Given
        MeetFilterDto filter = new MeetFilterDto();
        when(meetRepository.findByFilter(anyLong(), any(), any())).thenReturn(Stream.of(meet));

        // When
        List<MeetResponseDto> responseDtos = meetService.findProjectMeetsByFilter(1L, filter);

        // Then
        assertFalse(responseDtos.isEmpty());
        verify(meetRepository, times(1)).findByFilter(anyLong(), any(), any());
    }

    @Test
    void findById_ShouldThrowExceptionWhenMeetNotFound() {
        // Given
        when(meetRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> meetService.findById(1L));
    }

    @Test
    void findById_ShouldReturnMeetResponseDto() {
        // Given
        when(meetRepository.findById(1L)).thenReturn(Optional.of(meet));

        // When
        meetService.findById(1L);

        // Then
        verify(meetRepository, times(1)).findById(1L);
    }

    @Test
    void updateMeet_ShouldThrowExceptionWhenMeetNotFound() {
        // Given
        when(meetRepository.findById(updateMeetDto.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> meetService.updateMeet(updateMeetDto));
    }

    @Test
    void updateMeet_ShouldReturnMeetResponseDto() {
        // Given
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);

        // When
        meetService.updateMeet(updateMeetDto);

        // Then
        verify(meetRepository, times(1)).save(any(Meet.class));
    }

    @Test
    void cancelMeet_ShouldThrowExceptionWhenMeetNotFound() {
        // Given
        when(meetRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> meetService.cancelMeet(1L));
    }

    @Test
    void cancelMeet_ShouldReturnMeetResponseDtoWhenMeetFound() {
        // Given
        when(meetRepository.findById(1L)).thenReturn(Optional.of(meet));
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);

        // When
        MeetResponseDto responseDto = meetService.cancelMeet(1L);

        // Then
        assertEquals(MeetStatus.CANCELLED, responseDto.getStatus());
        verify(meetRepository, times(1)).save(any(Meet.class));
    }

    @Test
    void deleteMeet_ShouldCallDeleteById() {
        // Given
        when(meetRepository.findById(1L)).thenReturn(Optional.of(new Meet()));

        // When
        meetService.deleteMeet(1L);

        // Then
        verify(meetRepository, times(1)).delete(any(Meet.class));
    }

    @Test
    void addMeetToCalendar_ShouldCreateCalendarAndSaveMeet() throws Exception {
        //Given
        final var calendars = mock(Calendar.Calendars.class);
        final var events = mock(Calendar.Events.class);
        final var calendarInsert = mock(Calendar.Calendars.Insert.class);
        final var eventInsert = mock(Calendar.Events.Insert.class);
        final var executeResult = new com.google.api.services.calendar.model.Calendar();
        executeResult.setId("1");
        when(calendar.calendars()).thenReturn(calendars);
        when(calendar.events()).thenReturn(events);
        when(calendars.insert(any())).thenReturn(calendarInsert);
        when(calendarInsert.execute()).thenReturn(executeResult);
        when(events.insert(any(), any())).thenReturn(eventInsert);
        when(eventInsert.setSendNotifications(any())).thenReturn(eventInsert);
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        when(userServiceClient.getUsersByIds(any())).thenReturn(Collections.emptyList());

        //When
        meetService.addMeetToCalendar(1L);

        //Then
        verify(meetRepository, times(1)).save(any());
        verify(calendars, times(1)).insert(any());
        verify(events, times(1)).insert(any(), any());
    }

    @Test
    void getProjectCalendarMeets_ShouldCallGoogleApi() throws Exception {
        //Given
        final var events = mock(Calendar.Events.class);
        final var list = mock(Calendar.Events.List.class);
        final var eventExecute = new Events();
        meet.setProject(Project.builder().id(1L).googleCalendarId("1").build());
        eventExecute.setItems(Collections.emptyList());
        when(calendar.events()).thenReturn(events);
        when(projectService.getProjectById(anyLong())).thenReturn(meet.getProject());
        when(events.list(any())).thenReturn(list);
        when(list.execute()).thenReturn(eventExecute);

        //When
        meetService.getProjectCalendarMeets(1L);

        //Then
        verify(list, times(1)).execute();
    }
}
