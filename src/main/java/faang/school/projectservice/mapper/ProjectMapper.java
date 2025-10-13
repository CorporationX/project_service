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
    ProjectDto toDto(Project project);
    List<ProjectDto> toDtoList(List<Project> projects);

    Project toModel(ProjectCreateDto projectDto);
    List<Project> toModelList(List<ProjectCreateDto> projectDtos);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storageSize", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "parentProjectId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    @Mapping(target = "presentationFileKey", ignore = true)
    @Mapping(target = "presentationGeneratedAt", ignore = true)
    @Mapping(target = "galleryFileKeys", ignore = true)
    void updateModel(ProjectUpdateDto projectUpdateDto, @MappingTarget Project project);
}
