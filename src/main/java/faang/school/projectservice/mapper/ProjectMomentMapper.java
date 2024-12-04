package faang.school.projectservice.mapper;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.project.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMomentMapper {

    @Mapping(target = "date", source = "updatedAt")
    Moment toMoment(Project project);
}
