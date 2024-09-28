package faang.school.projectservice.service.google_calendar;

import faang.school.projectservice.exceptions.google_calendar.exceptions.NotFoundException;
import faang.school.projectservice.model.EventMapping;
import faang.school.projectservice.repository.EventMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventMappingService {
    private final EventMappingRepository eventMappingRepository;

    public void saveMapping(Long eventId, String googleEventId) {
        log.info("Сохранение маппинга eventId '{}' с googleEventId '{}'", eventId, googleEventId);

        EventMapping mapping = createEventMapping(eventId, googleEventId);
        eventMappingRepository.save(mapping);

        log.info("Маппинг сохранен");
    }

    public String getGoogleEventIdByEventId(Long eventId) {
        log.info("Получение googleEventId по eventId '{}'", eventId);

        return findMappingByEventId(eventId).getGoogleEventId();
    }

    public Long getEventIdByGoogleEventId(String googleEventId) {
        log.info("Получение eventId по googleEventId '{}'", googleEventId);

        return findMappingByGoogleEventId(googleEventId).getEventId();
    }

    public void deleteMapping(Long eventId) {
        log.info("Удаление маппинга для eventId '{}'", eventId);

        if (eventMappingRepository.existsById(eventId)) {
            throw new NotFoundException("Маппинг для eventId '" + eventId + "' не найден");
        }
        eventMappingRepository.deleteById(eventId);
        log.info("Маппинг удален");
    }

    private EventMapping createEventMapping(Long eventId, String googleEventId) {
        EventMapping mapping = new EventMapping();
        mapping.setEventId(eventId);
        mapping.setGoogleEventId(googleEventId);
        return mapping;
    }

    private EventMapping findMappingByEventId(Long eventId) {
        return eventMappingRepository.findByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("Маппинг для eventId '" + eventId + "' не найден"));
    }

    private EventMapping findMappingByGoogleEventId(String googleEventId) {
        return eventMappingRepository.findByGoogleEventId(googleEventId)
                .orElseThrow(() -> new NotFoundException("Маппинг для googleEventId '" + googleEventId + "' не найден"));
    }
}