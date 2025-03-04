package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {

    @Mapping(source = "project.id", target = "projectId")
    MeetDto toDto(Meet meetEntity);

    @Mapping(source = "projectId", target = "project.id")
    Meet toEntity(MeetDto meetDto);
}
