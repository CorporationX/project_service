package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CampaignFilterDto {
    private LocalDate createdAt;
    private CampaignStatus status;
    private Long createdBy;
}
