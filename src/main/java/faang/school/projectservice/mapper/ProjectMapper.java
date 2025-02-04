package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectCreateRequestDto;
import faang.school.projectservice.dto.project.ProjectCreateResponseDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateRequestDto;
import faang.school.projectservice.dto.project.ProjectUpdateResponseDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectResponseDto toResponseDto(Project project);

    ProjectCreateResponseDto toCreateResponseDto(Project project);

    ProjectUpdateResponseDto toUpdateResponseDto(Project project);

    Project toProject(ProjectCreateRequestDto projectCreateRequestDto);

    void update(@MappingTarget Project project, ProjectUpdateRequestDto projectUpdateRequestDto);
}
