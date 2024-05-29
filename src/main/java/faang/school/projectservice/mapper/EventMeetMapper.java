package faang.school.projectservice.mapper;

import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.service.ProjectService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ProjectService.class}
)
public interface EventMeetMapper {

    @Mapping(source = "summary", target = "name")
    @Mapping(target = "startDateTime", ignore = true)
    @Mapping(target = "endDateTime", ignore = true)
    @Mapping(source = "id", target = "eventGoogleId")
    @Mapping(source = "htmlLink", target = "eventGoogleUrl")
    @Mapping(source = "end.timeZone", target = "timeZone")
    MeetDto eventToMeetDto(Event event);

    @Mapping(source = "name", target = "summary")
    Event meetDtoToEvent(MeetDto meetDto);


    @Mapping(source = "projectId", target = "project")
    Meet toMeetEntity(MeetDto meetDto);

    @Mapping(source = "project.id", target = "projectId")
    MeetDto toDto(Meet meet);

    @Mapping(source = "projectId", target = "project")
    void update(MeetDto meetDto, @MappingTarget Meet foundMeet);

    List<MeetDto> toDtoList(List<Meet> meetings);
}
