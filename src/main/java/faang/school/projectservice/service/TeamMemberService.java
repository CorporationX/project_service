package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.dto.team.TeamMemberFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.filter.team.TeamMemberFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberMapper teamMemberMapper;
    private final UserServiceClient userServiceClient;
    private final List<TeamMemberFilter> teamMemberFilter;

    public TeamMemberDto addMember(TeamMemberDto memberDto, Long requesterId) {

        TeamMember requester = getRequester(memberDto.teamId(), requesterId, List.of(TeamRole.OWNER, TeamRole.MANAGER));

        userServiceClient.getUser(memberDto.userId());

        TeamMember newMember = teamMemberMapper.toEntity(memberDto);
        newMember.setTeam(requester.getTeam());
        newMember = teamMemberRepository.save(newMember);

        return teamMemberMapper.toDto(newMember);
    }

    public TeamMemberDto updateMember(Long memberId, TeamMemberDto updatedDto, Long requesterId) {

        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        if (!requesterId.equals(member.getUserId())) {
            getRequester(updatedDto.teamId(), requesterId, List.of(TeamRole.MANAGER));
        }

        member.setNickname(updatedDto.nickname());
        member.setRoles(updatedDto.roles());

        TeamMember updatedMember = teamMemberRepository.save(member);
        return teamMemberMapper.toDto(updatedMember);
    }

    public void removeMember(Long teamId, Long memberId, Long requesterId) {
        getRequester(teamId, requesterId, List.of(TeamRole.OWNER));
        teamMemberRepository.deleteById(memberId);
    }

    public List<TeamMemberDto> getAllMembers(Long teamId, TeamMemberFilterDto filterDto) {
        return teamMemberFilter.stream()
                .filter(filter -> filter.isAcceptable(filterDto))
                .flatMap(filter -> teamMemberRepository.findByTeamId(teamId).stream())
                .map(teamMemberMapper::toDto)
                .toList();
    }

    public TeamMemberDto getMember(Long memberId) {
        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found by id %s".formatted(memberId)));
        return teamMemberMapper.toDto(member);
    }

    private TeamMember getRequester(Long teamId, Long requesterId, List<TeamRole> allowedRoles) {
        TeamMember requester = teamMemberRepository.findByUserIdAndTeamId(requesterId, teamId)
                .orElseThrow(() -> new EntityNotFoundException("Requester not found in the team"));

        if (requester.getRoles().stream().noneMatch(allowedRoles::contains)) {
            log.info("Requester {} is not allowed to perform this operation", requesterId);
            throw new RuntimeException("Access denied");
        }
        return requester;
    }
}
