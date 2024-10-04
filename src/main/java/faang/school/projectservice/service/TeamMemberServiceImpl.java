package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.dto.team.TeamMemberCreateDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.dto.team.TeamMemberUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.teamfilter.TeamMemberFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final UserServiceClient userServiceClient;
    private final TeamMemberMapper teamMemberMapper;
    private final UserContext userContext;
    private final TeamRepository teamRepository;
    private final List<TeamMemberFilter> teamMemberFilters;

    @Override
    public TeamMemberDto addTeamMember(long teamId, TeamMemberCreateDto teamMemberCreateDto) {
        log.info("Attempting to add a team member with teamId: {} and teamMemberDto: {}", teamId, teamMemberCreateDto);
        long userId = userContext.getUserId();
        checkIfUserExists(userId);
        Team team = teamRepository.findById(teamId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team doesn't exist by id: %s", teamId)));
        long projectId = team.getProject().getId();
        TeamMember user = teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);
        if (!user.getRoles().contains(TeamRole.TEAMLEAD) &&
                !user.getRoles().contains(TeamRole.OWNER)) {
            throw new DataValidationException(String.format
                    ("User with id %d doesn't have the right to add new participants", userId));
        }
        if (!teamMemberJpaRepository.findByUserIdAndProjectId(
                teamMemberCreateDto.userId(), projectId).getRoles().isEmpty()){
            throw new DataValidationException(String.format("This member %d is already a project participant." +
                    " Please update their roles instead.", teamMemberCreateDto.userId()));
        }
        TeamMember teamMember = teamMemberMapper.toTeamMember(teamMemberCreateDto);
        teamMember.setId(teamMemberCreateDto.userId());
        teamMember.setRoles(teamMemberCreateDto.roles());
        teamMemberJpaRepository.save(teamMember);
        log.info("Successfully added team member: {}", teamMember);
        return teamMemberMapper.toTeamMemberDto(teamMember);
    }

    @Override
    public TeamMemberDto updateTeamMember(long teamId, long teamMemberId, TeamMemberUpdateDto teamMemberUpdateDto) {
        log.info("Attempting to update teamMemberDto: {}", teamMemberUpdateDto);
        long userId = userContext.getUserId();
        checkIfUserExists(userId);
        Team team = teamRepository.findById(teamId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team doesn't exist by id: %s", teamId)));
        long projectId = team.getProject().getId();
        TeamMember user = teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);
        if (!user.getRoles().contains(TeamRole.TEAMLEAD)) {
            throw new DataValidationException("Only teamlead can update the team member");
        }
        TeamMember teamMember = teamMemberJpaRepository.findByUserIdAndProjectId(teamMemberId, projectId);
        if (!teamMember.getRoles().isEmpty()) {
            teamMember.setRoles(teamMemberUpdateDto.roles());
        }
        teamMemberJpaRepository.save(teamMember);
        log.info("Successfully updated team member: {}", teamMember);
        return teamMemberMapper.toTeamMemberDto(teamMember);
    }

    @Override
    public void deleteTeamMember(long teamMemberId) {
        log.info("Attempting to delete team member with id: {}", teamMemberId);
        teamMemberJpaRepository.deleteById(teamMemberId);
        log.info("Successfully deleted team member with id: {}", teamMemberId);
    }

    @Override
    public List<TeamMemberDto> getTeamMembersByFilter(long teamId, TeamFilterDto filters) {
        log.debug("Getting team members by id: {} and filter: {}", teamId, filters);
        Stream<TeamMember> teamMembers = teamMemberJpaRepository.findAll().stream()
                .filter(teamMember -> teamMember.getTeam().getId().equals(teamId));
        return teamMemberFilters.stream()
                .filter(teamMemberFilter -> teamMemberFilter.isApplicable(filters))
                .reduce(teamMembers,
                        (currentStream, teamMemberFilter) -> teamMemberFilter.apply(teamMembers, filters),
                        (s1, s2) -> s1)
                .map(teamMemberMapper::toTeamMemberDto)
                .toList();
    }

    @Override
    public Page<TeamMemberDto> getAllTeamMembers(Pageable pageable) {
        log.debug("Getting all team members");
        Page<TeamMember> teamMembers = teamMemberJpaRepository.findAll(pageable);
        log.debug("Total team members found: {}", teamMembers.getTotalElements());
        return teamMembers.map(teamMemberMapper::toTeamMemberDto);
    }

    @Override
    public TeamMemberDto getTeamMemberById(long teamMemberId) {
        log.debug("Getting team member with id: {}", teamMemberId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);
        return teamMemberMapper.toTeamMemberDto(teamMember);
    }

    private void checkIfUserExists(long userId) {
        log.info("Validating user with id: {}", userId);
        userServiceClient.getUser(userId);
    }
}
