package faang.school.projectservice.mapper.resource;

import faang.school.projectservice.model.dto.ResourceDto;
import faang.school.projectservice.model.entity.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    @Mapping(source = "project.id", target = "projectId")
    public ResourceDto mapToResourceDto(Resource resource);

    @Mapping(source = "projectId", target = "project", ignore = true)
    public Resource mapToResource(ResourceDto dto);
}
