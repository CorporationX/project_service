package faang.school.projectservice.mapper.project.google;

import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.google.EventCalendarDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventCalendarMapper {
    EventCalendarDto toDto(Event event);
}
