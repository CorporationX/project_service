package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {
    @Mapping(target = "parentProjectId", source = "parentProject.id")
    ProjectDto toDto(Project project);

    List<ProjectDto> toDtoList(List<Project> projects);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storageSize", ignore = true)
    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "teams", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "vacancies", ignore = true)
    @Mapping(target = "moments", ignore = true)
    @Mapping(target = "meets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "presentationFileKey", ignore = true)
    @Mapping(target = "presentationGeneratedAt", ignore = true)
    @Mapping(target = "galleryFileKeys", ignore = true)
    Project toModel(ProjectCreateDto projectDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storageSize", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "presentationFileKey", ignore = true)
    @Mapping(target = "presentationGeneratedAt", ignore = true)
    @Mapping(target = "galleryFileKeys", ignore = true)
    void updateModel(ProjectUpdateDto projectUpdateDto, @MappingTarget Project project);
}
