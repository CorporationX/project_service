package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.function.Consumer;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {
    ProjectDto toProjectDto(Project project);

    static void updateProjectFields(Project project, ProjectUpdateDto dto) {
        updateIfNotNull(dto.description(), project::setDescription);
        updateIfNotNull(dto.status(), project::setStatus);
        updateIfNotNull(dto.visibility(), project::setVisibility);
    }

    static <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
