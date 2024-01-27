package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "interns", target = "interns", qualifiedByName ="getIdInterns")
    InternshipDto toInternshipDto (Internship internship);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "interns", ignore = true)
    Internship toEntity(InternshipDto internshipDto);

    @Named("getIdInterns")
    default List<Long> getIdInterns(List<TeamMember> interns){
        return interns.stream()
                .map(TeamMember::getId)
                .toList();
    }
}
