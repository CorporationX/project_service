package faang.school.projectservice.mapper;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "String", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    Moment ProjectToMoment(Project project);
}
