package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {

    @Mapping(target = "projectId", source = "project.id")
    MeetResponseDto toResponseDto(Meet meet);

    Meet fromCreateDto(CreateMeetDto createMeetDto);

    void update(@MappingTarget Meet meet, UpdateMeetDto updateMeetDto);
}
