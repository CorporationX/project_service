package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "interns", target = "internIds", qualifiedByName = "mapInterns")
    @Mapping(source = "schedule.id", target = "scheduleId")
    InternshipDto toDto(Internship entity);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "interns", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    Internship toEntity(InternshipDto dto);

    @Named("mapInterns")
    default List<Long> mapInternsToInternIds(List<TeamMember> interns) {
        if (interns == null) {
            interns = new ArrayList<>();
        }
        return interns.stream()
                .map(TeamMember::getId)
                .toList();
    }
}
