package faang.school.projectservice.mapper.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {

    Team toEntity(TeamDto teamDto);

    @Mapping(target = "projectId", source = "project.id")
    TeamDto toDto(Team team);
}
