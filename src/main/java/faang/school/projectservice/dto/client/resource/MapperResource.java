package faang.school.projectservice.dto.client.resource;

import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapperResource {

    @Mapping(source = "projectId", target = "project.id")
    Resource toResource(ResourceDto resourceDto);

    @Mapping(source = "project.id", target = "projectId")
    ResourceDto toResourceDto(Resource resource);
}