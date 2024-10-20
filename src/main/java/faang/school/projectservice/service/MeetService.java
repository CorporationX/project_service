package faang.school.projectservice.service;

import faang.school.projectservice.dto.calendar.CalendarEventDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.exception.CredentialsNotFoundException;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.GoogleCalendarTokenRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;


@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final ProjectRepository projectRepository;
    private final CalendarService calendarService;
    private final MeetMapper meetMapper;
    private final GoogleCalendarTokenRepository googleCalendarTokenRepository;
    private final ExecutorService meetCompletionThreadPool;

    @Value("${google.calendar_id}")
    private String calendarId;

    @Value("${meet.scheduler.batch.size}")
    private int BATCH_SIZE = 5;

    @Transactional
    public MeetDto createMeeting(MeetDto meetDto) {
        Project project = projectRepository.getByIdOrThrow(meetDto.getProjectId());

        Meet meet = meetMapper.toEntity(meetDto);
        meet.setProject(project);

        Meet createdMeet = meetRepository.save(meet);

        googleCalendarTokenRepository.findByProjectId(meet.getProject().getId()).ifPresentOrElse(calendarToken -> {
            CalendarEventDto eventDto = buildEventDto(createdMeet);
            CalendarEventDto createdEvent = calendarService.createEvent(meet.getProject().getId(), calendarId, eventDto);

            createdMeet.setGoogleEventId(createdEvent.getId());
            createdMeet.setGoogleCalendarId(calendarToken.getProject().getId().toString());

            meetRepository.save(createdMeet);
        }, () -> {
            throw new CredentialsNotFoundException("Project: " + meet.getProject().getId() + " doesn't have credentials, set credentials for this");
        });

        return meetMapper.toDto(createdMeet);
    }

    @Transactional
    public MeetDto updateMeeting(long id, MeetDto meetDto) {
        Meet existingMeet = meetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        if (existingMeet.getCreatorId() != meetDto.getCreatorId()) {
            throw new RuntimeException("Only the creator can update the meeting");
        }

        Meet updatedMeetEntity = meetMapper.toEntity(meetDto);
        update(existingMeet, updatedMeetEntity);

        Meet updatedMeet = meetRepository.save(existingMeet);

        googleCalendarTokenRepository.findByProjectId(existingMeet.getProject().getId()).ifPresent(calendarToken -> {
            if (existingMeet.getGoogleEventId() != null) {
                CalendarEventDto eventDto = buildEventDto(updatedMeet);
                calendarService.updateEvent(existingMeet.getProject().getId(), calendarId, existingMeet.getGoogleEventId(), eventDto);
            }
        });

        return meetMapper.toDto(updatedMeet);
    }

    @Transactional
    public void deleteMeeting(long id) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        googleCalendarTokenRepository.findByProjectId(meet.getProject().getId()).ifPresent(calendarToken -> {
            if (meet.getGoogleEventId() != null) {
                calendarService.deleteEvent(meet.getProject().getId(), calendarId, meet.getGoogleEventId());
            }
        });

        cancel(meet);
        meetRepository.save(meet);
    }

    @Transactional(readOnly = true)
    public List<MeetDto> getAllMeetings(String title, String date) {
        List<Meet> meets = meetRepository.findAllMeetingsByTitleAndDate(title, date);
        return meetMapper.toDtoList(meets);
    }

    @Transactional(readOnly = true)
    public MeetDto getMeetingById(long id) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        return meetMapper.toDto(meet);
    }

    public void checkAndCompletePastMeetings() {
        List<Meet> pendingMeetings = meetRepository.findAllByStatus(MeetStatus.PENDING);

        for (int i = 0; i < pendingMeetings.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, pendingMeetings.size());
            List<Meet> batch = pendingMeetings.subList(i, end);

            meetCompletionThreadPool.submit(() -> processBatch(batch));
        }
    }

    private void processBatch(List<Meet> batch) {
        for (Meet meet : batch) {
            if (isMeetingInThePast(meet)) {
                meet.setStatus(MeetStatus.COMPLETED);
                meetRepository.save(meet);
            }
        }
    }

    private boolean isMeetingInThePast(Meet meet) {
        LocalDateTime endTime = meet.getCreatedAt().plusHours(1);
        return LocalDateTime.now().isAfter(endTime);
    }

    private CalendarEventDto buildEventDto(Meet meet) {
        return CalendarEventDto.builder()
                .summary(meet.getTitle())
                .description(meet.getDescription())
                .startTime(meet.getCreatedAt())
                .endTime(meet.getCreatedAt().plusHours(1))
                .build();
    }

    private void cancel(Meet meet) {
        meet.setStatus(MeetStatus.CANCELLED);
    }

    private void update(Meet existingMeet, Meet updatedMeet) {
        existingMeet.setTitle(updatedMeet.getTitle());
        existingMeet.setDescription(updatedMeet.getDescription());
        existingMeet.setUpdatedAt(LocalDateTime.now());
    }
}
