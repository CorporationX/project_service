package faang.school.projectservice.dto.client.Campaign;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampaignDto {

    private Long id;

    @NotBlank(message = "Campaign title cannot be empty")
    @Size(max = 128, message = "Title cannot exceed 128 characters")
    private String title;

    @NotBlank(message = "Campaign description cannot be empty")
    @Size(max = 4096, message = "description cannot exceed 4096 characters")
    private String description;

    @NotNull(message = "Fundraising goal is required")
    @Positive(message = "Fundraising goal must be greater than 0")
    private BigDecimal goal;

    @NotNull(message = "Fundraising goal is required")
    private BigDecimal amountRaised;

    @NotNull(message = "Currency is required")
    private Currency currency;

    private CampaignStatus status;
    private Long projectId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long updatedBy;
}
