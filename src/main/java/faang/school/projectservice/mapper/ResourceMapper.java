package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.resource.ResourceReadDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(source = "updatedBy.id", target = "updatedById")
    @Mapping(source = "project.id", target = "projectId")
    ResourceReadDto toDto(Resource resource);
}
