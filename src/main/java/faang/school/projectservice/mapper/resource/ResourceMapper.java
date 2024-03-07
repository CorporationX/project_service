package faang.school.projectservice.mapper.resource;

import faang.school.projectservice.dto.client.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    @Mapping(source = "project.id", target = "projectId")
    ResourceDto toDto(Resource resource);

    @Mapping(source = "projectId", target = "project")
    Resource toResource(ResourceDto resourceDto);

    default Project mapIdToProject(Long id) {
        if (id == null) {
            return null;
        }
        return Project.builder()
                .id(id)
                .build();
    }
}
