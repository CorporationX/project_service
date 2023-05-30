package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ScheduleDto;
import faang.school.projectservice.model.Schedule;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduleMapper {
    ScheduleDto toDto(Schedule entity);
    Schedule toEntity(ScheduleDto dto);
}
