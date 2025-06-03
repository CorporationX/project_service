package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    Resource toEntity(ResourceDto resourceDto);

    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "updatedById", source = "updatedBy.id")
    @Mapping(target = "projectId", source = "project.id")
    ResourceDto toDto(Resource resource);
}
