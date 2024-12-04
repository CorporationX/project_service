package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.project.ProjectStatus;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "status", expression = "java(mapProjectStatus())")
    Project toEntity(CreateSubProjectDto createSubProjectDto);

    ProjectDto toProjectDto(Project project);

    List<ProjectDto> toProjectDto(List<Project> projects);

    ResponseProjectDto toResponseDto(Project project);

    Project toEntity(ResponseProjectDto responseProjectDto);

    List<ResponseProjectDto> toResponseDto(List<Project> projects);

    List<Project> toEntity(List<ResponseProjectDto> responseProjectDtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UpdateSubProjectDto updateSubProjectDto, @MappingTarget Project project);

    default ProjectStatus mapProjectStatus() {
        return ProjectStatus.CREATED;
    }
}
