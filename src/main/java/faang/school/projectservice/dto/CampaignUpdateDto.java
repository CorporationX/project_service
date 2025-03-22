package faang.school.projectservice.dto;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CampaignUpdateDto {
    @NotNull(message = "ID is required")
    private Long id;
    private String title;
    private String description;
    private Long projectId;
    private Long updatedBy;
    private CampaignStatus status;
    private BigDecimal goal;
    private Currency currency;
    private Long createdBy;
}
