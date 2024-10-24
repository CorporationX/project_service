package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignUpdateDto {

    @NotNull
    @Positive
    private Long id;

    @Positive
    private Long updatedBy;

    private String title;
    private String description;

}
