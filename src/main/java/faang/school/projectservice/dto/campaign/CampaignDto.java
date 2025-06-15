package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Data
public class CampaignDto {
    @NotNull(message = "Campaign ID is required")
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 128, message = "Title must be at most 128 characters")
    private String title;

    @Size(max = 4096, message = "Description must be at most 4096 characters")
    private String description;

    @NotNull(message = "Goal amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Goal must be positive")
    private BigDecimal goal;
    private BigDecimal amountRaised;
    private CampaignStatus status;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotNull(message = "Currency is required")
    private Currency currency;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
}

