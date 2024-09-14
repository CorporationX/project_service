package faang.school.projectservice.mapper.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    SubProjectDto toSubProjectDto(Project project);

    @Mapping(source = "parentProjectId", target = "parentProject.id")
    Project toProject(SubProjectDto subProjectDto);
}