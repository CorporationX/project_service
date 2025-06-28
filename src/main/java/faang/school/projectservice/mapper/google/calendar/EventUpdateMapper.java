package faang.school.projectservice.mapper.google.calendar;

import faang.school.projectservice.dto.google.calendar.GoogleCalendarEventDto;
import faang.school.projectservice.model.google.calendar.GoogleCalendarEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventUpdateMapper {
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    void updateEntityFromDto(GoogleCalendarEventDto dto, @MappingTarget GoogleCalendarEvent event);
}
