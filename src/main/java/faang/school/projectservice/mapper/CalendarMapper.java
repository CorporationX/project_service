package faang.school.projectservice.mapper;

import com.google.api.services.calendar.model.Calendar;
import faang.school.projectservice.dto.calendar.CalendarDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalendarMapper {
    Calendar toModel(CalendarDto calendarDto);

    CalendarDto toDto(Calendar calendar);

    List<CalendarDto> toDtos(List<Calendar> calendars);
}
