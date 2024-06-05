
package faang.school.projectservice.mapper;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.dto.project.calendar.EventDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    int MS_IN_S = 1000; //milliseconds in second

    @Mapping(source = "startTime", target = "start", qualifiedByName = "eventDateMapping")
    @Mapping(source = "endTime", target = "end", qualifiedByName = "eventDateMapping")
    Event toModel(EventDto event);

    @Mapping(source = "start", target = "startTime", qualifiedByName = "timeMapping")
    @Mapping(source = "end", target = "endTime", qualifiedByName = "timeMapping")
    EventDto toDto(Event event);

    @Named("eventDateMapping")
    default EventDateTime eventDateMapping(LocalDateTime date) {
        DateTime dateTime = new DateTime(date.toEpochSecond(ZoneOffset.UTC) * MS_IN_S);
        return new EventDateTime().setDateTime(dateTime);
    }

    @Named("timeMapping")
    default LocalDateTime timeMapping(EventDateTime date) {
        return LocalDateTime.ofEpochSecond(date.getDateTime().getValue() / MS_IN_S, 0, ZoneOffset.UTC);
    }

    List<EventDto> toDtos(List<Event> eventsOfProject);
}
