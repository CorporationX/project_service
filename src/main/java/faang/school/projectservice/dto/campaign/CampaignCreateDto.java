package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CampaignCreateDto {
    @NotNull(message = "Title is required")
    @Size(max = 128, message = "Title should not be greater than 128 characters")
    private String title;
    @NotNull(message = "Description is required")
    @Size(max = 4096, message = "Description should not be greater than 4096 characters")
    private String description;
    @NotNull(message = "Project is required")
    private Long projectId;
    @NotNull(message = "User is required")
    private Long updatedBy;
    private CampaignStatus status;
    private BigDecimal goal;
    private Currency currency;
    private Long createdBy;

}
