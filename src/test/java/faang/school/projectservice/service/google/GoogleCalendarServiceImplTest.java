package faang.school.projectservice.service.google;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.google.GoogleCalendarConfig;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.event.EventDto;
import faang.school.projectservice.repository.GoogleTokenRepository;
import faang.school.projectservice.validator.google.GoogleCalendarValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoogleCalendarServiceImplTest {
    private static final long EVENT_ID = 1L;
    private static final long USER_ID = 2L;

    @Mock
    private GoogleTokenRepository googleTokenRepository;
    @Mock
    private GoogleCalendarConfig googleCalendarConfig;
    @Mock
    private GoogleCalendarValidator googleCalendarValidator;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private GoogleCalendarServiceImpl googleCalendarService;
    private EventDto eventDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 2, 3, 4);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 2, 4, 5);
        eventDto = EventDto.builder()
                .id(EVENT_ID)
                .title("title")
                .description("description")
                .startDate(startTime)
                .endDate(endTime)
                .location("location")
                .build();
        userDto = new UserDto();
        userDto.setId(USER_ID);
        userDto.setParticipatedEventIds(List.of(EVENT_ID));
    }

    @Test
    public void whenCreateEventAndThrowsEntityNotFoundException() {
        when(googleCalendarValidator.checkUserAndEvent(anyLong(), anyLong())).thenThrow(EntityNotFoundException.class);
        Assert.assertThrows(EntityNotFoundException.class,
                () -> googleCalendarService.createEvent(USER_ID, EVENT_ID));
    }

    @Test
    public void whenCreateEventThenGetAuthorizationLink() throws IOException {
        String link = String.format("http://example%d-%d.com", USER_ID, EVENT_ID);
        String expected = String.format("follow the link to authorize in calendar: %s", link);
        when(googleCalendarValidator.checkUserAndEvent(anyLong(), anyLong())).thenReturn(true);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(userServiceClient.getEventById(EVENT_ID)).thenReturn(eventDto);
        when(googleTokenRepository.existsGoogleTokenByUserId(USER_ID)).thenReturn(false);
        when(googleCalendarConfig.getAuthorizationUrl(USER_ID, EVENT_ID)).thenReturn(link);
        String actual = googleCalendarService.createEvent(USER_ID, EVENT_ID);
        assertThat(actual).isEqualTo(expected);
    }
}