package faang.school.projectservice.service.google_calendar;

import faang.school.projectservice.exceptions.google_calendar.exceptions.NotFoundException;
import faang.school.projectservice.model.EventMapping;
import faang.school.projectservice.repository.EventMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class EventMappingService {
    private final EventMappingRepository eventMappingRepository;

    @Transactional
    public void saveMapping(Long eventId, String googleEventId) {
        EventMapping mapping = createEventMapping(eventId, googleEventId);
        eventMappingRepository.save(mapping);
        log.info("Save mapping eventId '{}' with googleEventId '{}'", eventId, googleEventId);
    }

    @Transactional(readOnly = true)
    public String getGoogleEventIdByEventId(Long eventId) {
        return findMappingByEventId(eventId).getGoogleEventId();
    }

    @Transactional(readOnly = true)
    public Long getEventIdByGoogleEventId(String googleEventId) {
        return findMappingByGoogleEventId(googleEventId).getEventId();
    }

    @Transactional
    public void deleteMapping(Long eventId) {
        if (!eventMappingRepository.existsById(eventId)) {
            throw new NotFoundException("Mapping for eventId '" + eventId + "' not found");
        }
        eventMappingRepository.deleteById(eventId);
        log.info("Delete mapping for eventId '{}'", eventId);
    }

    private EventMapping createEventMapping(Long eventId, String googleEventId) {
        EventMapping mapping = new EventMapping();
        mapping.setEventId(eventId);
        mapping.setGoogleEventId(googleEventId);
        return mapping;
    }

    private EventMapping findMappingByEventId(Long eventId) {
        return eventMappingRepository.findByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("Mapping for eventId '" + eventId + "' not found"));
    }

    private EventMapping findMappingByGoogleEventId(String googleEventId) {
        return eventMappingRepository.findByGoogleEventId(googleEventId)
                .orElseThrow(() -> new NotFoundException("Mapping for googleEventId '" + googleEventId + "' not found"));
    }
}