package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateCampaignDto {
    private String title;
    private String description;
    @Positive(message = "Goal must be greater than 0")
    private BigDecimal goal;
    private CampaignStatus status;
}
