package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.validator.TeamMemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final UserServiceClient userServiceClient;
    private final TeamMemberMapper teamMemberMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberValidator teamMemberValidator;

    public TeamMemberDto addMember(TeamMemberDto dto, Long projectId, Long requesterId) {
        userServiceClient.getUser(dto.getUserId());
        userServiceClient.getUser(requesterId);

        teamMemberValidator.validateLeaderOrOwner(teamMemberRepository.findByUserIdAndProjectId(requesterId, projectId));
        teamMemberValidator.validateAlreadyInProject(dto.getUserId(), projectId);

        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new EntityNotFoundException("Команда с id " + dto.getTeamId()+ " не найдена"));

        TeamMember newMember = teamMemberMapper.teamMemberDtoToTeamMember(dto);
        newMember.setTeam(team);
        return teamMemberMapper.teamMemberToTeamMemberDto(teamMemberRepository.save(newMember));
    }

    public TeamMemberDto updateMember(TeamMemberDto teamMemberDto, Long requesterId, Long projectId) {
        userServiceClient.getUser(teamMemberDto.getUserId());
        userServiceClient.getUser(requesterId);

        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(teamMemberDto.getUserId(), projectId);

        teamMemberValidator.isTeamMemberExist(teamMember);

        if (teamMemberValidator.isMemberLeader(teamMember)) {
            teamMember.setNickname(teamMemberDto.getNickname());
            teamMember.setRoles(teamMemberDto.getRoles());
        }
        else {
            teamMember.setNickname(teamMemberDto.getNickname());
        }

        TeamMember updatedMember = teamMemberRepository.save(teamMember);
        return teamMemberMapper.teamMemberToTeamMemberDto(updatedMember);
    }

    public void removeMember(Long memberId, Long requesterId, Long projectId) {
        if(teamMemberValidator.isMemberOwner(teamMemberRepository.findByUserIdAndProjectId(requesterId, projectId))) {
            teamMemberRepository.deleteById(memberId);
        } else {
            throw new BusinessException("Удалить пользователя может только владелец проекта");
        }
    }

    public List<TeamMemberDto> getProjectMembers(Long projectId, String role, String name) {
        List<TeamMember> members = teamMemberRepository.findByProjectId(projectId);

        Optional<TeamRole> optionalRole;
        if (role != null) {
            optionalRole = Arrays.stream(TeamRole.values())
                    .filter(r -> r.name().equalsIgnoreCase(role))
                    .findFirst();

            if (optionalRole.isEmpty()) {
                throw new BusinessException("Некорректная роль: " + role);
            }
        } else {
            optionalRole = Optional.empty();
        }

        members = members.stream()
                .filter(member -> optionalRole.isEmpty() || member.getRoles().contains(optionalRole.get()))
                .filter(member -> name == null || member.getNickname().toLowerCase().contains(name.toLowerCase()))
                .toList();

        return teamMemberMapper.teamMemberListToTeamMemberDtoList(members);
    }


    public List<TeamMemberDto> getAllMembers() {
        return teamMemberMapper.teamMemberListToTeamMemberDtoList(teamMemberRepository.findAll());
    }

    public TeamMemberDto getMemberById(Long memberId, Long projectId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(projectId, memberId);
        return teamMemberMapper.teamMemberToTeamMemberDto(teamMember);
    }
}
