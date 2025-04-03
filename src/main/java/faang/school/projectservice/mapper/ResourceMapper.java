package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(source = "createdBy.nickname", target = "createdBy")
    ResourceDto toDto(Resource resource);

    @InheritInverseConfiguration
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "allowedRoles", ignore = true)
    Resource toEntity(ResourceDto dto);
}
