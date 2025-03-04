package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
interface InternshipMapperHelper {
    @Named("mapProjectIdToProject")
    default Project mapProjectIdToProject(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return Project.builder().id(projectId).build();
    }

    @Named("mapMentorIdToMentor")
    default TeamMember mapMentorIdToMentor(Long mentorId) {
        if (mentorId == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return TeamMember.builder().id(mentorId).build();
    }

    @Named("mapInternIdsToTeamMembers")
    default List<TeamMember> mapInternIdsToTeamMembers(List<Long> internIds) {
        if (internIds == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return internIds.stream().map(id -> TeamMember.builder().id(id).build()).collect(Collectors.toList());
    }

    @Named("mapScheduleIdToSchedule")
    default Schedule mapScheduleIdToSchedule(Long scheduleId) {
        if (scheduleId == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return Schedule.builder().id(scheduleId).build();
    }

    @Named("mapTeamMembersToInternIds")
    default List<Long> mapTeamMembersToInternIds(List<TeamMember> teamMembers) {
        if (teamMembers == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return teamMembers.stream().map(TeamMember::getId).collect(Collectors.toList());
    }
}
