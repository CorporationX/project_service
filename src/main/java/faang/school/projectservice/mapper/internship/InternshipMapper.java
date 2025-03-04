package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = InternshipMapperHelper.class)
public interface InternshipMapper {

    @Mapping(target = "project", source = "projectId", qualifiedByName = "mapProjectIdToProject")
    @Mapping(target = "mentor", source = "mentorId", qualifiedByName = "mapMentorIdToMentor")
    @Mapping(target = "interns", source = "internIds", qualifiedByName = "mapInternIdsToTeamMembers")
    @Mapping(target = "schedule", source = "scheduleId", qualifiedByName = "mapScheduleIdToSchedule")
    @Mapping(target = "startDate")
    @Mapping(target = "endDate")
    @Mapping(target = "status")
    @Mapping(target = "description")
    @Mapping(target = "name")
    Internship toEntity(InternshipDto internshipDto);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "mentorId", source = "mentor.id")
    @Mapping(target = "internIds", source = "interns", qualifiedByName = "mapTeamMembersToInternIds")
    @Mapping(target = "scheduleId", source = "schedule.id")
    @Mapping(target = "status")
    @Mapping(target = "description")
    @Mapping(target = "name")
    @Mapping(target = "startDate")
    @Mapping(target = "endDate")
    InternshipDto toDto(Internship internship);
}