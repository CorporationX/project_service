package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "interns", source = "interns", qualifiedByName = "listOfId")
    @Mapping(target = "projectId", source = "project", qualifiedByName = "mapProjectToId")
    @Mapping(target = "mentorId", source = "mentorId", qualifiedByName = "mapMentorToId")
    InternshipDto toDto(Internship internship);

    @Mapping(target = "interns", source = "interns", ignore = true)
    @Mapping(target = "mentorId", source = "mentorId", ignore = true)
    Internship toEntity(InternshipDto internshipDto);

    @Named("listOfId")
    default List<Long> listOfId(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getUserId)
                .toList();
    }

    @Named("mapMentorToId")
    default Long mapMentorToId(TeamMember mentor) {
        return mentor.getId();
    }

    @Named("mapProjectToId")
    default Long mapProjectToId(Project project) {
        return project.getId();
    }
}