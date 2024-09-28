package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.google.calendar.CalendarEventDto;
import faang.school.projectservice.dto.meet.MeetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalendarEventMapper {

    @Mapping(source = "googleEventId", target = "id")
    CalendarEventDto toCalendarEventDto(MeetDto meetDto);
}

