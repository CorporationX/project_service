package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    @Mapping(source = "createdBy.id", target = "creatorId")
    @Mapping(source = "updatedBy.id", target = "updaterId")
    @Mapping(source = "project.id", target = "projectId")
    ResourceDto toResourceDto(Resource resource);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "project", ignore = true)
    Resource toResource(ResourceDto resourceDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "allowedRoles", ignore = true)
    void updateOldResourceWithUpdateDataResource(Resource newResource, @MappingTarget Resource oldResource);
}
