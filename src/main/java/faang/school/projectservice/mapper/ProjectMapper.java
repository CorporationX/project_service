package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectUpDateDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto projectDto);

    @Mapping(target = "description", source = "projectUpDateDto.description")
    @Mapping(target = "status", source = "projectUpDateDto.status")
    @Mapping(target = "resources", ignore = true)
        // если эту^ хреновину не игнорировать, не будет на данном этапе работать
    Project update(Project projectToUpdate, ProjectUpDateDto projectUpDateDto);
}