package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MomentMapper {
    MomentDto toDto(Moment moment);
    Moment toEntity(MomentDto momentDto);
}
