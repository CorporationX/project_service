package faang.school.projectservice.dto.project.stats;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Aggregated statistics related to a project")
public record ProjectStatsDto(

        @Schema(description = "Total number of tasks")
        int totalTasks,

        @Schema(description = "Number of completed tasks")
        int completedTasks,

        @Schema(description = "Number of active (in-progress) tasks")
        int activeTasks,

        @Schema(description = "Number of cancelled tasks")
        int cancelledTasks,

        @Schema(description = "Total number of teams")
        int totalTeams,

        @Schema(description = "Total number of team members")
        int totalMembers,

        @Schema(description = "Project creation date")
        String createdAt,

        @Schema(description = "Days passed since the project was created")
        long daysSinceCreation,

        @Schema(description = "Last project update timestamp")
        String lastUpdated
) {
}
