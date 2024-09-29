package faang.school.projectservice.mapper.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "updatedBy.id", target = "updatedById")
    @Mapping(source = "project.id", target = "projectId")
    ResourceDto toDto(Resource resource);

    @Mapping(source = "createdById", target = "createdBy.id")
    @Mapping(source = "updatedById", target = "updatedBy.id")
    @Mapping(source = "projectId", target = "project.id")
    Resource toEntity(ResourceDto dto);

    List<ResourceDto> toDTOList(List<Resource> resources);

    List<Resource> toEntityList(List<ResourceDto> dtos);
}