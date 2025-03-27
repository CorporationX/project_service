package faang.school.projectservice.service;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class Promotion {
    private final TeamMemberJpaRepository teamMemberJpaRepository;

    public List<TeamMember> promoteSucceededInterns(Internship internship, List<Long> succeededUserIds) {
        return succeededUserIds.stream()
                .map(id -> teamMemberJpaRepository.findByUserIdAndProjectId(id, internship.getProject().getId()))
                .peek(teamMember -> {
                    teamMember.getRoles().remove(TeamRole.INTERN);
                    teamMember.getRoles().add(internship.getRole());
                })
                .toList();
    }

    public List<TeamMember> demoteFailedInterns(Internship internship, List<Long> failedUserIds) {
        return failedUserIds.stream()
                .map(id -> teamMemberJpaRepository.findByUserIdAndProjectId(id, internship.getProject().getId()))
                .peek(teamMember -> {
                    teamMember.getRoles().remove(TeamRole.INTERN);
                    deleteTeamMemberFromProjectTeam(teamMember);
                })
                .toList();
    }

    private void deleteTeamMemberFromProjectTeam(TeamMember teamMember) {
        teamMember.getTeam().getTeamMembers().remove(teamMember);
        teamMember.setTeam(null); // I am not sure how should I delete intern, who failed, from its team.
    }

}
