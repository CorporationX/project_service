package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "allowedRoles", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Resource toEntity(ResourceDto dto);

    @Mapping(source = "project.id", target = "projectId")
    ResourceDto toDto(Resource resource);
}
