package faang.school.projectservice.dto;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CampaignDto {
    private Long id;
    @NotNull(message = "Title is required")
    private String title;
    @NotNull(message = "Description is required")
    private String description;
    private Long projectId;
    private CampaignStatus status;
    private Currency currency;
    @NotNull(message = "Creating user is required")
    private Long createdBy;
    private Long updatedBy;
}
