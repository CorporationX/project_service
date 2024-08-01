package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignUpdateDto {

    @Size(max = 128)
    private String title;

    @Size(max = 4096)
    private String description;

    @Min(1)
    private BigDecimal goal;

    private CampaignStatus status;
}
