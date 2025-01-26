package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectCreateRequestDto;
import faang.school.projectservice.dto.ProjectResponseDto;
import faang.school.projectservice.dto.ProjectUpdateRequestDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    Project toProjectEntity(ProjectCreateRequestDto dto);

    ProjectResponseDto toProjectResponseDto(Project entity);

    List<ProjectResponseDto> toProjectResponseDtos(List<Project> entities);

    void update(ProjectUpdateRequestDto projectUpdateRequestDto, @MappingTarget Project project);
}
