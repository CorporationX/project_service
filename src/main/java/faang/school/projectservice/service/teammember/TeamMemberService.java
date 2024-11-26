package faang.school.projectservice.service.teammember;

import faang.school.projectservice.exception.RoleProcessingException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static faang.school.projectservice.exception.RoleProcessingExceptionMessage.ABSENT_INTERN_ROLE_EXCEPTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public Optional<TeamMember> findTeamMember(Long id){
        if (id == null){
            throw new IllegalArgumentException("Project not found");
        }

        return Optional.ofNullable(teamMemberRepository.findById(id));
    }

    public void hireInterns(Internship internship) {
        List<TeamMember> interns = internship.getInterns();
        List<TeamMember> internsToBeHired = interns.stream()
                .filter(allTasksDonePredicate())
                .toList();

        interns.removeAll(internsToBeHired);

        interns.forEach(firedIntern -> firedIntern.getTeam().getTeamMembers().remove(firedIntern));
        interns.clear();

        assignRolesToInterns(internsToBeHired);
    }

    private void assignRolesToInterns(List<TeamMember> interns) {
        interns.forEach(intern -> {
            List<TeamRole> internRoles = intern.getRoles();

            if (!internRoles.contains(TeamRole.INTERN)) {
                throw new RoleProcessingException(ABSENT_INTERN_ROLE_EXCEPTION.getMessage());
            }

            if (internRoles.size() == 1) {
                internRoles.add(TeamRole.DEVELOPER);
            }

            internRoles.remove(TeamRole.INTERN);
        });

        interns.forEach(teamMemberRepository::save);
    }

    private Predicate<TeamMember> allTasksDonePredicate() {
        return teamMember -> {
            var allTasks = teamMember.getStages().stream()
                    .flatMap(stage -> stage.getTasks().stream())
                    .toList();

            var doneTasks = allTasks.stream()
                    .filter(task -> task.getStatus().equals(TaskStatus.DONE))
                    .toList();

            return doneTasks.size() == allTasks.size();
        };
    }
}
