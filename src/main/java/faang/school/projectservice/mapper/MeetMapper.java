package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {
    Meet toEntity(MeetDto dto);

    MeetDto toDto(Meet entity);

    List<MeetDto> toDtoList(List<Meet> entities);
}

