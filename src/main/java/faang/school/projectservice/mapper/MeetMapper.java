package faang.school.projectservice.mapper;

import faang.school.projectservice.config.app.AppConfig;
import faang.school.projectservice.model.dto.ZonedDateTimeDto;
import faang.school.projectservice.model.dto.MeetDto;
import faang.school.projectservice.model.entity.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MeetMapper {

    @Autowired
    protected AppConfig appConfig;

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "toZonedDateTimeDto")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "toZonedDateTimeDto")
    public abstract MeetDto toDto(Meet meet);

    public abstract List<MeetDto> toDtoList(List<Meet> meets);

    @Named("toZonedDateTimeDto")
    ZonedDateTimeDto toZonedDateTimeDto(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return new ZonedDateTimeDto(localDateTime, appConfig.getTimeZone());
    }
}


