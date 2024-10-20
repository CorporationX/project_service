package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.team.CreateTeamDto;
import faang.school.projectservice.model.dto.team.TeamDto;

import java.util.List;

public interface TeamService {
    TeamDto create(CreateTeamDto createTeamDto);
}
