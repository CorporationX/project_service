package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampaignFilterDto {
    private CampaignStatus status;
    private Long projectId;
    private Long createdBy;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
}
