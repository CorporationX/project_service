package faang.school.projectservice.mapper.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.request.CreationRequest;
import faang.school.projectservice.dto.subproject.request.UpdatingRequest;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE
        , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    SubProjectDto toSubProjectDto(Project project);

    @Mapping(source = "parentProjectId", target = "parentProject.id")
    Project toProject(SubProjectDto subProjectDto);

    @Mapping(source = "parentProjectId", target = "parentProject.id")
    Project toProjectFromCreationRequest(CreationRequest creationRequest);

    @Mapping(target = "id", ignore = true)
    void updateProjectFromUpdateRequest(UpdatingRequest updatingRequest, @MappingTarget Project project);
}