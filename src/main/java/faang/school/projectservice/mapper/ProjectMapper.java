package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectForUpdateDto;
import faang.school.projectservice.dto.project.ProjectForCreationDto;
import faang.school.projectservice.dto.project.ProjectOutputDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    Project toProjectEntity(ProjectForCreationDto projectDto);

    ProjectOutputDto toProjectDto(Project project);

    List<ProjectOutputDto> toProjectDtoList(List<Project> projectList);

    void update(ProjectForUpdateDto projectDto, @MappingTarget Project project);
}
