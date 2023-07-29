package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "interns", source = "interns", qualifiedByName = "internsOfId")
    InternshipDto toDto (Internship internship);

    @Mapping(target = "interns", source = "interns", ignore = true)
    Internship toEntity (InternshipDto internshipDto);

    @Named("internsOfId")
    default List<Long> internsOfId(List<TeamMember> interns) {
        return interns.stream()
                .map(TeamMember::getUserId)
                .toList();
    }
}
