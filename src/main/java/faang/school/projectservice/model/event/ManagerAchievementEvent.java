package faang.school.projectservice.model.event;

import lombok.Builder;

@Builder
public record ManagerAchievementEvent(
        long projectId,
        long authorId,
        long teamId
) {
}
