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

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "mentorId", source = "mentorId.id")
    @Mapping(target = "interns", source = "interns", qualifiedByName = "mapInternsToIds")
    @Mapping(target = "schedule", source = "schedule", qualifiedByName = "mapScheduleToId")
    InternshipDto toDto(Internship internship);

    @Mapping(target = "mentorId", source = "mentorId", ignore = true)
    @Mapping(target = "interns", source = "interns", qualifiedByName = "idsToInterns")
    @Mapping(target = "schedule", source = "schedule", qualifiedByName = "idToSchedule")
    Internship toEntity(InternshipDto internshipDto);

    @Named("mapInternsToIds")
    default List<Long> mapInternsToIds(List<TeamMember> interns) {
        return interns.stream().map(TeamMember::getUserId).toList();
    }

    @Named("mapScheduleToId")
    default Long mapScheduleToId(Schedule schedule) {
        return schedule.getId();
    }

    @Named("idsToInterns")
    default List<TeamMember> idsToInterns(List<Long> ids) {
        return ids.stream().map(id -> TeamMember.builder().id(id).build()).toList();
    }

    @Named("idToSchedule")
    default Schedule idToSchedule(Long id) {
        Schedule res = new Schedule();
        res.setId(id);
        return res;
    }
}
