package faang.school.projectservice.mapper.meet;

import faang.school.projectservice.dto.meet.MeetCreateDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.MeetUpdateDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {

    @Mapping(target = "project.id", source = "projectId")
    Meet toEntity(MeetCreateDto meetCreateDto);

    @Mapping(target = "project.id", source = "meetCreateDto.projectId")
    @Mapping(target = "creatorId", source = "userId")
    Meet toEntity(MeetCreateDto meetCreateDto, long userId);

    @Mapping(target = "projectId", source = "project.id")
    MeetResponseDto toDto(Meet meet);

    List<MeetResponseDto> toDto(List<Meet> meets);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project.id", source = "projectId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMeetFromDto(MeetUpdateDto dto, @MappingTarget Meet meet);

}
