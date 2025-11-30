package faang.school.projectservice.mapper.simple;

import faang.school.projectservice.dto.simple.ResourceSimpleDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResourceSimpleMapper {
    ResourceSimpleDto toDto(Resource resource);
    Resource toEntity(ResourceSimpleDto resourceDto);
}
