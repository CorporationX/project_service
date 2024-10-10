package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.ProjectResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    @Mapping(source = "createdBy.id", target = "createdBy")
    @Mapping(source = "updatedBy.id", target = "updatedBy")
    @Mapping(source = "project.id", target = "projectId")
    ResourceDto toResourceDto(ProjectResource projectResource);

    @Mapping(source = "createdBy", target = "createdBy.id")
    @Mapping(source = "updatedBy", target = "updatedBy.id")
    @Mapping(source = "projectId", target = "project.id")
    ProjectResource toResource(ResourceDto resourceDto);
}
