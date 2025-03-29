package faang.school.projectservice.dto.campaign;

public record CampaignDto(
        Long id,
        String title,
        String description,
        String goal,
        String amountRaised,
        String status,
        Long projectId,
        String currency,
        String createdAt,
        Long createdBy,
        String updatedAt,
        Long updatedBy
) {
}
