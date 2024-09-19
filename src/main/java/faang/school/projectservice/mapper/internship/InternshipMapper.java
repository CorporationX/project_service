package faang.school.projectservice.mapper.internship;

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
    @Mapping(source = "interns", target = "internsId", qualifiedByName = "mapToInternsId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "project.id", target = "projectId")
    InternshipDto toDto(Internship internship);

    @Mapping(target = "interns", ignore = true)
    @Mapping(source = "mentorId", target = "mentorId.id")
    @Mapping(source = "projectId", target = "project.id")
    Internship toEntity(InternshipDto internshipDto);

    @Named("mapToInternsId")
    default List<Long> mapToInternsId(List<TeamMember> interns) {
        return interns.stream()
                .map(TeamMember::getId)
                .toList();
    }
}
