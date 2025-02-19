package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class CampaignFilterDto {

    Long createdBy;
    LocalDateTime createdAt;
    CampaignStatus status;

}
