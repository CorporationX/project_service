package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.TeamMapper;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.exception.notFoundException.UserNotFoundException;
import faang.school.projectservice.service.exception.notFoundException.team.TeamNotFoundException;
import faang.school.projectservice.util.validator.TeamServiceValidator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final UserServiceClient client;
    private final TeamMemberMapper teamMemberMapper;
    private final ProjectRepository projectRepository;
    private final TeamServiceValidator validator;
    private final TeamMapper teamMapper;

    public TeamDto createTeamByOwner(TeamDto teamDto) {
        Project projectById = projectRepository.getProjectById(teamDto.getProjectId());

        for (TeamMemberDto teamMember : teamDto.getTeamMembers()) {
            TeamMember member = teamMemberMapper.toEntity(teamMember);
        }
        return null;
    }

    public TeamMemberDto addMembersInTeam(TeamMemberDto dto) {
        isUserExist(dto.getUserId());
        TeamMember teamMember = teamMemberRepository.findById(dto.getUserId());
        validator.validateTeamRole(teamMember);

        TeamMember member = teamMemberMapper.toEntity(dto);
        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        member.setRoles(dto.getRoles());
        member.setTeam(team);

        teamMemberJpaRepository.save(member);
        return teamMemberMapper.toDto(member);
    }

    private void saveTeamMembers(Team team, List<TeamMemberDto> teamMemberDto) {
        teamMemberDto.forEach(member -> {
            Long teamMemberId = teamMemberJpaRepository.create(member.getUserId(), team.getId());
            team.addTeamMember(teamMemberJpaRepository.findById(teamMemberId).orElse(null));
        });
    }

    private void isUserExist(long id) {
        try {
            client.getUser(id);
        } catch (FeignException.FeignClientException exception) {
            throw new UserNotFoundException("This user doesn't exist");
        }
    }
}
