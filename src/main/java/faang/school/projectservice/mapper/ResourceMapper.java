package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(source = "createdBy.userId", target = "createdById")
    @Mapping(source = "updatedBy.userId", target = "updatedById")
    @Mapping(source = "project.id", target = "projectId")
    ResourceResponseDto toDto(Resource resource);

    List<ResourceResponseDto> toListDto(List<Resource> resources);
}


