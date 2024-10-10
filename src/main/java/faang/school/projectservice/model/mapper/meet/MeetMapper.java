package faang.school.projectservice.model.mapper.meet;

import faang.school.projectservice.model.dto.meet.MeetRequestDto;
import faang.school.projectservice.model.dto.meet.MeetResponseDto;
import faang.school.projectservice.model.entity.meet.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {

    @Mapping(target = "project.id", source = "projectId")
    Meet toEntity(MeetRequestDto dto);

    @Mapping(target = "projectId", source = "project.id")
    MeetResponseDto toDto(Meet entity);

    void update(@MappingTarget Meet meet, MeetRequestDto dto);
}