package faang.school.projectservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Resource;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    @Mapping(source = "createdBy.userId", target = "createdById")
    @Mapping(source = "updatedBy.userId", target = "updatedById")
    @Mapping(source = "project.id", target = "projectId")
    ResourceDto toDto(Resource resource);
}
