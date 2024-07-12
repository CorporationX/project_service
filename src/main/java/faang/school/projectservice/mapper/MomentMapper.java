package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = ProjectMapper.class)
public interface MomentMapper {

    MomentMapper INSTANCE = Mappers.getMapper(MomentMapper.class);

    MomentDto toDto(Moment moment);

    @Mapping(target = "resource", ignore = true)
    @Mapping(target = "imageId", ignore = true)
    Moment toEntity(MomentDto momentDto);
}
