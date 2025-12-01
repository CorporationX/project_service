package faang.school.projectservice.mapper.simple;

import faang.school.projectservice.dto.simple.MomentSimpleDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MomentMapper {
    MomentSimpleDto toDto(Moment moment);
    Moment toEntity(MomentSimpleDto momentDto);
}
