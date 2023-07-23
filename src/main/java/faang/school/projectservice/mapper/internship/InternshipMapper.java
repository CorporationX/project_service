package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "projectId", source = "project", qualifiedByName = "mapProjectToId")
    @Mapping(target = "mentorId", source = "mentorId", qualifiedByName = "mapMentorToId")
    @Mapping(target = "interns", source = "interns", qualifiedByName = "mapInternsToIds")
    @Mapping(target = "schedule", source = "schedule", qualifiedByName = "mapScheduleToId")
    InternshipDto toDto(Internship internship);

    InternshipDto toEntity(InternshipDto internshipDto);

    @Named("mapProjectToId")
    default Long mapProjectToId(Project project) {
        return project.getId();
    }

    @Named("mapMentorToId")
    default Long mapMentorToId(TeamMember mentor) {
        return mentor.getId();
    }

    @Named("mapInternsToIds")
    default List<Long> mapInternsToIds(List<TeamMember> interns) {
        return interns.stream().map(TeamMember::getId).toList();
    }

    @Named("mapScheduleToId")
    default Long mapScheduleToId(Schedule schedule) {
        return schedule.getId();
    }
}
