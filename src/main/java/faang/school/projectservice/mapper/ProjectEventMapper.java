package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectEvent;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProjectEventMapper {
    @Mapping(target = "authorId", source = "ownerId")
    @Mapping(target = "projectId", source = "id")
   ProjectEvent toEntity(Project project);
}
