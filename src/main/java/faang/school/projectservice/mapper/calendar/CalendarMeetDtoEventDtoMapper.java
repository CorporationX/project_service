package faang.school.projectservice.mapper.calendar;

import faang.school.projectservice.model.dto.CalendarEventDto;
import faang.school.projectservice.model.enums.CalendarEventStatus;
import faang.school.projectservice.model.dto.MeetDto;
import faang.school.projectservice.model.enums.MeetStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalendarMeetDtoEventDtoMapper {

    @Mapping(source = "calendarEventId", target = "id")
    @Mapping(source = "title", target = "summary")
    @Mapping(source = "status", target = "status", qualifiedByName = "mapMeetStatusToCalendarEventStatus")
    CalendarEventDto toCalendarEventDto(MeetDto meetDto);

    @Named("mapMeetStatusToCalendarEventStatus")
    default CalendarEventStatus mapMeetStatusToCalendarEventStatus(MeetStatus status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case CONFIRMED -> {
                return CalendarEventStatus.CONFIRMED;
            }
            case TENTATIVE -> {
                return CalendarEventStatus.TENTATIVE;
            }
            case CANCELLED -> {
                return CalendarEventStatus.CANCELLED;
            }
            default -> throw new IllegalArgumentException("Неизвестный статус: " + status);
        }
    }
}

