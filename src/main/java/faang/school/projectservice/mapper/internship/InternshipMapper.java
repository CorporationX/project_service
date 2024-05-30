package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipToCreateDto;
import faang.school.projectservice.dto.internship.InternshipToUpdateDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "mentorId", source = "mentor.id")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "scheduleId", source = "schedule.id")
    @Mapping(source = "interns", target = "internsId", qualifiedByName = "toInternId")
    InternshipDto toDto(Internship entity);

    @Mapping(target = "mentor.id", source = "mentorId")
    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "schedule.id", source = "scheduleId")
    @Mapping(source = "internsId", target = "interns", qualifiedByName = "internIdToInterns")
    Internship toEntity(InternshipToCreateDto dto);

    void update(InternshipToUpdateDto dto, @MappingTarget Internship entity);

    @Named("toInternId")
    default List<Long> toInternId(List<TeamMember> interns) {
        return interns == null ? List.of() : interns.stream()
                .map(TeamMember::getId)
                .distinct()
                .collect(Collectors.toList());
    }

    @Named("internIdToInterns")
    default List<TeamMember> internIdToInterns(List<Long> internsId) {
        return internsId == null ? List.of() : internsId.stream()
                .map(id -> {
                    TeamMember intern = new TeamMember();
                    intern.setId(id);
                    return intern;
                })
                .distinct()
                .collect(Collectors.toList());
    }
}