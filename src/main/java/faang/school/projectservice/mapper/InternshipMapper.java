package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "mentorId", source = "mentor.id")
    @Mapping(target = "internIds", source = "interns", qualifiedByName = "internsToInternIds")
    InternshipDto toDto(Internship entity);

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "mentor.id", source = "mentorId")
    @Mapping(target = "interns", ignore = true)
    Internship toEntity(InternshipDto dto);

    @Named("internsToInternIds")
    default List<Long> mapToInternIds(List<TeamMember> interns) {
        if (interns == null) {
            return Collections.emptyList();
        }

        return interns.stream().mapToLong(TeamMember::getId).boxed().toList();
    }
}
