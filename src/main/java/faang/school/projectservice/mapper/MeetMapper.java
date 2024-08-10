package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.model.meet.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MeetMapper {

    @Mapping(source = "team.id", target = "teamId")
    @Mapping(source = "project.id", target = "projectId")
    MeetDto toDto(Meet meet);

    Meet toEntity(MeetDto meetDto);

    List<MeetDto> toDtoList(List<Meet> meets);


}
