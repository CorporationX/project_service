package faang.school.projectservice.service;

import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.dto.filter.MeetFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.MeetFilter;
import faang.school.projectservice.jpa.MeetJpaRepository;
import faang.school.projectservice.mapper.EventMeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.google.GoogleCalendarService;
import faang.school.projectservice.validator.MeetValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetJpaRepository meetJpaRepository;
    private final MeetRepository meetRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final GoogleCalendarService googleCalendarService;
    private final EventMeetMapper eventMeetMapper;
    private final MeetValidator meetValidator;
    private final List<MeetFilter> meetFilters;

    @Transactional
    public MeetDto create(MeetDto meetDto) {
        meetValidator.validateExistsMeetCreator(meetDto);
        Meet newMeet = eventMeetMapper.toMeetEntity(meetDto);
        Event event = googleCalendarService.createEvent(meetDto);

        newMeet.setEventGoogleId(event.getId());
        newMeet.setEventGoogleUrl(event.getHtmlLink());
        Meet savedMeet = meetJpaRepository.save(newMeet);
        return eventMeetMapper.toDto(savedMeet);
    }

    @Transactional
    public MeetDto update(MeetDto meetDto, long id) {
        Meet foundMeet = meetRepository.findById(id);
        validateMeetCreator(meetDto.getCreatedBy(), foundMeet.getCreatedBy());
        if (meetDto.getMeetStatus().equals(MeetStatus.CANCELLED)) {
            googleCalendarService.deleteEvent(meetDto.getEventGoogleId());
        } else {
            googleCalendarService.updateEvent(meetDto);
        }
        eventMeetMapper.update(meetDto, foundMeet);
        foundMeet.setId(id);
        return eventMeetMapper.toDto(meetJpaRepository.save(foundMeet));
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Meet meet = meetRepository.findById(id);
        TeamMember updater = teamMemberRepository.findByUserIdAndProjectId(userId, meet.getProject().getId());
        validateMeetCreator(updater.getId(), meet.getCreatedBy());

        log.info("Delete meeting with id {}", id);
        googleCalendarService.deleteEvent(meet.getEventGoogleId());
        meetJpaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<MeetDto> findAllByProjectId(Long id) {
        return eventMeetMapper.toDtoList(meetJpaRepository.findAllByProjectId(id));
    }

    @Transactional(readOnly = true)
    public MeetDto findById(Long id) {
        return eventMeetMapper.toDto(meetRepository.findById(id));
    }

    @Transactional(readOnly = true)
    public List<MeetDto> findAllByProjectIdAndFilter(Long id, MeetFilterDto filters) {
        Supplier<Stream<Meet>> meetings = () -> meetJpaRepository.findAllByProjectId(id).stream();
        List<Meet> filteredMeetings = meetFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(meetings, filters))
                .distinct()
                .toList();
        return eventMeetMapper.toDtoList(filteredMeetings);
    }

    public void createAcl(String userEmail) {
        googleCalendarService.createAcl(userEmail);
    }

    public void deleteAcl(String userEmail) {
        googleCalendarService.deleteAcl(userEmail);
    }

    public String getAcl(String userEmail) {
        return googleCalendarService.getAclById(userEmail);
    }

    private void validateMeetCreator(Long updaterId, Long creatorId) {
        if (!Objects.equals(updaterId, creatorId)) {
            throw new DataValidationException(
                    String.format("Team member not creator for this meet. Creator ID: %d, Updater ID: %d",
                            creatorId, updaterId));
        }
    }
}
