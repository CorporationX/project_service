package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignFilterDto {
    private LocalDate createdAt;
    private CampaignStatus status;
    @Min(1)
    private Long createdBy;
}
