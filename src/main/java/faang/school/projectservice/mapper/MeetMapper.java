package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeetMapper {
    @Mapping(source = "project.id", target = "projectId")
    MeetDto toDto(Meet meet);

    @Mapping(source = "projectId", target = "project.id")
    Meet toEntity(MeetDto meetDto);
}
