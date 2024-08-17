package faang.school.projectservice.mapper.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(source = "updatedBy.id", target = "updatedById")
    @Mapping(source = "createdBy.id", target = "createdById")
    ResourceDto toDto(Resource resource);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Resource toEntity(ResourceDto resourceDto);
}
