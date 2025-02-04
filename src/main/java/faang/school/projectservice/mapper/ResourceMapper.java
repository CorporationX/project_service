package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(target = "teamMemberId", source = "createdBy.id")
    @Mapping(target = "projectId", source = "project.id")
    ResourceDto toDto(Resource resource);
}
