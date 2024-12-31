package faang.school.projectservice.mapper.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "userIds", ignore = true)
    Meet toEntity(MeetDto meetDto);

    @Mapping(target = "project", source = "project.id")
    MeetDto toDto(Meet meet);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMeetFromDto(UpdateMeetDto dto, @MappingTarget Meet meet);
}
