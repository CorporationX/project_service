package faang.school.projectservice.dto;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotBlank;
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
public class CampaignDto {
    private Long id;
    @NotNull
    @NotBlank
    private String title;
    @NotNull
    private Currency currency;
    @NotNull
    @Positive
    private Long goal;
    @NotNull
    @Positive
    private Long projectId;
}
