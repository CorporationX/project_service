package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeetMapper {

    @Mapping(target = "project", ignore = true)
    Meet toMeet(CreateMeetDto createMeetDto);

    @Mapping(source = "project.id", target = "projectId")
    MeetDto toMeetDto(Meet meet);

    @Mapping(target = "project", ignore = true)
    void update(UpdateMeetDto dto, @MappingTarget Meet entity);
}