package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(target = "project", ignore = true)
    Resource toEntity(ResourceDto dto);

    @Mapping(target = "projectId", source = "project.id")
    ResourceDto toDto(Resource entity);
}
