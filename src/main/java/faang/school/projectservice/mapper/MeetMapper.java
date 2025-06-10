package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.meeting.MeetDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MeetMapper {
    @Mapping(target = "id", ignore = true)
    Meet toEntity(MeetDto dto);
    MeetDto toDto(Meet entity);
    List<MeetDto> toDtoList(List<Meet> entities);
}


