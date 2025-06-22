package faang.school.projectservice.mapper.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    @Mapping(source = "project.id", target = "projectId")
    TeamDto toDto(Team team);

    default Team toEntityCustom(TeamDto teamDto, ProjectRepository repository) {
        return Team.builder()
                .id(teamDto.getId())
                .project(repository.getReferenceById(teamDto.getProjectId()))
                .teamMembers(List.of())
                .avatarKey(teamDto.getAvatarKey())
                .build();
    }
}