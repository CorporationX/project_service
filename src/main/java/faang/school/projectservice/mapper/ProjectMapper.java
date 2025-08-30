package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;

/**
 * ProjectMapper — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author fuckmynameagain
 * @since 14.08.2025
 */
@Mapper
public interface ProjectMapper {
    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto dto);
}