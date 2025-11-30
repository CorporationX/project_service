package faang.school.projectservice.mapper.simple;

import faang.school.projectservice.dto.simple.ScheduleSimpleDto;
import faang.school.projectservice.model.Schedule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    ScheduleSimpleDto toDto(Schedule schedule);
    Schedule toEntity(ScheduleSimpleDto scheduleDto);
}
