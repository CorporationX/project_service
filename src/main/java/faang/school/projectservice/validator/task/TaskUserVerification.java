package faang.school.projectservice.validator.task;

import faang.school.projectservice.model.Task;

public class TaskUserVerification {

    public static void userVerification(Long userId, Task task) {
        if(!task.getProject().getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getUserId().equals(userId))){
            throw new IllegalArgumentException("User is not a participant in the project");
        }
    }
}
