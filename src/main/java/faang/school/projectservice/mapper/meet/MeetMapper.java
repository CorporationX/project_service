package faang.school.projectservice.mapper.meet;

import faang.school.projectservice.dto.meet.MeetCreateDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.MeetUpdateDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {

    Meet toEntity(MeetCreateDto meetCreateDto);

    @Mapping(target = "projectId", source = "project.id")
    MeetResponseDto toDto(Meet meet);

    List<MeetResponseDto> toDto(List<Meet> meets);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project.id", source = "projectId")
    void updateMeetFromDto(MeetUpdateDto dto, @MappingTarget Meet meet);

}
