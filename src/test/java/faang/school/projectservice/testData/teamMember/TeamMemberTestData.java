package faang.school.projectservice.testData.teamMember;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static faang.school.projectservice.model.TeamRole.INTERN;

public class TeamMemberTestData {
    private final List<TeamMember> interns = new ArrayList<>();
    private final Team internshipTeam = new Team();

    public TeamMemberTestData() {
        internshipTeam.setTeamMembers(new ArrayList<>());

        for (long i = 1L; i < 4L; i++) {
            var intern = new TeamMember();
            intern.setId(i);

            List<TeamRole> roles = new ArrayList<>();
            roles.add(INTERN);

            intern.setRoles(roles);

            List<Task> tasks = new ArrayList<>();
            tasks.add(Task.builder().status(TaskStatus.DONE).build());
            intern.setStages(List.of(Stage.builder().tasks(tasks).build()));

            intern.setTeam(internshipTeam);
            internshipTeam.getTeamMembers().add(intern);

            interns.add(intern);
        }
    }

    public List<TeamMember> getInterns() {
        return interns;
    }

    public Team getInternshipTeam() {
        return internshipTeam;
    }
}
