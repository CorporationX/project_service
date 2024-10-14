package faang.school.projectservice.service.teammember;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.teammember.AddTeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.teammember.TeamMemberMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
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
        
        if (
            !userWithAccessToAddTeamMember.getRoles().contains(TeamRole.OWNER) && 
            !userWithAccessToAddTeamMember.getRoles().contains(TeamRole.TEAMLEAD)
        ) {
            throw new IllegalArgumentException("Only the owner or TEAMLEAD can add new members to the project");
        }

        List<TeamMember> existingMembers = teamMemberRepository.findAll();
        for (TeamMember member : existingMembers) {
            if (member.getUserId().equals(addTeamMemberDto.getUserId())) {
                member.setRoles(addTeamMemberDto.getRoles());
                TeamMember updatedMember = teamMemberRepository.save(member);
                return teamMemberMapper.toDto(updatedMember);
            }
        }

        TeamMember newMember = TeamMember.builder()
                .userId(addTeamMemberDto.getUserId())
                .roles(addTeamMemberDto.getRoles())
                .nickname(addTeamMemberDto.getNickname())
                .build();

        TeamMember savedMember = teamMemberRepository.save(newMember);
        return teamMemberMapper.toDto(savedMember);
    }

    @Transactional
    public TeamMemberDto updateTeamMember(TeamMemberDto teamMemberDto) {
        long updaterUserId = userContext.getUserId();
        TeamMember updater = findTeamMember(updaterUserId);
        TeamMember teamMember = findTeamMember(teamMemberDto.getUserId());

        if (updater.getUserId().equals(teamMemberDto.getUserId())) {
            teamMember.setNickname(teamMemberDto.getNickname());
        } else if (updater.getRoles().contains(TeamRole.TEAMLEAD)) {
            teamMember.setRoles(teamMemberDto.getRoles());
        } else {
            throw new IllegalArgumentException("Only TEAMLEAD can update roles and permissions");
        }

        teamMember.setLastModified(LocalDateTime.now());
        TeamMember updatedMember = teamMemberRepository.save(teamMember);
        return teamMemberMapper.toDto(updatedMember);
    }

    @Transactional
    public void removeTeamMember(Long ownerId, Long memberId) {
        TeamMember owner = findTeamMember(ownerId);
        
        if (!owner.getRoles().contains(TeamRole.OWNER)) {
            throw new IllegalArgumentException("Only the owner can remove members from the project");
        }

        TeamMember member = findTeamMember(memberId);
        
        teamMemberRepository.deleteById(member.getId());
    }

    public List<TeamMemberDto> findTeamMembers(TeamMemberFilterDto filterDto) {
        if (filterDto == null) {
            throw new IllegalArgumentException("Filter can't be null!");
        }

        Stream<TeamMember> members = teamMemberRepository.findAll().stream();

        return teamMemberFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(
                    members, 
                    (stream, filter) -> filter.applyFilter(stream, filterDto), 
                    (m1, m2) -> m1
                )
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
