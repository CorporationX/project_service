package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "String", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {
    MomentDto toDto(Moment moment);
    Moment ProjectToMoment(Project project);
}
