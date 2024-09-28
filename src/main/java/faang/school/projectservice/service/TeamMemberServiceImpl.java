package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.teamfilter.TeamMemberFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final List<TeamMemberFilter> teamMemberFilters;

    @Override
    public TeamMemberDto addTeamMember(long userId, TeamMemberDto teamMemberDto) {
        log.info("Attempting to add a team member with userId: {} and teamMemberDto: {}", userId, teamMemberDto);
        validateUser(userId);
        TeamMember user = teamMemberRepository.findById(userId);
        if (!(user.getRoles().contains(TeamRole.TEAMLEAD) ||
                user.getRoles().contains(TeamRole.OWNER))) {
            String message = String.format("User with id %d doesn't have the right to add new participants", userId);
            log.error(message);
            throw new DataValidationException(message);
        }
        if (!teamMemberDto.role().isEmpty()){
            String message = String.format("This member %d is already a project participant." +
                    " Please update their role instead.", teamMemberDto.teamMemberId());
            log.error(message);
            throw new DataValidationException(message);
        }
        TeamMember teamMember = TeamMember.builder()
                .id(teamMemberDto.teamMemberId())
                .roles(teamMemberDto.role())
                .build();
        teamMemberJpaRepository.save(teamMember);
        log.info("Successfully added team member: {}", teamMember);
        return teamMemberMapper.toTeamMemberDto(teamMember);
    }

    @Override
    public TeamMemberDto updateTeamMember(long userId, TeamMemberDto teamMemberDto) {
        log.info("Attempting to update teamMemberDto: {}", teamMemberDto);
        validateUser(userId);
        if (!teamMemberRepository.findById(userId).getRoles().contains(TeamRole.TEAMLEAD)) {
            String message = "Only teamlead can update the team member";
            log.error(message);
            throw new DataValidationException(message);
        }
        TeamMember teamMember = teamMemberMapper.toTeamMember(teamMemberDto);
        if (!teamMember.getRoles().isEmpty()) {
            teamMember.setRoles(teamMemberDto.role());
        }
        teamMemberJpaRepository.save(teamMember);
        log.info("Successfully updated team member: {}", teamMember);
        return teamMemberMapper.toTeamMemberDto(teamMember);
    }

    @Override
    public void deleteTeamMember(long id) {
        log.info("Attempting to delete team member with id: {}", id);
        teamMemberJpaRepository.delete(teamMemberRepository.findById(id));
        log.info("Successfully deleted team member with id: {}", id);
    }

    @Override
    public List<TeamMemberDto> getTeamMembersByFilter(TeamFilterDto filters) {
        log.info("Getting team members by filter: {}", filters);
        Stream<TeamMember> teamMembers = teamMemberJpaRepository.findAll().stream();
        return teamMemberFilters.stream()
                .filter(teamMemberFilter -> teamMemberFilter.isApplicable(filters))
                .reduce(teamMembers,
                        (currentStream, teamMemberFilter) -> teamMemberFilter.apply(teamMembers, filters),
                        (s1, s2) -> s1)
                .map(teamMemberMapper::toTeamMemberDto)
                .toList();
    }

    @Override
    public List<TeamMemberDto> getAllTeamMembers() {
        log.info("Getting all team members");
        List<TeamMember> teamMembers = teamMemberJpaRepository.findAll();
        log.info("Total team members found: {}", teamMembers.size());
        return teamMemberMapper.toTeamMemberDtos(teamMembers);
    }

    @Override
    public TeamMemberDto getTeamMemberById(long teamMemberId) {
        log.info("Getting team member with id: {}", teamMemberId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);
        return teamMemberMapper.toTeamMemberDto(teamMember);
    }

    private void validateUser(long userId) {
        log.info("Validating user with id: {}", userId);
        userServiceClient.getUser(userId);
    }
}
