package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Meet toEntity(MeetDto meetDto);

    @Mapping(target = "projectId", source = "project.id")
    MeetDto toDto(Meet meet);

    List<MeetDto> toDtoList(List<Meet> meets);
}