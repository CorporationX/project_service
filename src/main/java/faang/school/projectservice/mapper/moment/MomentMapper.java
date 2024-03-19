package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.dto.moment.MomentDto;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MomentMapper {
    MomentDto toDto(Moment moment);
    Moment toEntity(MomentDto momentDto);
}
