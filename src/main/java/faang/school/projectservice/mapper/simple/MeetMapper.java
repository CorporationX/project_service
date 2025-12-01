package faang.school.projectservice.mapper.simple;

import faang.school.projectservice.dto.simple.MeetSimpleDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MeetMapper {
    MeetSimpleDto toDto(Meet meet);
    Meet toEntity(MeetSimpleDto meetDto);
}
