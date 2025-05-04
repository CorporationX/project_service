package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.dto.resource.ResourceReadDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import faang.school.projectservice.dto.resource.ResourceFileDto;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(source = "project.id", target = "projectId")
    ResourceFileDto toDto(Resource resource);

    @Mapping(source = "projectId", target = "project.id")
    Resource toEntity(ResourceFileDto dto);

    @Mapping(source = "project.id", target = "projectId")
    ResourceReadDto todo (Resource resource);

    @Mapping(source = "createdBy.id", target = "createdBy")
    @Mapping(source = "updatedBy.id", target = "updatedBy")
    ResourceDto toResource(Resource resource);
}
