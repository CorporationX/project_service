package faang.school.projectservice.service.presentation.stats;

import faang.school.projectservice.dto.project.stats.ProjectStatsDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Service
public class ProjectPresentationStatsServiceImpl implements ProjectPresentationStatsService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public ProjectStatsDto calculate(Project project) {
        List<Task> tasks = project.getTasks() != null ? project.getTasks() : Collections.emptyList();
        List<Team> teams = project.getTeams() != null ? project.getTeams() : Collections.emptyList();
        return new ProjectStatsDto(
                tasks.size(),
                countTasksByStatus(tasks, TaskStatus.DONE),
                countActiveTasks(tasks),
                countTasksByStatus(tasks, TaskStatus.CANCELLED),
                teams.size(),
                countTeamMembers(teams),
                formatDate(project.getCreatedAt()),
                calculateDaysSinceCreation(project.getCreatedAt()),
                formatDate(project.getUpdatedAt())
        );
    }

    private int countTasksByStatus(List<Task> tasks, TaskStatus status) {
        return Math.toIntExact(tasks.stream()
                .filter(task -> task.getStatus() == status)
                .count());
    }

    private int countActiveTasks(List<Task> tasks) {
        return Math.toIntExact(tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE && task.getStatus() != TaskStatus.CANCELLED)
                .count());
    }

    private int countTeamMembers(List<Team> teams) {
        return teams.stream()
                .mapToInt(team -> team.getTeamMembers().size())
                .sum();
    }

    private long calculateDaysSinceCreation(LocalDateTime createdAt) {
        return ChronoUnit.DAYS.between(createdAt.toLocalDate(), LocalDate.now());
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }
}
