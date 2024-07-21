package faang.school.projectservice.service;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

import static faang.school.projectservice.exception.InternshipError.ABSENT_INTERN_ROLE_EXCEPTION;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberJpaRepository  teamMemberJpaRepository;

    public void changeRoleForInternsAndDeleteFiredInterns(Internship internship) {
        List<TeamMember> interns = internship.getInterns();
        List<TeamMember> internsToBeChanged = interns.stream().filter(conditionForAllTaskDone()).toList();

        interns.removeAll(internsToBeChanged);
        interns.forEach(firedIntern -> firedIntern.getTeam().getTeamMembers().remove(firedIntern));

        interns.clear();

        changeRole(internsToBeChanged);
    }

    private Predicate<TeamMember> conditionForAllTaskDone() {
        return teamMember -> teamMember.getStages().stream()
               .flatMap(stage -> stage.getTasks()
                       .stream())
               .anyMatch(task -> task.getStatus().equals(TaskStatus.DONE));
    }

    private void changeRole(List<TeamMember> internships) {
        internships.forEach(intern -> {
            List<TeamRole> roles = intern.getRoles();
            if (!roles.contains(TeamRole.INTERN))
                throw new DataValidationException(ABSENT_INTERN_ROLE_EXCEPTION);
            if (roles.size() == 1)
                intern.getRoles().add(TeamRole.DEVELOPER);

            teamMemberJpaRepository.save(intern);
        });
    }
}
