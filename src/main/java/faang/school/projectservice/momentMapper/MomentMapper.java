package faang.school.projectservice.momentMapper;

import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MomentMapper {
    MomentMapper toMomentDto(Moment moment);
}
