package faang.school.projectservice.mapper.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(source = "projectId", target = "project.id")
    Resource toEntity(ResourceDto resourceDto);

    @Mapping(source = "project.id", target = "projectId")
    ResourceDto toDto(Resource resource);
}
