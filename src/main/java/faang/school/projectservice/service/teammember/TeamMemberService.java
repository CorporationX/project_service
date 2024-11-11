package faang.school.projectservice.service.teammember;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.teammember.AddTeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.UpdateTeamMemberDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.teammember.TeamMemberMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberMapper teamMemberMapper;

    private final UserContext userContext;

    private final List<Filter<TeamMemberFilterDto, TeamMember>> teamMemberFilters;

    @Transactional
    public TeamMemberDto addTeamMember(AddTeamMemberDto addTeamMemberDto) {
        Long userId = userContext.getUserId();
        TeamMember userWithAccessToAddTeamMember = findTeamMember(userId);

        if (!userWithAccessToAddTeamMember.getRoles().contains(TeamRole.OWNER) &&
                !userWithAccessToAddTeamMember.getRoles().contains(TeamRole.TEAMLEAD)) {
            throw new IllegalArgumentException("Only the owner or TEAMLEAD can add new members to the project");
        }

        checkIfMemberExists(addTeamMemberDto);

        TeamMember newMember = teamMemberMapper.toEntity(addTeamMemberDto);

        TeamMember savedMember = teamMemberRepository.save(newMember);
        return teamMemberMapper.toDto(savedMember);
    }

    private void checkIfMemberExists(AddTeamMemberDto addTeamMemberDto) {
        Optional<TeamMember> existingMember = teamMemberRepository.findByUserIdAndProjectId(
                addTeamMemberDto.getUserId(),
                addTeamMemberDto.getProjectId());

        if (existingMember.isPresent() && existingMember.get().getUserId().equals(addTeamMemberDto.getUserId())) {
            throw new IllegalArgumentException("The member with userId: " +
                    addTeamMemberDto.getUserId() + " already exists.");
        }
    }

    @Transactional
    public TeamMemberDto updateTeamMember(Long id, UpdateTeamMemberDto teamMemberDto) {
        long updaterUserId = userContext.getUserId();
        TeamMember updater = findTeamMember(updaterUserId);
        TeamMember teamMember = findTeamMember(id);

        List<TeamRole> roles = teamMemberDto.getRoles();
        String nickName = teamMemberDto.getNickname();

        if (!roles.isEmpty() && !teamMember.getRoles().contains(TeamRole.TEAMLEAD)) {
            throw new IllegalArgumentException("Only TEAMLEAD can updateCampaign roles and permissions");
        }
        if (!nickName.isEmpty() && !id.equals(updater.getId())) {
            throw new IllegalArgumentException("Nickname can be edited only by yourself");
        }

        if (!roles.isEmpty() && !teamMember.getRoles().contains(TeamRole.TEAMLEAD)) {
            teamMember.setRoles(teamMemberDto.getRoles());
        }
        if (!nickName.isEmpty() && id.equals(updater.getId())) {
            teamMember.setNickname(teamMemberDto.getNickname());
        }
        teamMember.setLastModified(LocalDateTime.now());
        TeamMember updatedMember = teamMemberRepository.save(teamMember);
        return teamMemberMapper.toDto(updatedMember);
    }

    @Transactional
    public void removeTeamMember(Long projectId, Long memberId) {
        long ownerId = userContext.getUserId();
        TeamMember owner = findTeamMember(ownerId);
        Long ownerProjectId = owner.getTeam().getProject().getId();

        if (!owner.getRoles().contains(TeamRole.OWNER) || ownerProjectId != projectId) {
            throw new IllegalArgumentException("Only the owner can remove members from the project");
        }

        TeamMember member = findTeamMember(memberId);

        teamMemberRepository.deleteById(member.getId());
    }

    public List<TeamMemberDto> findTeamMembers(@NotNull TeamMemberFilterDto filterDto) {
        Stream<TeamMember> members = teamMemberRepository.findAll().stream();

        return teamMemberFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(
                        members,
                        (stream, filter) -> filter.applyFilter(stream, filterDto),
                        (m1, m2) -> m1)
                .map(teamMemberMapper::toDto)
                .toList();
    }

    public List<TeamMemberDto> getAllTeamMembers() {
        return teamMemberMapper.toDtos(teamMemberRepository.findAll());
    }

    public TeamMemberDto getTeamMemberById(Long id) {
        TeamMember teamMember = findTeamMember(id);
        return teamMemberMapper.toDto(teamMember);
    }

    private TeamMember findTeamMember(Long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team member with id " + id + " does not exist!"));
    }

}
