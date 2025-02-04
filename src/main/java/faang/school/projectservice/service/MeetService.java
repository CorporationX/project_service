package faang.school.projectservice.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper;
    private final ProjectService projectService;
    private final UserValidator userValidator;
    private final AuditorAwareImpl auditorAware;
    private final Calendar calendar;
    private final UserServiceClient userServiceClient;

    @Transactional
    public MeetResponseDto createMeet(CreateMeetDto createMeetDto) {
        userValidator.validateCurrentUserExists();
        Meet meet = meetMapper.fromCreateDto(createMeetDto);
        meet.setCreatorId(auditorAware.getCurrentAuditor().get());
        meet.setProject(projectService.getProjectById(createMeetDto.getProjectId()));
        meet.setStatus(MeetStatus.PENDING);
        return meetMapper.toResponseDto(meetRepository.save(meet));
    }

    @Transactional(readOnly = true)
    public List<MeetResponseDto> findProjectMeetsByFilter(long projectId, MeetFilterDto filter) {
        return meetRepository.findByFilter(
                        projectId,
                        filter.getTitlePattern(),
                        Optional.ofNullable(filter.getDatePattern())
                                .map(LocalDate::toString)
                                .orElse(null))
                .map(meetMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeetResponseDto> findAll() {
        return meetRepository.findAll().stream()
                .map(meetMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MeetResponseDto findById(long id) {
        return meetMapper.toResponseDto(
                meetRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Meet no found by id: " + id))
        );
    }

    @Transactional
    public MeetResponseDto updateMeet(UpdateMeetDto updateMeetDto) {
        Meet meet = meetRepository.findById(updateMeetDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot update meet with id: " + updateMeetDto.getId() + ", because not found")
                );
        userValidator.validateUserIsMeetCreator(meet);
        meetMapper.update(meet, updateMeetDto);
        return meetMapper.toResponseDto(meetRepository.save(meet));
    }

    @Transactional
    public MeetResponseDto cancelMeet(long id) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot cancel meet with id: "
                        + id + ", because not found")
                );
        userValidator.validateUserIsMeetCreator(meet);
        meet.setStatus(MeetStatus.CANCELLED);
        return meetMapper.toResponseDto(meetRepository.save(meet));
    }

    @Transactional
    public void deleteMeet(long id) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot delete meet with id: "
                        + id + ", because not found")
                );
        userValidator.validateUserIsMeetCreator(meet);
        meetRepository.delete(meet);
    }

    public void addMeetToCalendar(long id) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot add meet to calendar with id: "
                        + id + ", because not found")
                );
        Event event = new Event();
        event.setDescription(meet.getDescription());
        event.setSummary(meet.getTitle());
        event.setStart(convertLocalToEventDateTime(meet.getStartsAt()));
        event.setEnd(convertLocalToEventDateTime(meet.getStartsAt().plusHours(1)));

        var meetAttendees = userServiceClient.getUsersByIds(meet.getUserIds());
        event.setAttendees(meetAttendees.stream()
                .map(userDto -> {
                    EventAttendee eventAttendee = new EventAttendee();
                    eventAttendee.setEmail(userDto.email());
                    eventAttendee.setId(String.valueOf(userDto.id()));
                    eventAttendee.setDisplayName(userDto.username());
                    return eventAttendee;
                })
                .toList()
        );

        try {
            if (meet.getProject().getGoogleCalendarId() == null) {
                var projectCalendar = new com.google.api.services.calendar.model.Calendar();
                projectCalendar.setSummary(meet.getProject().getName());
                var createdCalendarId = calendar.calendars().insert(projectCalendar).execute().getId();
                meet.getProject().setGoogleCalendarId(createdCalendarId);
                meetRepository.save(meet);
            }
            calendar.events()
                    .insert(meet.getProject().getGoogleCalendarId(), event)
                    .setSendNotifications(true)
                    .execute();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MeetResponseDto> getProjectCalendarMeets(long projectId) {
        try {
            var calendarId = projectService.getProjectById(projectId).getGoogleCalendarId();
            return calendar.events()
                    .list(calendarId)
                    .execute()
                    .getItems()
                    .stream()
                    .map(event -> {
                        MeetResponseDto meetDto = new MeetResponseDto();
                        meetDto.setTitle(event.getSummary());
                        DateTime dateTime = event.getStart().getDateTime();
                        Instant instant = Instant.ofEpochMilli(dateTime.getValue());
                        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
                        meetDto.setStartsAt(zonedDateTime.toLocalDateTime());
                        return meetDto;
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private EventDateTime convertLocalToEventDateTime(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        DateTime dateTime = new DateTime(zonedDateTime.toInstant().toEpochMilli());
        EventDateTime eventDateTime = new EventDateTime();
        eventDateTime.setDateTime(dateTime);
        eventDateTime.setTimeZone(TimeZone.getDefault().getID());
        return eventDateTime;
    }
}
