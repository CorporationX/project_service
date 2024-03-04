package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "interns", target = "internsId", qualifiedByName = "toInternId")
    InternshipDto toDto(Internship internship);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "mentorId", target = "mentorId.id")
    Internship toEntity(InternshipDto internshipDto);
    @Named("toInternId")
    default List<Long> toInternId(List<TeamMember> interns) {
        if (interns == null) {
            return new ArrayList<>();
        }
        return interns.stream()
                .map(TeamMember::getId)
                .toList();
    }
}