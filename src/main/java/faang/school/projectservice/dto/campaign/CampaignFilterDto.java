package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.model.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CampaignFilterDto extends FilterDto {
    private LocalDateTime createdAt;
    private CampaignStatus status;
    private Long createdBy;
}
