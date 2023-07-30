package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InternshipMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "mentorId", source = "mentorId.id")
    @Mapping(target = "internIds", source = "interns", qualifiedByName = "extractInternIds")
    @Mapping(target = "status", source = "status")
    InternshipDto toDto(Internship internship);

    List<InternshipDto> toListDto(List<Internship> internships);

    @Named("extractInternIds")
    default List<Long> extractInternIds(List<TeamMember> interns) {
        return interns.stream()
            .map(TeamMember::getId)
            .collect(Collectors.toList());
    }
}
