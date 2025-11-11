package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.math.BigInteger;
import java.util.function.Consumer;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    ProjectDto toProjectDto(Project project);

    static Project toEntity(ProjectCreateDto projectCreateDto, Long userId) {
        return Project.builder()
                .name(projectCreateDto.name())
                .description(projectCreateDto.description())
                .status(ProjectStatus.CREATED)
                .ownerId(userId)
                .visibility(projectCreateDto.visibility())
                .storageSize(BigInteger.valueOf(0))
                .maxStorageSize(BigInteger.valueOf(10485760))
                .build();
    }

    static void updateProjectFields(Project project, ProjectUpdateDto dto) {
        updateIfNotNull(dto.name(), project::setName);
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
