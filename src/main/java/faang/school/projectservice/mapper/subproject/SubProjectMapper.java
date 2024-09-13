package faang.school.projectservice.mapper.subproject;

import faang.school.projectservice.dto.client.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.client.subproject.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {

    CreateSubProjectDto mapToSubDto(ProjectDto projectDto);

    Project mapToEntity(CreateSubProjectDto createSubProjectDto);

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    ProjectDto mapToProjectDto(Project project);
}