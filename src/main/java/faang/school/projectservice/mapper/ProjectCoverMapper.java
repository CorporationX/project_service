package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectCoverMapper {

    ProjectCoverDto toDto(Project project);

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Project toProject(ProjectCoverDto projectDto);
}


