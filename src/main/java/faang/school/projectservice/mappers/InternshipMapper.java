package faang.school.projectservice.mappers;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InternshipMapper {

    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(target = "internIds", source = "interns")
    @Mapping(target = "projectId", source = "project.id")
    InternshipDto toDto(Internship internship);

    @Mapping(source = "mentorId", target = "mentorId.id")
    @Mapping(target = "project", source = "projectId")
    @Mapping(target = "interns", source = "internIds")
    Internship toEntity(InternshipDto internshipDto);

    default Long mapToLong(TeamMember mentor) {
        return mentor != null ? mentor.getId() : null;
    }

    default TeamMember mapToTeamMember(Long mentorId) {
        if (mentorId == null) {
            return null;
        }
        TeamMember mentor = new TeamMember();
        mentor.setId(mentorId);
        return mentor;
    }

    default Project mapToProject(Long projectId) {
        if (projectId == null) {
            return null;
        }
        Project project = new Project();
        project.setId(projectId);
        return project;
    }
}


