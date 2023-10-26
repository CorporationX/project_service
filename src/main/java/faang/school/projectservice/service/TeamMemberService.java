package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.exception.enumException.team.TeamRoleEnumException;
import faang.school.projectservice.service.exception.notFoundException.UserNotFoundException;
import faang.school.projectservice.service.exception.notFoundException.team.TeamNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamMemberService {
    private final TeamMemberRepository repository;
    private final TeamRepository teamRepository;
    private final TeamMemberJpaRepository jpaRepository;
    private final UserServiceClient client;
    private final TeamMemberMapper teamMemberMapper;

    public TeamMemberDto addMembersInTeam(TeamMemberDto dto) {
        isUserExist(dto.getUserId());
        TeamMember teamMember = repository.findById(dto.getUserId());
        validateRole(teamMember);

        TeamMember member = teamMemberMapper.toEntity(dto);
        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        member.setRoles(dto.getRoles());
        member.setTeam(team);

        jpaRepository.save(member);
        return teamMemberMapper.toDto(member);
    }

    private void isUserExist(long id) {
        try {
            client.getUser(id);
        } catch (FeignException.FeignClientException exception) {
            throw new UserNotFoundException("This user doesn't exist");
        }
    }

    public void validateRole(TeamMember teamMember) {
        if (!teamMember.getRoles().contains(TeamRole.TEAMLEAD) && !teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new TeamRoleEnumException("...");
        }
    }
}
