package faang.school.projectservice.model.mapper.resource;

import faang.school.projectservice.model.dto.resource.ResourceDto;
import faang.school.projectservice.model.entity.Resource;
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
