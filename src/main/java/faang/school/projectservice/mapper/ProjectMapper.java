package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectFilterDto;
import faang.school.projectservice.dto.client.project.ProjectViewDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Project toEntity(ProjectCreateDto projectDto);

    void update(ProjectUpdateDto projectDto, @MappingTarget Project entity);

    ProjectViewDto toViewDto(Project project);

    Project toEntity(ProjectViewDto projectViewDto);

}