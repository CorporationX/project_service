package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.ScheduleService;
import faang.school.projectservice.service.TeamMemberService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ScheduleService.class, TeamMemberService.class, ProjectService.class}
)
public interface InternshipMapper {

    @Mapping(source = "projectId", target = "project")
    @Mapping(target = "interns", ignore = true)
    @Mapping(source = "scheduleId", target = "schedule")
    Internship toEntity(InternshipDto internshipDto);

    @Mapping(source = "internship.mentorId.id", target = "mentorId")
    @Mapping(source = "internship.project.id", target = "projectId")
    @Mapping(source = "interns", target = "internIds", qualifiedByName = "internsToIds")
    @Mapping(target = "candidateIds", ignore = true)
    @Mapping(source = "schedule.id", target = "scheduleId")
    InternshipDto toDto(Internship internship);

    List<InternshipDto> toListDto(List<Internship> internships);

    @Named("internsToIds")
    default List<Long> internsToIds(List<TeamMember> interns) {
        List<Long> resultList = new ArrayList<>();
        if (interns != null) {
            resultList = interns.stream().map(TeamMember::getId).toList();
        }
        return resultList;
    }

    Project map(Long projectId);
}
