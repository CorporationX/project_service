package faang.school.projectservice.mapper.moment.meeting;

import faang.school.projectservice.dto.meeting.MeetDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MeetMapper {
    Meet toEntity(MeetDto dto);
    MeetDto toDto(Meet entity);
    List<MeetDto> toDtoList(List<Meet> entities);
}


