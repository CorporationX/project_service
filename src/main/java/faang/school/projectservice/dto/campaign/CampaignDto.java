package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class CampaignDto {
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Goal amount is required")
    @DecimalMin(value = "0.01", message = "Goal must be at least 0.01")
    private BigDecimal goal;

    @DecimalMin(value = "0.00", message = "Amount raised cannot be negative")
    private BigDecimal amountRaised;

    @NotNull(message = "Status is required")
    private CampaignStatus status;

    @NotNull(message = "Project is required")
    private Project project;

    @NotNull(message = "Currency is required")
    private Currency currency;

    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
}
