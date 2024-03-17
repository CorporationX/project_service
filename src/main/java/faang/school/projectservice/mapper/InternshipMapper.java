package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "interns", target = "internsIds", qualifiedByName = "map")
    InternshipDto toDto (Internship internship);

    List<InternshipDto> toDto (List<Internship> internships);

    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "project", ignore = true)//or we go to repository and bring the data
    @Mapping(target = "interns", ignore = true)
    Internship toEntity (InternshipDto internshipDto);

    @Named("map")
    default List<Long> map (List<TeamMember> members){
        return members.stream()
                .map(TeamMember::getId)
                .toList();
    }
}
