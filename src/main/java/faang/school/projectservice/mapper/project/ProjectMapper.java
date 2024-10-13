package faang.school.projectservice.mapper.project;


import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.model.entity.Project;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    Project toEntity(ProjectDto projectDto);
    ProjectDto toDto(Project project);
}