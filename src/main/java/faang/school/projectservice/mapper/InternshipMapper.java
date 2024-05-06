package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "interns", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    Internship toEntity(InternshipDto internshipDto);

    @Mapping(source = "internship.mentorId.id", target = "mentorId")
    @Mapping(source = "internship.project.id", target = "projectId")
    @Mapping(source = "interns", target = "internIds", qualifiedByName = "internsToIds")
    @Mapping(target = "candidateIds", ignore = true)
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
}
