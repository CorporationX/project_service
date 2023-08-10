package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipUpdateDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
@Mapper (componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface InternshipMapper {

    @Mapping(target = "project", source = "projectId", qualifiedByName = "toProject")
    @Mapping(target = "mentorId", source = "mentorId", qualifiedByName = "toTeamMember")
    @Mapping(target = "interns", source = "internsId", qualifiedByName = "toTeamMemberList")
    @Mapping(target = "schedule", source = "scheduleId", qualifiedByName = "toSchedule")
    Internship toInternship(InternshipDto internshipDto);


    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "mentorId", source = "mentorId.id")
    @Mapping(target = "internsId", source = "interns", qualifiedByName = "teamMemberListToLongList")
    @Mapping(target = "scheduleId", source = "schedule.id")
    InternshipDto toInternshipDto(Internship internship);


    @Mapping(target = "project", source = "projectId", qualifiedByName = "toProject")
    @Mapping(target = "mentorId", source = "mentorId", qualifiedByName = "toTeamMember")
    //@Mapping(target = "interns", source = "internsId", qualifiedByName = "toTeamMemberList")
    @Mapping(target = "schedule", source = "scheduleId", qualifiedByName = "toSchedule")
    void update(InternshipUpdateDto internshipUpdateDto, @MappingTarget Internship internship);

    @Named("toProject")
    default Project toProject(Long projectId){
        return Project.builder().id(projectId).build();
    }

    @Named("toTeamMember")
    default TeamMember toTeamMember(Long mentorId){
        return TeamMember.builder().id(mentorId).build();
    }

    @Named("toTeamMemberList")
    default List<TeamMember> toTeamMemberList(List<Long> interns){
        if (interns==null){
            return new ArrayList<>();
        }
        List<TeamMember> internsList = new ArrayList<>();
        for (Long id : interns){
            internsList.add(TeamMember.builder().id(id).build());
        }
        return internsList;
    }

  @Named("teamMemberListToLongList")
    default List<Long> toInternsIdDto(List<TeamMember> interns){
      if (interns==null){
          return new ArrayList<>();
      }
        return interns.stream().map(TeamMember::getId).toList();
    }

    @Named("toSchedule")
    default Schedule toSchedule(Long scheduleId){
        return Schedule.builder().id(scheduleId).build();
    }
}

