package faang.school.projectservice.mapper.meet;

import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Collection;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {
    @Mapping(target = "projectId", source = "project.id")
    MeetDto toDto(Meet meet);

    Collection<MeetDto> toDtos(Collection<Meet> meets);

    Meet toEntity(CreateMeetDto dto);
    Meet toEntity(UpdateMeetDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Meet updateEntity(UpdateMeetDto updateMeetDto, @MappingTarget Meet meet);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Meet updateEntity(Meet sourceMeet, @MappingTarget Meet targetMeet);
}
