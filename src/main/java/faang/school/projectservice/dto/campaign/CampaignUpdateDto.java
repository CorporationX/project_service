package faang.school.projectservice.dto.campaign;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampaignUpdateDto {

    @NotNull
    @Min(1L)
    public
    Long id;
    String title;
    String description;

}
