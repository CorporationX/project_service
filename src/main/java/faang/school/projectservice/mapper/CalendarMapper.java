package faang.school.projectservice.mapper;

import com.google.api.services.calendar.model.Calendar;
import faang.school.projectservice.dto.calendar.CalendarDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalendarMapper {

    Calendar toEntity(CalendarDto calendarDto);

    CalendarDto toDto(Calendar calendar);
}