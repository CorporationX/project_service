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
    @Mapping(source = "internsId", target = "interns", qualifiedByName = "internIdToIntern")
    Internship toEntity(InternshipToCreateDto dto);

    void update(InternshipToUpdateDto dto, @MappingTarget Internship entity);

    @Named("toInternId")
    default Long toInternId(TeamMember intern) {
        return intern == null ? null : intern.getId();
    }

    @Named("internIdToIntern")
    default TeamMember internIdToIntern(Long internId) {
        if (internId == null) {
            return null;
        }
        TeamMember intern = new TeamMember();
        intern.setId(internId);
        return intern;
    }
}