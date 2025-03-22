package faang.school.projectservice.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CampaignUpdateDto {
    private Long id;
    private String title;
    private String description;
    private Long updatedBy;
}
