package faang.school.projectservice.dto.campaign;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampaignDto {

    private long id;

    @NotNull(message = "Title must not be null")
    private String title;
}
