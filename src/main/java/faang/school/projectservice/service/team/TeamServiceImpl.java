package faang.school.projectservice.service.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.team.TeamMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {

    private final TeamMemberJpaRepository teamMemberRepository;
    private final TeamMapper teamMapper;
    private final TeamRepository teamRepository;
    private final ProjectService projectService;

    public TeamDto createTeam(TeamDto teamDto) {

        Team team = teamMapper.toTeam(teamDto);
        team.setProject(projectService.getProjectEntityById(teamDto.getProjectId()));


        return teamMapper.toTeamDto(teamRepository.save(team));
    }


    @Override
    public void deleteMemberByUserId(Long userId) {
        teamMemberRepository.deleteByUserId(userId);
    }

    public Optional<TeamMember> findMemberByUserIdAndProjectId(Long userId, Long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
    }
}
