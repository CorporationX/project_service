package faang.school.projectservice.service;

import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.MeetJpaRepository;
import faang.school.projectservice.mapper.EventMeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.google.GoogleCalendarService;
import faang.school.projectservice.validator.MeetValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetServiceTest {

    private static final Long CREATOR_ID = 1L;
    private static final Long MEET_ID = 1L;
    private static final Long USER_ID = 10L;
    private static final String MEET_NAME = "Event";
    private static final String GOOGLE_EVENT_ID = "ZGUycG5uNXQ1";
    private static final String GOOGLE_EVENT_URL = "https://www.google.com/calendar/event?eid=ZGUycG5uNXQ1";
    private static final Long UPDATER_ID = 2L;
    private static final Long PROJECT_ID = 1L;

    @Mock
    private MeetJpaRepository meetJpaRepository;
    @Mock
    private MeetRepository meetRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private GoogleCalendarService googleCalendarService;
    @Mock
    private EventMeetMapper eventMeetMapper;
    @Mock
    private MeetValidator meetValidator;

    @InjectMocks
    private MeetService meetService;

    MeetDto meetDto;
    MeetDto savedMeetDto;
    Meet meet;
    Meet savedMeet;
    Event event;
    Project project;
    TeamMember updater;

    @BeforeEach
    public void init() {
        meetDto = MeetDto.builder()
                .name(MEET_NAME)
                .createdBy(CREATOR_ID)
                .meetStatus(MeetStatus.OPEN)
                .build();

        meet = Meet.builder()
                .name(MEET_NAME)
                .createdBy(CREATOR_ID)
                .build();

        savedMeet = Meet.builder()
                .id(MEET_ID)
                .name(MEET_NAME)
                .createdBy(CREATOR_ID)
                .eventGoogleId(GOOGLE_EVENT_ID)
                .eventGoogleUrl(GOOGLE_EVENT_URL)
                .build();

        savedMeetDto = MeetDto.builder()
                .name(MEET_NAME)
                .createdBy(CREATOR_ID)
                .eventGoogleId(GOOGLE_EVENT_ID)
                .eventGoogleUrl(GOOGLE_EVENT_URL)
                .build();

        event = new Event();
        event.setId(GOOGLE_EVENT_ID);
        event.setHtmlLink(GOOGLE_EVENT_URL);

        project = Project.builder()
                .id(PROJECT_ID)
                .build();

        updater = TeamMember.builder()
                .id(CREATOR_ID)
                .build();
    }

    @Test
    @DisplayName("Create a meet when creator not exists")
    public void createWhenCreatorNotExists() {
        String errMessage = "The meet creator is not from the project team";

        doThrow(new DataValidationException(errMessage))
                .when(meetValidator).validateExistsMeetCreator(meetDto);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> meetService.create(meetDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Create a meet when everything is fine")
    public void createWhenOK() {
        when(eventMeetMapper.toMeetEntity(meetDto)).thenReturn(meet);
        when(googleCalendarService.createEvent(meetDto)).thenReturn(event);
        when(meetJpaRepository.save(meet)).thenReturn(savedMeet);
        when(eventMeetMapper.toDto(savedMeet)).thenReturn(savedMeetDto);

        MeetDto actualResult = meetService.create(meetDto);
        assertEquals(savedMeetDto, actualResult);
    }

    @Test
    @DisplayName("Update a meet when meet not exists")
    public void updateWhenMeetNotExists() {
        String errMessage = String.format("Meet not found by id: %s", MEET_ID);

        when(meetRepository.findById(MEET_ID)).thenThrow(new EntityNotFoundException(errMessage));

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> meetService.update(meetDto, MEET_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Update a meet when creator not equals updater")
    public void updateWhenCreatorNotEqualsUpdater() {
        meetDto.setCreatedBy(UPDATER_ID);
        String errMessage = String.format("Team member not creator for this meet. Creator ID: %d, Updater ID: %d",
                CREATOR_ID, UPDATER_ID);

        when(meetRepository.findById(MEET_ID)).thenReturn(savedMeet);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> meetService.update(meetDto, MEET_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Update a meet when meet status is CANCELLED")
    public void updateWhenMeetStatusCancelled() {
        meetDto.setMeetStatus(MeetStatus.CANCELLED);
        meetDto.setEventGoogleId(GOOGLE_EVENT_ID);

        when(meetRepository.findById(MEET_ID)).thenReturn(savedMeet);
        savedMeet.setMeetStatus(MeetStatus.CANCELLED);
        when(meetJpaRepository.save(savedMeet)).thenReturn(savedMeet);
        when(eventMeetMapper.toDto(savedMeet)).thenReturn(savedMeetDto);


        MeetDto actualResult = meetService.update(meetDto, MEET_ID);
        assertEquals(savedMeetDto, actualResult);

        verify(googleCalendarService).deleteEvent(GOOGLE_EVENT_ID);
        verify(eventMeetMapper).update(meetDto, savedMeet);
        verify(meetJpaRepository).save(savedMeet);
    }

    @Test
    @DisplayName("Update a meet when OK")
    public void updateWhenOK() {
        meetDto.setEventGoogleId(GOOGLE_EVENT_ID);

        when(meetRepository.findById(MEET_ID)).thenReturn(savedMeet);
        when(meetJpaRepository.save(savedMeet)).thenReturn(savedMeet);
        when(eventMeetMapper.toDto(savedMeet)).thenReturn(savedMeetDto);


        MeetDto actualResult = meetService.update(meetDto, MEET_ID);
        assertEquals(savedMeetDto, actualResult);

        verify(googleCalendarService).updateEvent(meetDto);
        verify(eventMeetMapper).update(meetDto, savedMeet);
        verify(meetJpaRepository).save(savedMeet);
    }

    @Test
    @DisplayName("Delete a meet when meet not exists")
    public void deleteWhenMeetNotExists() {
        String errMessage = String.format("Meet not found by id: %s", MEET_ID);

        when(meetRepository.findById(MEET_ID)).thenThrow(new EntityNotFoundException(errMessage));

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> meetService.delete(MEET_ID, USER_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Delete a meet when teamMember for user not found")
    public void deleteWhenUserNotFound() {
        String errMessage = String.format("Team member doesn't exist by User ID: %s and Project ID: %s", USER_ID, PROJECT_ID);
        savedMeet.setProject(project);

        when(meetRepository.findById(MEET_ID)).thenReturn(savedMeet);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenThrow(new EntityNotFoundException(errMessage));

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> meetService.delete(MEET_ID, USER_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Delete a meet when success!")
    public void deleteWhenOK() {
        savedMeet.setProject(project);

        when(meetRepository.findById(MEET_ID)).thenReturn(savedMeet);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(updater);

        meetService.delete(MEET_ID, USER_ID);

        verify(googleCalendarService).deleteEvent(GOOGLE_EVENT_ID);
        verify(meetJpaRepository).deleteById(MEET_ID);
    }

    @Test
    @DisplayName("Find meet by id")
    public void findByIdTest() {
        when(meetRepository.findById(MEET_ID)).thenReturn(savedMeet);
        when(eventMeetMapper.toDto(savedMeet)).thenReturn(savedMeetDto);

        MeetDto actualResult = meetService.findById(MEET_ID);
        assertEquals(savedMeetDto, actualResult);
    }

}