package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.gallery.AddImageResponseDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(target = "createdByTeamMemberId", source = "createdBy.id")
    @Mapping(target = "updatedByTeamMemberId", source = "updatedBy.id")
    @Mapping(target = "projectId", source = "project.id")
    AddImageResponseDto toAddDto(Resource resource);
}
