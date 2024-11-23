package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember getTeamMemberByUserId(Long userId) {
        return teamMemberRepository.findById(userId);
    }

    public boolean existsById(Long userId) {
        return teamMemberRepository.existsById(userId);
    }

    public List<TeamMember> getProjectParticipantsWithRole(Project project, String role) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> teamMember.getRoles().stream()
                        .anyMatch(teamRole -> teamRole.toString().equalsIgnoreCase(role)))
                .toList();
    }
}
