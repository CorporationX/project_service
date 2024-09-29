package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.resource.ResourceDto;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    ResourceDto toResourceDto(Resource resource);

    Resource toResource(ResourceDto resourceDto);
}
